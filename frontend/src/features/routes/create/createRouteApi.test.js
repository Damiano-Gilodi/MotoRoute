import {http, HttpResponse} from "msw";
import {describe, expect, test} from "vitest";

import {server} from "../../../test/server";
import {
  ApiRequestError,
  createRoute,
} from "./createRouteApi";

const apiUrl = new URL(
  "/api/routes",
  window.location.origin,
).toString();

const validPayload = {
  name: "Passo dello Stelvio",
  description: "Percorso panoramico",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: 47.5,
  difficulty: "HARD",
};

async function getRejectedError(promise) {
  try {
    await promise;
  } catch (error) {
    return error;
  }

  throw new Error("Expected the promise to reject");
}

describe("createRoute", () => {
  test("should send the route payload and return the created route", async () => {
    const createdRoute = {
      id: "11111111-1111-1111-1111-111111111111",
      ...validPayload,
      createdAt: "2026-07-28T10:00:00Z",
      updatedAt: "2026-07-28T10:00:00Z",
    };

    let receivedPayload;

    server.use(
      http.post(apiUrl, async ({request}) => {
        receivedPayload = await request.json();

        expect(
          request.headers.get("Content-Type"),
        ).toBe("application/json");

        return HttpResponse.json(createdRoute, {
          status: 201,
          headers: {
            Location: `/api/routes/${createdRoute.id}`,
          },
        });
      }),
    );

    const result = await createRoute(validPayload);

    expect(receivedPayload).toEqual(validPayload);
    expect(result).toEqual(createdRoute);
  });

  test("should throw a structured error when validation fails", async () => {
    server.use(
      http.post(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-28T10:00:00Z",
            status: 400,
            error: "Bad Request",
            message: "One or more fields are invalid",
            path: "/api/routes",
            fieldErrors: {
              name: "must not be blank",
            },
          },
          {
            status: 400,
          },
        ),
      ),
    );

    const error = await getRejectedError(
      createRoute(validPayload),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 400,
      message: "One or more fields are invalid",
      fieldErrors: {
        name: "must not be blank",
      },
    });
  });

  test("should use a generic message when the error response is not JSON", async () => {
    server.use(
      http.post(
        apiUrl,
        () =>
          new HttpResponse("Internal server error", {
            status: 500,
            headers: {
              "Content-Type": "text/plain",
            },
          }),
      ),
    );

    const error = await getRejectedError(
      createRoute(validPayload),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 500,
      message: "Impossibile creare l’itinerario",
      fieldErrors: {},
    });
  });
});
