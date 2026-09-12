const allowedDifficulties = [
  "EASY",
  "MEDIUM",
  "HARD",
];

export function validateRoute(values) {
  const errors = {};

  const name = values.name.trim();
  const description =
    values.description.trim();
  const startLocation =
    values.startLocation.trim();
  const endLocation =
    values.endLocation.trim();
  const distanceKm =
    Number(values.distanceKm);

  if (!name) {
    errors.name =
      "Il nome è obbligatorio";
  } else if (name.length > 120) {
    errors.name =
      "Il nome non può superare 120 caratteri";
  }

  if (description.length > 2000) {
    errors.description =
      "La descrizione non può superare 2000 caratteri";
  }

  if (!startLocation) {
    errors.startLocation =
      "Il luogo di partenza è obbligatorio";
  } else if (
    startLocation.length > 120
  ) {
    errors.startLocation =
      "Il luogo di partenza non può superare 120 caratteri";
  }

  if (!endLocation) {
    errors.endLocation =
      "Il luogo di arrivo è obbligatorio";
  } else if (
    endLocation.length > 120
  ) {
    errors.endLocation =
      "Il luogo di arrivo non può superare 120 caratteri";
  }

  if (
    values.distanceKm.trim() === "" ||
    !Number.isFinite(distanceKm) ||
    distanceKm <= 0
  ) {
    errors.distanceKm =
      "La distanza deve essere maggiore di zero";
  }

  if (!values.difficulty) {
    errors.difficulty =
      "La difficoltà è obbligatoria";
  } else if (
    !allowedDifficulties.includes(
      values.difficulty,
    )
  ) {
    errors.difficulty =
      "La difficoltà selezionata non è valida";
  }

  return errors;
}

export function normalizeRouteValues(values) {
  return {
    name: values.name.trim(),
    description:
      values.description.trim() || null,
    startLocation:
      values.startLocation.trim(),
    endLocation:
      values.endLocation.trim(),
    distanceKm:
      Number(values.distanceKm),
    difficulty:
    values.difficulty,
  };
}
