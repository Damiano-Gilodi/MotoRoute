import {useEffect, useState} from "react";
import {Link, useParams} from "react-router";

import {ApiRequestError} from "../../../shared/api/ApiRequestError.js";
import {deleteWaypoint} from "../delete/deleteWaypointApi.js";
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
  const [deletingWaypointId, setDeletingWaypointId] = useState(null);
  const [deleteError, setDeleteError] = useState("");
  const [reloadKey, setReloadKey] = useState(0);

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
        if (error.name === "AbortError") return;

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
  }, [routeId, page, reloadKey]);

  const waypoints = waypointPage?.content ?? [];

  async function handleDeleteWaypoint(waypoint) {
    if (deletingWaypointId !== null) return;

    const confirmed = window.confirm(
      `Vuoi davvero eliminare il waypoint "${waypoint.name}"?`
    );

    if (!confirmed) return;

    setDeletingWaypointId(waypoint.id);
    setDeleteError("");

    try {
      await deleteWaypoint({
        routeId,
        waypointId: waypoint.id
      });

      if (waypoints.length === 1 && page > DEFAULT_WAYPOINT_PAGE) {
        setPage(currentPage => currentPage - 1);
      } else {
        setReloadKey(currentKey => currentKey + 1);
      }
    } catch (error) {
      if (error instanceof ApiRequestError) {
        setDeleteError(error.message);
      } else {
        setDeleteError("Impossibile eliminare il waypoint");
      }
    } finally {
      setDeletingWaypointId(null);
    }
  }

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

      {deleteError && (
        <p role="alert">
          {deleteError}
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
                routeId={routeId}
                onDelete={handleDeleteWaypoint}
                isDeleting={deletingWaypointId === waypoint.id}
                isDeleteDisabled={deletingWaypointId !== null}
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
