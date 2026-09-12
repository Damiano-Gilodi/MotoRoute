import {server} from "../../../test/server.js";
import {http, HttpResponse} from "msw";
import {
  describe,
  expect,
  test,
} from "vitest";
import {getRoute} from "./getRouteApi.js";
import {ApiRequestError} from "../api/ApiRequestError.js";

const routeId = "11111111-1111-1111-1111-111111111111";

const apiUrl = new URL(
  `/api/routes/${routeId}`,
  window.location.origin,
).toString();

const route = {
  id: routeId,
  name: "Passo dello Stelvio",
  description: "Percorso panoramico",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: 47.5,
  difficulty: "HARD",
  createdAt: "2026-07-29T10:00:00Z",
  updatedAt: "2026-07-29T10:00:00Z",
}

async function getRejectedError(promise) {
  try {
    await promise;
  } catch (error) {
    return error;
  }

  throw new Error("Expected the promise to reject");
}

describe("getRoute", () => {
  test("should fetch a route by ID", async () => {

    server.use(
      http.get(apiUrl, ({request}) => {

        const requestUrl = new URL(request.url);

        expect(requestUrl.pathname).toBe(`/api/routes/${routeId}`);
        expect(request.headers.get("Accept")).toBe("application/json");

        return HttpResponse.json(route);
      }),
    );

    const result = await getRoute({routeId});

    expect(result).toEqual(route);
  })

  test("should throw a structured API error when route does not exist", async () => {
    server.use(
      http.get(apiUrl, () =>
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
      )
    )

    const error = await getRejectedError(
      getRoute({routeId}),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 404,
      message: "Route not found with id: " + routeId,
      fieldErrors: {},
    });
  })

  test("should throw a structured API error when server returns 500", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 500,
            error: "Internal Server Error",
            message: "An unexpected error occurred",
            path: `/api/routes/${routeId}`,
            fieldErrors: {},
          },
          {
            status: 500,
          },
        ),
      ),
    );

    const error = await getRejectedError(
      getRoute({routeId}),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 500,
      message: "An unexpected error occurred",
      fieldErrors: {},
    });
  });

  test("should use a generic message when the response is not JSON", async () => {
    server.use(
      http.get(apiUrl, () =>
        new HttpResponse(
          "Internal server error",
          {
            status: 500,
            headers: {
              "Content-Type": "text/plain",
            },
          },
        ),
      ),
    );

    const error = await getRejectedError(
      getRoute({routeId}),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      status: 500,
      message: "Impossibile caricare l’itinerario",
      fieldErrors: {},
    });
  });
});
