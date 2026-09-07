import {ApiRequestError} from "./ApiRequestError.js";

async function readJsonResponse(response) {
  try {
    return await response.json();
  } catch {
    return null;
  }
}

export async function handleJsonResponse(
  response,
  fallbackMessage,
) {
  const responseBody = await readJsonResponse(response);

  if (!response.ok) {
    throw new ApiRequestError(
      responseBody?.message ?? fallbackMessage,
      response.status,
      responseBody?.fieldErrors ?? {},
    );
  }

  return responseBody;
}
