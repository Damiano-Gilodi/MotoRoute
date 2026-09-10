import {
  http,
  HttpResponse,
} from "msw";
import {
  describe,
  expect,
  test,
} from "vitest";

import {server} from "../../../test/server.js";
import {deleteRoute} from "./deleteRouteApi.js";
import {ApiRequestError} from "../api/ApiRequestError.js";

const routeId =
  "11111111-1111-1111-1111-111111111111";

const apiUrl = new URL(
  `/api/routes/${routeId}`,
  window.location.origin,
).toString();

async function getRejectedError(promise) {
  try {
    await promise;
  } catch (error) {
    return error;
  }

  throw new Error("Expected the promise to reject");
}

describe("deleteRoute", () => {
  test("should delete the route", async () => {

      server.use(
        http.delete(apiUrl, ({request}) => {
            const requestUrl = new URL(request.url);

            expect(requestUrl.pathname).toBe(`/api/routes/${routeId}`);
            expect(request.headers.get("Accept")).toBe("application/json");

            return new HttpResponse(
              null,
              {status: 204,},
            );
          },
        ),
      );

      const result = await deleteRoute({routeId,});

      expect(result).toBeNull();
    },
  );

  test("should throw a structured API error when route does not exist", async () => {
      server.use(
        http.delete(apiUrl, () =>
          HttpResponse.json(
            {
              timestamp: "2026-07-29T10:00:00Z",
              status: 404,
              error: "Not Found",
              message: "Route not found with id: " + routeId,
              path: `/api/routes/${routeId}`,
              fieldErrors: {},
            },
            {
              status: 404,
            },
          )
        ),
      );

      const error = await getRejectedError(
        deleteRoute({routeId}),
      );

      expect(error).toBeInstanceOf(ApiRequestError);

      expect(error).toMatchObject({
        name: "ApiRequestError",
        status: 404,
        message: "Route not found with id: " + routeId,
        fieldErrors: {},
      });
    },
  );

  test("should use fallback message when server returns a non-JSON error", async () => {
      server.use(
        http.delete(apiUrl, () =>
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

      const error = await getRejectedError(
        deleteRoute({routeId}),
      );

      expect(error).toBeInstanceOf(ApiRequestError);

      expect(error).toMatchObject({
        name: "ApiRequestError",
        status: 500,
        message: "Impossibile cancellare l’itinerario",
        fieldErrors: {},
      });
    },
  );
});
