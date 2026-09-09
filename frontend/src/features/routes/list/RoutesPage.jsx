import {
  useEffect,
  useState,
} from "react";
import {Link} from "react-router";

import {
  DEFAULT_ROUTE_PAGE_SIZE,
  DEFAULT_ROUTE_SORT,
  listRoutes,
} from "./listRoutesApi.js";
import {RouteCard} from "./RouteCard.jsx";
import {RoutesPagination} from "./RoutesPagination.jsx";
import {ApiRequestError} from "../api/ApiRequestError.js";

export function RoutesPage() {
  const [page, setPage] = useState(0);
  const [routePage, setRoutePage] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [apiError, setApiError] = useState("");

  useEffect(() => {
    const abortController = new AbortController();

    async function loadRoutes() {
      setIsLoading(true);
      setApiError("");

      try {
        const response = await listRoutes({
          page,
          size: DEFAULT_ROUTE_PAGE_SIZE,
          sort: DEFAULT_ROUTE_SORT,
          signal: abortController.signal,
        });

        setRoutePage(response);
      } catch (error) {
        if (error.name === "AbortError") {
          return;
        }

        setRoutePage(null);

        if (error instanceof ApiRequestError) {
          setApiError(error.message);
        } else {
          setApiError(
            "Impossibile caricare gli itinerari",
          );
        }
      } finally {
        if (!abortController.signal.aborted) {
          setIsLoading(false);
        }
      }
    }

    loadRoutes();

    return () => {
      abortController.abort();
    };
  }, [page]);

  const routes = routePage?.content ?? [];

  return (
    <main>
      <header>
        <h1>Itinerari</h1>

        <Link to="/routes/new">
          Crea un nuovo itinerario
        </Link>
      </header>

      {isLoading && (
        <p role="status">
          Caricamento itinerari...
        </p>
      )}

      {!isLoading && apiError && (
        <p role="alert">
          {apiError}
        </p>
      )}

      {!isLoading &&
        !apiError &&
        routes.length === 0 && (
          <p>
            Nessun itinerario disponibile.
          </p>
        )}

      {!isLoading &&
        !apiError &&
        routes.length > 0 && (
          <>
            <section aria-label="Elenco itinerari">
              {routes.map((route) => (
                <RouteCard
                  key={route.id}
                  route={route}
                />
              ))}
            </section>

            <RoutesPagination
              page={routePage.page}
              totalPages={routePage.totalPages}
              isLoading={isLoading}
              onPageChange={setPage}
            />
          </>
        )}
    </main>
  );
}
