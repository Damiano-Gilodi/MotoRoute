import {handleJsonResponse} from "../api/apiResponse.js";

export async function updateRoute({
                                    routeId,
                                    payload,
                                    signal,
                                  } = {}) {
  const apiUrl = new URL(
    `/api/routes/${routeId}`,
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
    "Impossibile aggiornare l’itinerario",
  );
}
