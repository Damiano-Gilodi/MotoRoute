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
          <dd>{difficulty}</dd>
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

function formatDate(date) {
  return new Intl.DateTimeFormat("it-IT", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(date));
}
