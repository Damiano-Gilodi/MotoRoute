import {
  useEffect,
  useState,
} from "react";
import {
  useNavigate,
  useParams,
} from "react-router";

import {ApiRequestError} from "../../../shared/api/ApiRequestError.js";
import {WaypointForm} from "../form/WaypointForm.jsx";
import {getWaypoint} from "./getWaypointApi.js";
import {updateWaypoint} from "./updateWaypointApi.js";

export function WaypointEditPage() {
  const {routeId, waypointId} = useParams();
  const navigate = useNavigate();

  const [waypoint, setWaypoint] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [apiError, setApiError] = useState("");
  const [serverErrors, setServerErrors] = useState({});

  useEffect(() => {
    const abortController = new AbortController();

    async function loadWaypoint() {
      setIsLoading(true);
      setApiError("");

      try {
        const response = await getWaypoint({
          routeId,
          waypointId,
          signal: abortController.signal,
        });

        setWaypoint(response);
      } catch (error) {
        if (error.name === "AbortError") {
          return;
        }

        setWaypoint(null);

        if (error instanceof ApiRequestError) {
          setApiError(error.message);
        } else {
          setApiError("Impossibile caricare il waypoint");
        }
      } finally {
        if (!abortController.signal.aborted) {
          setIsLoading(false);
        }
      }
    }

    void loadWaypoint();

    return () => {
      abortController.abort();
    };
  }, [routeId, waypointId]);

  async function handleSubmit(payload) {
    setIsSubmitting(true);
    setApiError("");
    setServerErrors({});

    try {
      await updateWaypoint({
        routeId,
        waypointId,
        payload,
      });

      navigate(`/routes/${routeId}/waypoints`, {replace: true});
    } catch (error) {
      if (error instanceof ApiRequestError) {
        setApiError(error.message);
        setServerErrors(error.fieldErrors ?? {});
      } else {
        setApiError("Impossibile aggiornare il waypoint");
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  if (isLoading) {
    return (
      <main>
        <p role="status">
          Caricamento waypoint...
        </p>
      </main>
    );
  }

  if (!waypoint) {
    return (
      <main>
        <h1>Modifica waypoint</h1>

        {apiError && (
          <p role="alert">
            {apiError}
          </p>
        )}
      </main>
    );
  }

  const initialValues = {
    name: waypoint.name,
    description: waypoint.description ?? "",
    position: String(waypoint.position),
    latitude: String(waypoint.latitude),
    longitude: String(waypoint.longitude),
  };

  return (
    <main>
      <h1>Modifica waypoint</h1>

      {apiError && (
        <p role="alert">
          {apiError}
        </p>
      )}

      <WaypointForm
        initialValues={initialValues}
        onSubmit={handleSubmit}
        isSubmitting={isSubmitting}
        serverErrors={serverErrors}
        submitLabel="Salva modifiche"
      />
    </main>
  );
}
