import {
  formatRouteDateTime,
  formatRouteDifficulty,
} from "../utils/routeFormatters.js";

export function RouteDetails({route}) {
  const {
    name,
    description,
    startLocation,
    endLocation,
    distanceKm,
    difficulty,
    createdAt,
    updatedAt,
  } = route;

  return (
    <article>
      <h1>{name}</h1>

      {description && (
        <p>{description}</p>
      )}

      <dl>
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
          <dd>
            {formatRouteDifficulty(
              difficulty,
            )}</dd>
        </div>

        <div>
          <dt>Creato il</dt>
          <dd>
            <time dateTime={createdAt}>
              {formatRouteDateTime(createdAt)}
            </time>
          </dd>
        </div>

        <div>
          <dt>Aggiornato il</dt>
          <dd>
            <time dateTime={updatedAt}>
              {formatRouteDateTime(updatedAt)}
            </time>
          </dd>
        </div>
      </dl>
    </article>
  );
}
