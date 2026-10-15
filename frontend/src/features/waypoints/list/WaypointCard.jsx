import {Link} from "react-router";

import {formatDate} from "../../../shared/utils/dateFormatters.js";

export function WaypointCard({
                               waypoint,
                               routeId,
                               onDelete,
                               isDeleting = false,
                               isDeleteDisabled = false
                             }) {
  const titleId = `waypoint-title-${waypoint.id}`;

  return (
    <article
      className="entity-card waypoint-card"
      aria-labelledby={titleId}
    >
      <div className="waypoint-card__main">
        <div
          className="waypoint-card__position"
          data-position={waypoint.position}
          aria-hidden="true"
        />

        <div className="entity-card__content">
          <h2
            className="entity-card__title"
            id={titleId}
          >
            {waypoint.name}
          </h2>

          {waypoint.description && (
            <p className="entity-card__description">
              {waypoint.description}
            </p>
          )}

          <dl className="entity-card__details waypoint-card__details">
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
                  {formatDate(waypoint.createdAt)}
                </time>
              </dd>
            </div>
          </dl>
        </div>
      </div>

      <div className="entity-card__actions waypoint-card__actions">
        <Link
          className="action-link"
          to={`/routes/${routeId}/waypoints/${waypoint.id}/edit`}
        >
          Modifica waypoint
        </Link>

        <button
          className="action-button action-button--danger"
          type="button"
          onClick={() => onDelete(waypoint)}
          disabled={isDeleteDisabled}
        >
          {isDeleting
            ? "Eliminazione in corso..."
            : "Elimina waypoint"}
        </button>
      </div>
    </article>
  );
}
