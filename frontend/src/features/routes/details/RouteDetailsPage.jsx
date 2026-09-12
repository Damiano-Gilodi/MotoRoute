import {
  useEffect,
  useState,
} from "react";
import {
  Link,
  useNavigate,
  useParams,
} from "react-router";

import {RouteDetails} from "./RouteDetails.jsx";
import {getRoute} from "./getRouteApi.js";
import {ApiRequestError} from "../api/ApiRequestError.js";
import {deleteRoute} from "../delete/deleteRouteApi.js";

export function RouteDetailsPage() {

  const {routeId} = useParams();
  const navigate = useNavigate();

  const [route, setRoute] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isNotFound, setIsNotFound] = useState(false);
  const [apiError, setApiError] = useState("");
  const [isDeleting, setIsDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState("");

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

  async function handleDelete() {
    const confirmed = window.confirm(
      "Vuoi davvero eliminare questo itinerario?"
    );

    if (!confirmed) {
      return;
    }

    setIsDeleting(true);
    setDeleteError("");

    try {
      await deleteRoute({routeId});

      navigate("/routes", {replace: true,});

    } catch (error) {
      if (error instanceof ApiRequestError) {
        setDeleteError(error.message);
      } else {
        setDeleteError(
          "Impossibile cancellare l’itinerario",
        );
      }
    } finally {
      setIsDeleting(false);
    }
  }

  return (
    <main>
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

      {!isLoading &&
        !isNotFound &&
        !apiError &&
        route && (
          <>
            <RouteDetails route={route}/>

            <Link to={`/routes/${route.id}/edit`}>
              Modifica itinerario
            </Link>

            <button type="button" onClick={handleDelete} disabled={isDeleting}>
              {isDeleting ? "Eliminazione in corso..." : "Elimina itinerario"}
            </button>

            {deleteError && (
              <p role="alert">
                {deleteError}
              </p>
            )}
          </>
        )}

      <Link to="/routes"> Torna agli itinerari </Link>
    </main>
  );
}
