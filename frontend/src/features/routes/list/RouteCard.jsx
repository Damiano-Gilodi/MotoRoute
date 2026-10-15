import {Link} from "react-router";

import {formatRouteDifficulty} from "../utils/routeFormatters.js";
import {formatDate} from "../../../shared/utils/dateFormatters.js";

export function RouteCard({route}) {
  const titleId = `route-title-${route.id}`;

  return (
    <article
      className="entity-card route-card"
      aria-labelledby={titleId}
    >
      <div className="entity-card__content">
        <h2
          className="entity-card__title"
          id={titleId}
        >
          {route.name}
        </h2>

        <dl className="entity-card__details route-card__details">
          <div>
            <dt>Partenza</dt>
            <dd>{route.startLocation}</dd>
          </div>

          <div>
            <dt>Arrivo</dt>
            <dd>{route.endLocation}</dd>
          </div>

          <div>
            <dt>Distanza</dt>
            <dd>{route.distanceKm} km</dd>
          </div>

          <div>
            <dt>Difficoltà</dt>
            <dd>
              {formatRouteDifficulty(route.difficulty)}
            </dd>
          </div>

          <div>
            <dt>Creato il</dt>
            <dd>
              <time dateTime={route.createdAt}>
                {formatDate(route.createdAt)}
              </time>
            </dd>
          </div>
        </dl>
      </div>

      <div className="entity-card__actions route-card__actions">
        <Link
          className="action-link"
          to={`/routes/${route.id}`}
        >
          Visualizza dettagli
        </Link>
      </div>
    </article>
  );
}
