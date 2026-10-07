import {handleJsonResponse} from "../../../shared/api/apiResponse.js";

export async function getWaypoint({
                                    routeId,
                                    waypointId,
                                    signal,
                                  } = {}) {

  const apiUrl = new URL(
    `/api/routes/${routeId}/waypoints/${waypointId}`,
    window.location.origin,
  );

  const response = await fetch(apiUrl, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    signal,
  });

  return handleJsonResponse(
    response,
    "Impossibile caricare il waypoint",
  );
}
