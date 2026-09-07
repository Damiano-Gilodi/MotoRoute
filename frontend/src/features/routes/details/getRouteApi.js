import {ApiRequestError} from "../api/ApiRequestError.js";

export {ApiRequestError};

async function readJsonResponse(response) {
  try {
    return await response.json();
  } catch {
    return null;
  }
}

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

  const responseBody = await readJsonResponse(response);

  if (!response.ok) {
    throw new ApiRequestError(
      responseBody?.message ?? "Impossibile caricare l’itinerario",
      response.status,
      responseBody?.fieldErrors ?? {},
    );
  }

  return responseBody;
}
