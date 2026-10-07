import {useEffect, useState} from "react";
import {Link, useParams} from "react-router";

import {ApiRequestError} from "../../../shared/api/ApiRequestError.js";
import {WaypointCard} from "./WaypointCard.jsx";
import {WaypointPagination} from "./WaypointPagination.jsx";
import {
  DEFAULT_WAYPOINT_PAGE,
  DEFAULT_WAYPOINT_PAGE_SIZE,
  listWaypoints
} from "./listWaypointsApi.js";

export function WaypointsPage() {
  const {routeId} = useParams();

  const [page, setPage] = useState(DEFAULT_WAYPOINT_PAGE);
  const [waypointPage, setWaypointPage] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [apiError, setApiError] = useState("");

  useEffect(() => {
    const abortController = new AbortController();

    async function loadWaypoints() {
      setIsLoading(true);
      setApiError("");

      try {
        const response = await listWaypoints({
          routeId,
          page,
          size: DEFAULT_WAYPOINT_PAGE_SIZE,
          signal: abortController.signal
        });

        setWaypointPage(response);
      } catch (error) {
        if (error.name === "AbortError") {
          return;
        }

        setWaypointPage(null);

        if (error instanceof ApiRequestError) {
          setApiError(error.message);
        } else {
          setApiError("Impossibile caricare i waypoint");
        }
      } finally {
        if (!abortController.signal.aborted) {
          setIsLoading(false);
        }
      }
    }

    void loadWaypoints();

    return () => {
      abortController.abort();
    };
  }, [routeId, page]);

  const waypoints = waypointPage?.content ?? [];

  return (
    <main>
      <header>
        <h1>Waypoint dell'itinerario</h1>

        <Link to={`/routes/${routeId}`}>
          Torna all'itinerario
        </Link>

        <Link to={`/routes/${routeId}/waypoints/new`}>
          Aggiungi waypoint
        </Link>
      </header>

      {isLoading && (
        <p role="status">
          Caricamento waypoint...
        </p>
      )}

      {!isLoading && apiError && (
        <p role="alert">
          {apiError}
        </p>
      )}

      {!isLoading && !apiError && waypoints.length === 0 && (
        <p>Nessun waypoint disponibile.</p>
      )}

      {!isLoading && !apiError && waypoints.length > 0 && (
        <>
          <section aria-label="Elenco waypoint">
            {waypoints.map((waypoint) => (
              <WaypointCard
                key={waypoint.id}
                waypoint={waypoint}
              />
            ))}
          </section>

          <WaypointPagination
            page={waypointPage.page}
            totalPages={waypointPage.totalPages}
            isLoading={isLoading}
            onPageChange={setPage}
          />
        </>
      )}
    </main>
  );
}
