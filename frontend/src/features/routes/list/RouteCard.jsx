import {Link} from "react-router";

const difficultyLabels = {
  EASY: "Facile",
  MEDIUM: "Media",
  HARD: "Difficile",
};

function formatDate(value) {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat("it-IT", {
    dateStyle: "medium",
  }).format(date);
}

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
            {difficultyLabels[route.difficulty] ??
              route.difficulty}
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

      <Link to={`/routes/${route.id}`}>
        Visualizza dettagli
      </Link>
    </article>
  );
}
