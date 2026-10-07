import {http, HttpResponse} from "msw";
import {
  describe,
  expect,
  test,
} from "vitest";

import {server} from "../../../test/server.js";
import {ApiRequestError} from "../../../shared/api/ApiRequestError.js";
import {updateWaypoint} from "./updateWaypointApi.js";

const routeId = "11111111-1111-1111-1111-111111111111";
const waypointId = "22222222-2222-2222-2222-222222222222";

const apiUrl = new URL(
  `/api/routes/${routeId}/waypoints/${waypointId}`,
  window.location.origin,
).toString();

const waypointUpdated = {
  id: waypointId,
  routeId,
  name: "Punta de la Marmolada",
  description: "Punto di osservazione panoramico",
  position: 1,
  longitude: 10.1167,
  latitude: 45.9167,
  createdAt: "2026-07-29T10:00:00Z",
  updatedAt: "2026-07-29T10:30:00Z",
};

const validPayload = {
  name: "Punta de la Marmolada",
  description: "Punto di osservazione panoramico",
  position: 1,
  longitude: 10.1167,
  latitude: 45.9167,
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

describe("updateWaypoint", () => {
  test("should send the updated waypoint payload and return the updated waypoint", async () => {
      let receivedPayload;

      server.use(
        http.put(apiUrl, async ({request}) => {

          receivedPayload = await request.json();
          const requestUrl = new URL(request.url);

          expect(requestUrl.pathname).toBe(`/api/routes/${routeId}/waypoints/${waypointId}`);
          expect(request.headers.get("Accept")).toBe("application/json");
          expect(request.headers.get("Content-Type")).toBe("application/json");

          return HttpResponse.json(waypointUpdated, {status: 200});
        }),
      );

      const result = await updateWaypoint({routeId, waypointId, payload: validPayload});

      expect(receivedPayload).toEqual(validPayload);
      expect(result).toEqual(waypointUpdated);
    },
  );

  test("should throw a structured API error when waypoint does not exist", async () => {
      server.use(
        http.put(apiUrl, () =>
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
          ),
        ),
      );

      const error = await getRejectedError(
        updateWaypoint({routeId, waypointId, payload: validPayload}));


      expect(error).toBeInstanceOf(ApiRequestError);

      expect(error).toMatchObject({
        name: "ApiRequestError",
        status: 404,
        message: "Waypoint not found with id: " + waypointId,
        fieldErrors: {},
      });
    },
  );

  test("should preserve field errors when update request is invalid", async () => {
      server.use(
        http.put(apiUrl, () =>
          HttpResponse.json(
            {
              timestamp: "2026-07-29T10:00:00Z",
              status: 400,
              error: "Bad Request",
              message: "One or more fields are invalid",
              path: `/api/routes/${routeId}/waypoints/${waypointId}`,
              fieldErrors: {
                name: "name must not be blank",
                position: "position must be greater than zero",
              },
            },
            {
              status: 400,
            },
          ),
        ),
      );

      const error = await getRejectedError(
        updateWaypoint({routeId, waypointId, payload: validPayload}));

      expect(error).toBeInstanceOf(ApiRequestError);

      expect(error).toMatchObject({
        status: 400,
        message: "One or more fields are invalid",
        fieldErrors: {
          name: "name must not be blank",
          position: "position must be greater than zero",
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
              headers: {"Content-Type": "text/plain"}
            },
          ),
        ),
      );

      const error = await getRejectedError(
        updateWaypoint({routeId, waypointId, payload: validPayload}));

      expect(error).toBeInstanceOf(ApiRequestError);

      expect(error).toMatchObject({
        status: 500,
        message: "Impossibile aggiornare il waypoint",
        fieldErrors: {},
      });
    },
  );

  test("should throw a conflict error when waypoint position is already occupied", async () => {
    server.use(
      http.put(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 409,
            error: "Conflict",
            message: `Waypoint position 1 already exists in route: ${routeId}`,
            path: `/api/routes/${routeId}/waypoints/${waypointId}`,
            fieldErrors: {},
          },
          {status: 409},
        ),
      ),
    );

    const error = await getRejectedError(
      updateWaypoint({routeId, waypointId, payload: validPayload}));

    expect(error).toBeInstanceOf(ApiRequestError);
    expect(error).toMatchObject({
      status: 409,
      message: `Waypoint position 1 already exists in route: ${routeId}`,
      fieldErrors: {},
    });
  });
});
