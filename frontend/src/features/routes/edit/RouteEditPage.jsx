import {
  useEffect,
  useState,
} from "react";
import {
  useNavigate,
  useParams,
} from "react-router";

import {ApiRequestError} from "../api/ApiRequestError.js";
import {getRoute} from "../details/getRouteApi.js";
import {RouteForm} from "../form/RouteForm.jsx";
import {updateRoute} from "./updateRouteApi.js";

export function RouteEditPage() {
  const {routeId} = useParams();
  const navigate = useNavigate();

  const [route, setRoute] = useState(null);
  const [isLoading, setIsLoading] =
    useState(true);
  const [isSubmitting, setIsSubmitting] =
    useState(false);
  const [apiError, setApiError] =
    useState("");
  const [serverErrors, setServerErrors] =
    useState({});

  useEffect(() => {
    const abortController =
      new AbortController();

    async function loadRoute() {
      setIsLoading(true);
      setApiError("");

      try {
        const response = await getRoute({
          routeId,
          signal:
          abortController.signal,
        });

        setRoute(response);
      } catch (error) {
        if (
          error.name === "AbortError"
        ) {
          return;
        }

        setRoute(null);

        if (
          error instanceof ApiRequestError
        ) {
          setApiError(error.message);
        } else {
          setApiError(
            "Impossibile caricare l’itinerario",
          );
        }
      } finally {
        setIsLoading(false);
      }
    }

    void loadRoute();

    return () => {
      abortController.abort();
    };
  }, [routeId]);

  async function handleSubmit(payload) {
    setIsSubmitting(true);
    setApiError("");
    setServerErrors({});

    try {
      const updatedRoute = await updateRoute({
        routeId,
        payload,
      });

      navigate(`/routes/${updatedRoute.id}`,);

    } catch (error) {
      if (
        error instanceof ApiRequestError
      ) {
        setApiError(error.message);
        setServerErrors(
          error.fieldErrors ?? {},
        );
      } else {
        setApiError(
          "Impossibile aggiornare l’itinerario",
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  if (isLoading) {
    return (
      <main>
        <p>
          Caricamento itinerario...
        </p>
      </main>
    );
  }

  if (!route) {
    return (
      <main>
        <h1>Modifica itinerario</h1>

        {apiError && (
          <p role="alert">
            {apiError}
          </p>
        )}
      </main>
    );
  }

  const initialValues = {
    name: route.name,
    description: route.description ?? "",
    startLocation: route.startLocation,
    endLocation: route.endLocation,
    distanceKm: String(route.distanceKm),
    difficulty: route.difficulty,
  };

  return (
    <main>
      <h1>Modifica itinerario</h1>

      {apiError && (
        <p role="alert">
          {apiError}
        </p>
      )}

      <RouteForm
        initialValues={initialValues}
        onSubmit={handleSubmit}
        isSubmitting={isSubmitting}
        serverErrors={serverErrors}
        submitLabel="Salva modifiche"
      />
    </main>
  );
}
