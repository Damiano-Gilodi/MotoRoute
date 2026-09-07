import {ApiRequestError} from "../api/ApiRequestError.js";
import {handleJsonResponse} from "../api/apiResponse.js";

export {ApiRequestError};

export async function createRoute(payload) {
  const apiUrl = new URL(
    "/api/routes",
    window.location.origin,
  );

  const response = await fetch(apiUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  return handleJsonResponse(
    response,
    "Impossibile creare l’itinerario",
  );
}
