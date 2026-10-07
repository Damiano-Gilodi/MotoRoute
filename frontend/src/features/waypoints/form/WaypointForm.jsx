import {useState} from "react";
import {normalizeWaypointValues, validateWaypoint} from "./waypointValidation.js";

const EMPTY_WAYPOINT_VALUES = {
  name: "",
  description: "",
  position: "",
  latitude: "",
  longitude: "",
};

export function WaypointForm({
                               initialValues = EMPTY_WAYPOINT_VALUES,
                               onSubmit,
                               isSubmitting = false,
                               serverErrors = {},
                               submitLabel = "Salva waypoint",
                             }) {

  const [values, setValues] = useState(initialValues);

  const [clientErrors, setClientErrors] = useState({});

  const errors = {
    ...serverErrors,
    ...clientErrors,
  };

  function handleChange(event) {
    const {name, value} = event.target;

    setValues((currentValues) => ({
      ...currentValues,
      [name]: value,
    }));

    setClientErrors((currentErrors) => {
      if (!currentErrors[name]) {
        return currentErrors;
      }

      const nextErrors = {
        ...currentErrors,
      };

      delete nextErrors[name];

      return nextErrors;
    });
  }

  function handleSubmit(event) {
    event.preventDefault();

    if (isSubmitting) {
      return;
    }

    const validationErrors = validateWaypoint(values);

    setClientErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    const payload =
      normalizeWaypointValues(values);

    onSubmit(payload);
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      <div>
        <label htmlFor="name"> Nome </label>
        <input
          id="name"
          name="name"
          type="text"
          value={values.name}
          onChange={handleChange}
          disabled={isSubmitting}
          aria-invalid={Boolean(errors.name)}
          aria-describedby={errors.name ? "name-error" : undefined}
        />

        {errors.name && (
          <p id="name-error" role="alert">
            {errors.name}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="description"> Descrizione </label>
        <input
          id="description"
          name="description"
          value={values.description}
          onChange={handleChange}
          disabled={isSubmitting}
          aria-invalid={Boolean(errors.description)}
          aria-describedby={errors.description ? "description-error" : undefined}
        />

        {errors.description && (
          <p id="description-error" role="alert">
            {errors.description}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="position"> Posizione </label>
        <input
          id="position"
          name="position"
          type="number"
          min="1"
          step="1"
          value={values.position}
          onChange={handleChange}
          disabled={isSubmitting}
          aria-invalid={Boolean(errors.position)}
          aria-describedby={errors.position ? "position-error" : undefined}
        />

        {errors.position && (
          <p id="position-error" role="alert">
            {errors.position}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="latitude"> Latitudine </label>
        <input
          id="latitude"
          name="latitude"
          type="number"
          value={values.latitude}
          onChange={handleChange}
          step="0.000001"
          min="-90"
          max="90"
          disabled={isSubmitting}
          aria-invalid={Boolean(errors.latitude)}
          aria-describedby={errors.latitude ? "latitude-error" : undefined}
        />

        {errors.latitude && (
          <p id="latitude-error" role="alert">
            {errors.latitude}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="longitude"> Longitudine </label>
        <input
          id="longitude"
          name="longitude"
          type="number"
          value={values.longitude}
          onChange={handleChange}
          step="0.000001"
          min="-180"
          max="180"
          disabled={isSubmitting}
          aria-invalid={Boolean(errors.longitude)}
          aria-describedby={errors.longitude ? "longitude-error" : undefined}
        />

        {errors.longitude && (
          <p id="longitude-error" role="alert">
            {errors.longitude}
          </p>
        )}
      </div>

      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? "Salvataggio in corso..." : submitLabel}
      </button>
    </form>
  )
}
