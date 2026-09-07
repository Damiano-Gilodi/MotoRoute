import {useState} from "react";

import {CreateRouteForm} from "./CreateRouteForm";
import {
  ApiRequestError,
  createRoute,
} from "./createRouteApi";

import {useNavigate} from "react-router";

export function CreateRoutePage() {

  const navigate = useNavigate();

  const [isSubmitting, setIsSubmitting] =
    useState(false);

  const [serverErrors, setServerErrors] =
    useState({});

  const [apiError, setApiError] =
    useState("");

  async function handleCreateRoute(payload) {
    setIsSubmitting(true);
    setServerErrors({});
    setApiError("");

    try {
      const route = await createRoute(payload);

      navigate(`/routes/${route.id}`);
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

      <CreateRouteForm
        onSubmit={handleCreateRoute}
        isSubmitting={isSubmitting}
        serverErrors={serverErrors}
      />
    </main>
  );
}
