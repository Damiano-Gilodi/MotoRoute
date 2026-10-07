import {handleJsonResponse} from "../../../shared/api/apiResponse.js";

export async function updateWaypoint({
                                       routeId,
                                       waypointId,
                                       payload,
                                       signal,
                                     } = {}) {
  const apiUrl = new URL(
    `/api/routes/${routeId}/waypoints/${waypointId}`,
    window.location.origin,
  );

  const response = await fetch(apiUrl, {
    method: "PUT",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
    signal,
  });

  return handleJsonResponse(
    response,
    "Impossibile aggiornare il waypoint",
  );
}
