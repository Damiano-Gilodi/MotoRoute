import {server} from "../../../test/server.js";
import {http, HttpResponse} from "msw";
import {
  describe,
  expect,
  test,
} from "vitest";

import {getWaypoint} from "./getWaypointApi.js";
import {ApiRequestError} from "../../../shared/api/ApiRequestError.js";

const routeId = "11111111-1111-1111-1111-111111111111";
const waypointId = "22222222-2222-2222-2222-222222222222";

const apiUrl = new URL(
  `/api/routes/${routeId}/waypoints/${waypointId}`,
  window.location.origin,
).toString();

const waypoint = {
  id: waypointId,
  routeId: routeId,
  name: "Punta de la Marmolada",
  description: "Punto di osservazione panoramico",
  position: 1,
  longitude: 10.1167,
  latitude: 45.9167,
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

describe("getWaypoint", () => {
  test("should fetch a waypoint by ID and route ID", async () => {

    server.use(
      http.get(apiUrl, ({request}) => {

        const requestUrl = new URL(request.url);

        expect(requestUrl.pathname).toBe(`/api/routes/${routeId}/waypoints/${waypointId}`);
        expect(request.headers.get("Accept")).toBe("application/json");

        return HttpResponse.json(waypoint);
      }),
    );

    const result = await getWaypoint({routeId, waypointId});

    expect(result).toEqual(waypoint);
  })

  test("should throw a structured API error when waypoint does not exist", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 404,
            error: "Not Found",
            message: "Waypoint not found with id: " + waypointId,
            path: `/api/routes/${routeId}/waypoints/${waypointId}`,
            fieldErrors: {},
          },
          {
            status: 404,
          },
        )
      )
    )

    const error = await getRejectedError(
      getWaypoint({routeId, waypointId}),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 404,
      message: "Waypoint not found with id: " + waypointId,
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
            path: `/api/routes/${routeId}/waypoints/${waypointId}`,
            fieldErrors: {},
          },
          {
            status: 500,
          },
        ),
      ),
    );

    const error = await getRejectedError(
      getWaypoint({routeId, waypointId}),
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
      getWaypoint({routeId, waypointId}),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      status: 500,
      message: "Impossibile caricare il waypoint",
      fieldErrors: {},
    });
  });
});
