import {ApiRequestError} from "../api/ApiRequestError.js";

export {ApiRequestError};

async function readJsonResponse(response) {
  try {
    return await response.json();
  } catch {
    return null;
  }
}

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

  const responseBody = await readJsonResponse(response);

  if (!response.ok) {
    throw new ApiRequestError(
      responseBody?.message ??
      "Impossibile creare l’itinerario",
      response.status,
      responseBody?.fieldErrors ?? {},
    );
  }

  return responseBody;
}
