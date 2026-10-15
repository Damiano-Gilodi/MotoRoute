import {formatRouteDifficulty} from "../utils/routeFormatters.js";
import {formatDate} from "../../../shared/utils/dateFormatters.js";
import "../routes.css";

export function RouteDetails({route}) {
  const {
    name,
    description,
    startLocation,
    endLocation,
    distanceKm,
    difficulty,
    createdAt,
    updatedAt
  } = route;

  return (
    <article className="route-details">
      <h1 className="route-details__title">
        {name}
      </h1>

      {description && (
        <p className="route-details__description">
          {description}
        </p>
      )}

      <dl className="route-details__data">
        <div>
          <dt>Partenza</dt>
          <dd>{startLocation}</dd>
        </div>

        <div>
          <dt>Arrivo</dt>
          <dd>{endLocation}</dd>
        </div>

        <div>
          <dt>Distanza</dt>
          <dd>{distanceKm} km</dd>
        </div>

        <div>
          <dt>Difficoltà</dt>
          <dd>{formatRouteDifficulty(difficulty)}</dd>
        </div>

        <div>
          <dt>Creato il</dt>
          <dd>
            <time dateTime={createdAt}>
              {formatDate(createdAt)}
            </time>
          </dd>
        </div>

        <div>
          <dt>Aggiornato il</dt>
          <dd>
            <time dateTime={updatedAt}>
              {formatDate(updatedAt)}
            </time>
          </dd>
        </div>
      </dl>
    </article>
  );
}
