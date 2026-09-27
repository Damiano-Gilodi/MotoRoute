import {handleJsonResponse} from "../../../shared/api/apiResponse.js";

export async function createWaypoint({routeId, payload, signal} = {}) {

  const apiUrl = new URL(
    `/api/routes/${routeId}/waypoints`,
    window.location.origin,
  );

  const response = await fetch(apiUrl, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
    signal,
  });

  return handleJsonResponse(
    response,
    "Impossibile creare il waypoint",
  );
}
