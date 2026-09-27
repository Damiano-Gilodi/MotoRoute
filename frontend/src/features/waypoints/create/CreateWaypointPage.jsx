import {useNavigate, useParams} from "react-router";
import {useState} from "react";
import {ApiRequestError} from "../../../shared/api/ApiRequestError.js";
import {createWaypoint} from "./createWaypointApi.js";
import {WaypointForm} from "./WaypointForm.jsx";

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
        setServerErrors(error.fieldErrors);
        setApiError(error.message);
      } else {
        setApiError("Impossibile creare il waypoint");
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main>
      <h1>Crea Waypoint</h1>

      {apiError && (
        <p role="alert">
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
