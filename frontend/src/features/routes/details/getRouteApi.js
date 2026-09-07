import {ApiRequestError} from "../api/ApiRequestError.js";
import {handleJsonResponse} from "../api/apiResponse.js";

export {ApiRequestError};

export async function getRoute({
                                 routeId,
                                 signal,
                               } = {}) {

  const apiUrl = new URL(
    `/api/routes/${routeId}`,
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
    "Impossibile caricare l’itinerario",
  );
}
