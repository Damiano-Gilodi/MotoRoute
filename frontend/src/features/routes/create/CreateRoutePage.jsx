import {useState} from "react";

import {CreateRouteForm} from "./CreateRouteForm";
import {
  ApiRequestError,
  createRoute,
} from "./createRouteApi";

export function CreateRoutePage() {
  const [isSubmitting, setIsSubmitting] =
    useState(false);

  const [serverErrors, setServerErrors] =
    useState({});

  const [apiError, setApiError] =
    useState("");

  const [createdRoute, setCreatedRoute] =
    useState(null);

  async function handleCreateRoute(payload) {
    setIsSubmitting(true);
    setServerErrors({});
    setApiError("");
    setCreatedRoute(null);

    try {
      const route = await createRoute(payload);

      setCreatedRoute(route);
    } catch (error) {
      if (error instanceof ApiRequestError) {
        setServerErrors(error.fieldErrors);
        setApiError(error.message);
      } else {
        setApiError(
          "Impossibile creare l’itinerario",
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main>
      {apiError && (
        <p role="alert">
          {apiError}
        </p>
      )}

      {createdRoute && (
        <p role="status">
          Itinerario creato con successo:{" "}
          {createdRoute.name}.
        </p>
      )}

      <CreateRouteForm
        onSubmit={handleCreateRoute}
        isSubmitting={isSubmitting}
        serverErrors={serverErrors}
      />
    </main>
  );
}
