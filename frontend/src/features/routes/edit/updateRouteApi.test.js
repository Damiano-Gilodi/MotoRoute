import {http, HttpResponse} from "msw";
import {
  describe,
  expect,
  test,
} from "vitest";

import {server} from "../../../test/server.js";
import {ApiRequestError} from "../api/ApiRequestError.js";
import {updateRoute} from "./updateRouteApi.js";

const routeId =
  "11111111-1111-1111-1111-111111111111";

const apiUrl = new URL(
  `/api/routes/${routeId}`,
  window.location.origin,
).toString();

const routeUpdated = {
  id: routeId,
  name: "Passo dello Stelvio",
  description: "Percorso panoramico",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: 40.5,
  difficulty: "MEDIUM",
  createdAt: "2026-07-29T10:00:00Z",
  updatedAt: "2026-07-29T10:30:00Z",
};

const validPayload = {
  name: "Passo dello Stelvio",
  description: "Percorso panoramico",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: 40.5,
  difficulty: "MEDIUM",
};

async function getRejectedError(promise) {
  try {
    await promise;
  } catch (error) {
    return error;
  }

  throw new Error(
    "Expected the promise to reject",
  );
}

describe("updateRoute", () => {
  test("should send the updated route payload and return the updated route", async () => {
      let receivedPayload;

      server.use(
        http.put(apiUrl, async ({request}) => {
          receivedPayload =
            await request.json();

          const requestUrl =
            new URL(request.url);

          expect(requestUrl.pathname)
            .toBe(`/api/routes/${routeId}`);

          expect(
            request.headers.get("Accept"),
          ).toBe("application/json");

          expect(
            request.headers.get(
              "Content-Type",
            ),
          ).toBe("application/json");

          return HttpResponse.json(
            routeUpdated,
            {
              status: 200,
            },
          );
        }),
      );

      const result = await updateRoute({
        routeId,
        payload: validPayload,
      });

      expect(receivedPayload)
        .toEqual(validPayload);

      expect(result)
        .toEqual(routeUpdated);
    },
  );

  test("should throw a structured API error when route does not exist", async () => {
      server.use(
        http.put(apiUrl, () =>
          HttpResponse.json(
            {
              timestamp:
                "2026-07-29T10:00:00Z",
              status: 404,
              error: "Not Found",
              message:
                "Route not found with id: "
                + routeId,
              path:
                `/api/routes/${routeId}`,
              fieldErrors: {},
            },
            {
              status: 404,
            },
          ),
        ),
      );

      const error =
        await getRejectedError(
          updateRoute({
            routeId,
            payload: validPayload,
          }),
        );

      expect(error)
        .toBeInstanceOf(ApiRequestError);

      expect(error).toMatchObject({
        name: "ApiRequestError",
        status: 404,
        message:
          "Route not found with id: "
          + routeId,
        fieldErrors: {},
      });
    },
  );

  test("should preserve field errors when update request is invalid", async () => {
      server.use(
        http.put(apiUrl, () =>
          HttpResponse.json(
            {
              timestamp:
                "2026-07-29T10:00:00Z",
              status: 400,
              error: "Bad Request",
              message:
                "One or more fields are invalid",
              path:
                `/api/routes/${routeId}`,
              fieldErrors: {
                name:
                  "must not be blank",
                distanceKm:
                  "must be greater than 0",
              },
            },
            {
              status: 400,
            },
          ),
        ),
      );

      const error =
        await getRejectedError(
          updateRoute({
            routeId,
            payload: validPayload,
          }),
        );

      expect(error)
        .toBeInstanceOf(ApiRequestError);

      expect(error).toMatchObject({
        status: 400,
        message:
          "One or more fields are invalid",
        fieldErrors: {
          name: "must not be blank",
          distanceKm:
            "must be greater than 0",
        },
      });
    },
  );

  test("should use fallback message when server returns a non-JSON error", async () => {
      server.use(
        http.put(apiUrl, () =>
          new HttpResponse(
            "Internal Server Error",
            {
              status: 500,
              headers: {
                "Content-Type":
                  "text/plain",
              },
            },
          ),
        ),
      );

      const error =
        await getRejectedError(
          updateRoute({
            routeId,
            payload: validPayload,
          }),
        );

      expect(error)
        .toBeInstanceOf(ApiRequestError);

      expect(error).toMatchObject({
        status: 500,
        message:
          "Impossibile aggiornare l’itinerario",
        fieldErrors: {},
      });
    },
  );
});
