const difficultyLabels = {
  EASY: "Facile",
  MEDIUM: "Media",
  HARD: "Difficile",
};

export function formatRouteDifficulty(difficulty) {
  return difficultyLabels[difficulty] ?? difficulty;
}

function parseDate(value) {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return null;
  }

  return date;
}

export function formatRouteDate(value) {
  const date = parseDate(value);

  if (!date) {
    return value;
  }

  return new Intl.DateTimeFormat("it-IT", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  }).format(date);
}

export function formatRouteDateTime(value) {
  const date = parseDate(value);

  if (!date) {
    return value;
  }

  return new Intl.DateTimeFormat("it-IT", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}
