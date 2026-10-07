export function validateWaypoint(values) {
  const errors = {};

  const name = values.name.trim();
  const description = values.description.trim();
  const position = Number(values.position);

  if (!name) {
    errors.name = "Il nome è obbligatorio";
  } else if (name.length > 120) {
    errors.name = "Il nome non può superare 120 caratteri";
  }

  if (description.length > 2000) {
    errors.description = "La descrizione non può superare 2000 caratteri";
  }

  if (values.position.trim() === "") {
    errors.position = "La posizione è obbligatoria";
  } else if (!Number.isInteger(position)) {
    errors.position = "La posizione deve essere un numero intero";
  } else if (position <= 0) {
    errors.position = "La posizione deve essere maggiore di zero";
  }

  const latitudeError = validateCoordinate(
    values.latitude,
    "latitudine",
    -90,
    90,
  );

  if (latitudeError) {
    errors.latitude = latitudeError;
  }

  const longitudeError = validateCoordinate(
    values.longitude,
    "longitudine",
    -180,
    180,
  );

  if (longitudeError) {
    errors.longitude = longitudeError;
  }

  return errors;
}

function validateCoordinate(value, fieldName, min, max) {

  const numericValue = Number(value.trim());

  if (value.trim() === "") {
    return `La ${fieldName} è obbligatoria`;
  }

  const coordinatePattern = /^[+-]?(?:\d+(?:\.\d*)?|\.\d+)$/;

  if (
    !Number.isFinite(numericValue) ||
    !coordinatePattern.test(value.trim())
  ) {
    return `La ${fieldName} deve essere un numero valido`;
  }

  if (
    numericValue < min ||
    numericValue > max
  ) {
    return `La ${fieldName} deve essere compresa tra ${min} e ${max}`;
  }

  const decimalPart = value.trim().split(".")[1] ?? "";

  if (decimalPart.length > 6) {
    return `La ${fieldName} deve avere al massimo 6 decimali`;
  }

  return null;
}

export function normalizeWaypointValues(values) {
  return {
    name: values.name.trim(),
    description: values.description.trim() || null,
    position: Number(values.position),
    latitude: Number(values.latitude),
    longitude: Number(values.longitude),
  };
}
