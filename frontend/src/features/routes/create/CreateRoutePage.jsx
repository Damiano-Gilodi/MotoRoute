import {useState} from "react";

import {createRoute} from "./createRouteApi";

import {useNavigate} from "react-router";
import {ApiRequestError} from "../api/ApiRequestError.js";
import {RouteForm} from "../form/RouteForm.jsx";

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
      const route = await createRoute({payload,});

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
      <h1>Crea itinerario</h1>
      
      {apiError && (
        <p role="alert">
          {apiError}
        </p>
      )}

      <RouteForm
        onSubmit={handleCreateRoute}
        isSubmitting={isSubmitting}
        serverErrors={serverErrors}
        submitLabel="Crea itinerario"
      />
    </main>
  );
}
