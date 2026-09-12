import {handleJsonResponse} from "../api/apiResponse.js";

export async function deleteRoute({routeId, signal} = {}) {

  const apiUrl = new URL(
    `/api/routes/${routeId}`,
    window.location.origin,
  );

  const response = await fetch(apiUrl, {
    method: "DELETE",
    headers: {
      Accept: "application/json",
    },
    signal,
  })

  return handleJsonResponse(
    response,
    "Impossibile cancellare l’itinerario",
  );
}
