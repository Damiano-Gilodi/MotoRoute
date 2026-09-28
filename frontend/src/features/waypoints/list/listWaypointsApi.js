import {handleJsonResponse} from "../../../shared/api/apiResponse.js";

export const DEFAULT_WAYPOINT_PAGE = 0;
export const DEFAULT_WAYPOINT_PAGE_SIZE = 20;

export async function listWaypoints({
                                      routeId,
                                      page = DEFAULT_WAYPOINT_PAGE,
                                      size = DEFAULT_WAYPOINT_PAGE_SIZE,
                                      signal
                                    } = {}) {

  const apiUrl = new URL(`/api/routes/${routeId}/waypoints`, window.location.origin);

  apiUrl.searchParams.set("page", String(page));
  apiUrl.searchParams.set("size", String(size));

  const response = await fetch(apiUrl, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    signal,
  })

  return handleJsonResponse(
    response,
    "Impossibile caricare i waypoint",
  );
}
