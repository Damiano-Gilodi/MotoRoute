import {Link} from "react-router";

import {formatRouteDate} from "../../routes/utils/routeFormatters.js";

export function WaypointCard({
                               waypoint,
                               routeId,
                               onDelete,
                               isDeleting = false,
                               isDeleteDisabled = false
                             }) {
  const titleId = `waypoint-title-${waypoint.id}`;

  return (
    <article aria-labelledby={titleId}>
      <h2 id={titleId}>{waypoint.name}</h2>

      {waypoint.description && <p>{waypoint.description}</p>}

      <dl>
        <div>
          <dt>Posizione</dt>
          <dd>{waypoint.position}</dd>
        </div>

        <div>
          <dt>Latitudine</dt>
          <dd>{waypoint.latitude}</dd>
        </div>

        <div>
          <dt>Longitudine</dt>
          <dd>{waypoint.longitude}</dd>
        </div>

        <div>
          <dt>Creato il</dt>
          <dd>
            <time dateTime={waypoint.createdAt}>
              {formatRouteDate(waypoint.createdAt)}
            </time>
          </dd>
        </div>
      </dl>

      <Link to={`/routes/${routeId}/waypoints/${waypoint.id}/edit`}>
        Modifica waypoint
      </Link>

      <button
        type="button"
        onClick={() => onDelete(waypoint)}
        disabled={isDeleteDisabled}
      >
        {isDeleting ? "Eliminazione in corso..." : "Elimina waypoint"}
      </button>
    </article>
  );
}
