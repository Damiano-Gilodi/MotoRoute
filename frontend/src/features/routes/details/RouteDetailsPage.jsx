import {
  useEffect,
  useState,
} from "react";
import {
  Link,
  useParams,
} from "react-router";

import {RouteDetails} from "./RouteDetails.jsx";
import {
  ApiRequestError,
  getRoute,
} from "./getRouteApi.js";

export function RouteDetailsPage() {

  const {routeId} = useParams();

  const [route, setRoute] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isNotFound, setIsNotFound] = useState(false);
  const [apiError, setApiError] = useState("");

  useEffect(() => {
    const abortController = new AbortController();

    async function loadRoute() {
      setIsLoading(true);
      setRoute(null);
      setIsNotFound(false);
      setApiError("");

      try {
        const response = await getRoute({
          routeId,
          signal: abortController.signal,
        });

        setRoute(response);
      } catch (error) {
        if (error.name === "AbortError") {
          return;
        }

        if (
          error instanceof ApiRequestError &&
          error.status === 404
        ) {
          setIsNotFound(true);
          return;
        }

        if (error instanceof ApiRequestError) {
          setApiError(error.message);
        } else {
          setApiError(
            "Impossibile caricare l’itinerario",
          );
        }
      } finally {
        if (!abortController.signal.aborted) {
          setIsLoading(false);
        }
      }
    }

    void loadRoute();

    return () => {
      abortController.abort();
    };
  }, [routeId]);


  return (
    <main>
      <Link to="/routes">
        Torna agli itinerari
      </Link>

      {isLoading && (
        <p role="status">
          Caricamento itinerario...
        </p>
      )}

      {!isLoading && isNotFound && (
        <section>
          <h1>Itinerario non trovato</h1>

          <p>
            L’itinerario richiesto non esiste.
          </p>
        </section>
      )}

      {!isLoading && apiError && (
        <p role="alert">
          {apiError}
        </p>
      )}

      {!isLoading && !isNotFound && !apiError && route && (
        <RouteDetails route={route}/>
      )}
    </main>
  );
}
