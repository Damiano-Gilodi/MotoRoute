import {handleJsonResponse} from "../api/apiResponse.js";

export async function createRoute({
                                    payload,
                                    signal,
                                  } = {}) {
  const apiUrl = new URL(
    "/api/routes",
    window.location.origin,
  );

  const response = await fetch(apiUrl, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
    signal,
  });

  return handleJsonResponse(
    response,
    "Impossibile creare l’itinerario",
  );
}
