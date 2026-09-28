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
import {ApiRequestError} from "../../../shared/api/ApiRequestError.js";
import {listWaypoints} from "./listWaypointsApi.js";

const routeId = "11111111-1111-1111-1111-111111111111";

const apiUrl = new URL(
  `/api/routes/${routeId}/waypoints`,
  window.location.origin,
).toString();

const waypointPage = {
  content: [
    {
      id: "22222222-2222-2222-2222-222222222222",
      name: "Punto panoramico",
      description: "Descrizione del punto panoramico",
      position: 1,
      latitude: 45.123456,
      longitude: 9.123456,
      createdAt: "2026-07-29T10:00:00Z",
    },
  ],
  page: 0,
  size: 20,
  totalElements: 1,
  totalPages: 1,
  first: true,
  last: true,
};

async function getRejectedError(promise) {
  try {
    await promise;
  } catch (error) {
    return error;
  }

  throw new Error("Expected the promise to reject");
}

describe("listWaypoints", () => {
  test("should send pagination parameters and return the waypoint page", async () => {
    server.use(
      http.get(apiUrl, ({request}) => {
        const requestUrl = new URL(request.url);

        expect(requestUrl.pathname).toBe(`/api/routes/${routeId}/waypoints`);
        expect(requestUrl.searchParams.get("page")).toBe("2");
        expect(requestUrl.searchParams.get("size")).toBe("5");
        expect(request.headers.get("Accept")).toBe("application/json");

        return HttpResponse.json({
          ...waypointPage,
          page: 2,
          size: 5,
          totalElements: 11,
          totalPages: 3,
          first: false,
          last: true
        });
      }),
    );

    const result = await listWaypoints({
      routeId,
      page: 2,
      size: 5
    });

    expect(result.page).toBe(2);
    expect(result.size).toBe(5);
    expect(result.content).toHaveLength(1);
    expect(result.totalElements).toBe(11);
    expect(result.totalPages).toBe(3);
    expect(result.first).toBe(false);
    expect(result.last).toBe(true);
  });

  test("should use default pagination parameters", async () => {
    server.use(
      http.get(apiUrl, ({request}) => {
        const requestUrl = new URL(request.url);

        expect(requestUrl.pathname).toBe(`/api/routes/${routeId}/waypoints`);
        expect(requestUrl.searchParams.get("page")).toBe("0");
        expect(requestUrl.searchParams.get("size")).toBe("20");

        return HttpResponse.json(waypointPage);
      }),
    );

    const result = await listWaypoints({routeId});

    expect(result).toEqual(waypointPage);
  });

  test("should throw a structured API error", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 400,
            error: "Bad Request",
            message: "size must be between 1 and 100",
            path: "/api/routes/" + routeId + "/waypoints",
            fieldErrors: {},
          },
          {
            status: 400,
          },
        ),
      ),
    );

    const error = await getRejectedError(
      listWaypoints({routeId}),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 400,
      message: "size must be between 1 and 100",
      fieldErrors: {},
    });
  });

  test("should throw a structured error when the route does not exist", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json({
          timestamp: "2026-07-29T10:00:00Z",
          status: 404,
          error: "Not Found",
          message: `Route not found with id: ${routeId}`,
          path: `/api/routes/${routeId}/waypoints`,
          fieldErrors: {}
        }, {status: 404})
      )
    );

    const error = await getRejectedError(
      listWaypoints({routeId})
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 404,
      message: `Route not found with id: ${routeId}`,
      fieldErrors: {}
    });
  });

  test("should use a generic message when the response is not JSON", async () => {
    server.use(
      http.get(
        apiUrl,
        () =>
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
      listWaypoints({routeId}),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      status: 500,
      message:
        "Impossibile caricare i waypoint",
      fieldErrors: {},
    });
  });
});
