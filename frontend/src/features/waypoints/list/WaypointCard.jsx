import {formatRouteDate} from "../../routes/utils/routeFormatters.js";

export function WaypointCard({waypoint}) {

  const titleId = `waypoint-title-${waypoint.id}`;

  return (
    <article aria-labelledby={titleId}>
      <h2 id={titleId}>{waypoint.name}</h2>

      <dl>
        <div>
          <dt>Posizione</dt>
          <dd>{waypoint.position}</dd>
        </div>

        <div>
          <dt>Latitudine</dt>
          <dd>{waypoint.latitude} </dd>
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
    </article>
  )
}
