import {useState} from "react";
import {useNavigate, useParams} from "react-router";

import {ApiRequestError} from "../../../shared/api/ApiRequestError.js";
import {WaypointForm} from "../form/WaypointForm.jsx";
import {createWaypoint} from "./createWaypointApi.js";

export function CreateWaypointPage() {
  const {routeId} = useParams();
  const navigate = useNavigate();

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [serverErrors, setServerErrors] = useState({});
  const [apiError, setApiError] = useState("");

  async function handleCreateWaypoint(payload) {
    setIsSubmitting(true);
    setServerErrors({});
    setApiError("");

    try {
      await createWaypoint({routeId, payload});

      navigate(`/routes/${routeId}`);
    } catch (error) {
      if (error instanceof ApiRequestError) {
        setServerErrors(error.fieldErrors ?? {});
        setApiError(error.message);
      } else {
        setApiError("Impossibile creare il waypoint");
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="form-page">
      <h1 className="form-page__title">
        Crea waypoint
      </h1>

      {apiError && (
        <p className="error-message" role="alert">
          {apiError}
        </p>
      )}

      <WaypointForm
        onSubmit={handleCreateWaypoint}
        isSubmitting={isSubmitting}
        serverErrors={serverErrors}
        submitLabel="Crea waypoint"
      />
    </main>
  );
}
