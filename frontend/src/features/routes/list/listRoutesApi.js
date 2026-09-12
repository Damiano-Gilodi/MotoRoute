import {handleJsonResponse} from "../api/apiResponse.js";

export const DEFAULT_ROUTE_PAGE = 0;
export const DEFAULT_ROUTE_PAGE_SIZE = 20;
export const DEFAULT_ROUTE_SORT = "createdAt,desc";

export async function listRoutes({
                                   page = DEFAULT_ROUTE_PAGE,
                                   size = DEFAULT_ROUTE_PAGE_SIZE,
                                   sort = DEFAULT_ROUTE_SORT,
                                   signal,
                                 } = {}) {
  const apiUrl = new URL(
    "/api/routes",
    window.location.origin,
  );

  apiUrl.searchParams.set("page", String(page));
  apiUrl.searchParams.set("size", String(size));
  apiUrl.searchParams.set("sort", sort);

  const response = await fetch(apiUrl, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    signal,
  });

  return handleJsonResponse(
    response,
    "Impossibile caricare gli itinerari",
  );
}
