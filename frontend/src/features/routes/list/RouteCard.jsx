import {Link} from "react-router";

import {
  formatRouteDate,
  formatRouteDifficulty,
} from "../utils/routeFormatters.js";

export function RouteCard({route}) {
  const titleId = `route-title-${route.id}`;

  return (
    <article aria-labelledby={titleId}>
      <h2 id={titleId}>{route.name}</h2>

      <dl>
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
              {formatRouteDate(route.createdAt)}
            </time>
          </dd>
        </div>
      </dl>

      <Link to={`/routes/${route.id}`}>
        Visualizza dettagli
      </Link>
    </article>
  );
}
