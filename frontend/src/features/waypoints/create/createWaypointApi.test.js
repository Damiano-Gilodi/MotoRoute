import {http, HttpResponse} from "msw";
import {server} from "../../../test/server.js";
import {createWaypoint} from "./createWaypointApi.js";
import {ApiRequestError} from "../../../shared/api/ApiRequestError.js";
import {describe, expect, test} from "vitest";

const routeId = "11111111-1111-1111-1111-111111111111";

const apiUrl = new URL(
  `/api/routes/${routeId}/waypoints`,
  window.location.origin,
).toString();

const payload = {
  name: "Punta de la Marmolada",
  description: "Punto di osservazione panoramico",
  position: 1,
  longitude: 10.1167,
  latitude: 45.9167,
}

async function getRejectedError(promise) {
  try {
    await promise;
  } catch (error) {
    return error;
  }

  throw new Error("Expected the promise to reject");
}

describe("createWaypoint", () => {

  test("should send the waypoint payload and return the created waypoint", async () => {

    const waypointCreated = {
      id: "22222222-2222-2222-2222-222222222222",
      routeId: "11111111-1111-1111-1111-111111111111",
      ...payload,
      createdAt: "2026-07-28T10:00:00Z",
      updatedAt: "2026-07-28T10:00:00Z",
    }

    let receivedPayload

    server.use(
      http.post(apiUrl, async ({request}) => {
        receivedPayload = await request.json();

        expect(request.headers.get("Accept")).toBe("application/json");
        expect(request.headers.get("Content-Type")).toBe("application/json");

        return HttpResponse.json(waypointCreated, {
          status: 201,
          headers: {Location: `/api/routes/${routeId}/waypoints/${waypointCreated.id}`}
        });
      })
    );

    const result = await createWaypoint({routeId, payload})

    expect(receivedPayload).toEqual(payload)
    expect(result).toEqual(waypointCreated)
  })

  test("should throw a structured error when validation fails", async () => {

    server.use(
      http.post(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-28T10:00:00Z",
            status: 400,
            error: "Bad Request",
            message: "One or more fields are invalid",
            path: "/api/routes/" + routeId + "/waypoints",
            fieldErrors: {
              name: "must not be blank",
            },
          },
          {
            status: 400,
          },
        )
      )
    )

    const error = await getRejectedError(createWaypoint({routeId, payload}));

    expect(error).toBeInstanceOf(ApiRequestError)

    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 400,
      message: "One or more fields are invalid",
      fieldErrors: {
        name: "must not be blank",
      },
    })
  });

  test("should use a generic message when the error response is not JSON", async () => {
    server.use(
      http.post(apiUrl, () =>
        new HttpResponse("Internal server error", {
          status: 500,
          headers: {
            "Content-Type": "text/plain",
          },
        }),
      ),
    );

    const error = await getRejectedError(createWaypoint({routeId, payload}))

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 500,
      message: "Impossibile creare il waypoint",
      fieldErrors: {},
    })
  });

  test("should throw a structured error when position is occupied", async () => {

    server.use(
      http.post(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-28T10:00:00Z",
            status: 409,
            error: "Conflict",
            message: "Waypoint position 1 already exists in route: " + routeId,
            path: "/api/routes/" + routeId + "/waypoints",
            fieldErrors: {},
          },
          {status: 409,},
        )
      )
    );

    const error = await getRejectedError(createWaypoint({routeId, payload}))

    expect(error).toBeInstanceOf(ApiRequestError)
    
    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 409,
      message: "Waypoint position 1 already exists in route: " + routeId,
      fieldErrors: {},
    })

  });
})
