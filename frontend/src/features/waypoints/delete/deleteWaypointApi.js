import {handleJsonResponse} from "../../../shared/api/apiResponse.js";

export async function deleteWaypoint({routeId, waypointId, signal} = {}) {
  const apiUrl = new URL(
    `/api/routes/${routeId}/waypoints/${waypointId}`,
    window.location.origin
  );

  const response = await fetch(apiUrl, {
    method: "DELETE",
    headers: {
      Accept: "application/json"
    },
    signal
  });

  return handleJsonResponse(
    response,
    "Impossibile cancellare il waypoint"
  );
}
