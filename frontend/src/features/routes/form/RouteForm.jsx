import {useState} from "react";

import {
  normalizeRouteValues,
  validateRoute,
} from "./routeValidation.js";

const EMPTY_ROUTE_VALUES = {
  name: "",
  description: "",
  startLocation: "",
  endLocation: "",
  distanceKm: "",
  difficulty: "",
};

export function RouteForm({
                            initialValues = EMPTY_ROUTE_VALUES,
                            onSubmit,
                            isSubmitting = false,
                            serverErrors = {},
                            submitLabel = "Salva itinerario",
                          }) {
  const [values, setValues] = useState(
    initialValues,
  );

  const [clientErrors, setClientErrors] =
    useState({});

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

    const validationErrors =
      validateRoute(values);

    setClientErrors(validationErrors);

    if (
      Object.keys(validationErrors).length > 0
    ) {
      return;
    }

    onSubmit(
      normalizeRouteValues(values),
    );
  }

  return (
    <form
      onSubmit={handleSubmit}
      noValidate
    >
      <div>
        <label htmlFor="name">
          Nome
        </label>

        <input
          id="name"
          name="name"
          type="text"
          value={values.name}
          onChange={handleChange}
          disabled={isSubmitting}
          aria-invalid={
            Boolean(errors.name)
          }
          aria-describedby={
            errors.name
              ? "name-error"
              : undefined
          }
        />

        {errors.name && (
          <p
            id="name-error"
            role="alert"
          >
            {errors.name}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="description">
          Descrizione
        </label>

        <textarea
          id="description"
          name="description"
          value={values.description}
          onChange={handleChange}
          disabled={isSubmitting}
          aria-invalid={
            Boolean(errors.description)
          }
          aria-describedby={
            errors.description
              ? "description-error"
              : undefined
          }
        />

        {errors.description && (
          <p
            id="description-error"
            role="alert"
          >
            {errors.description}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="startLocation">
          Partenza
        </label>

        <input
          id="startLocation"
          name="startLocation"
          type="text"
          value={values.startLocation}
          onChange={handleChange}
          disabled={isSubmitting}
          aria-invalid={
            Boolean(errors.startLocation)
          }
          aria-describedby={
            errors.startLocation
              ? "startLocation-error"
              : undefined
          }
        />

        {errors.startLocation && (
          <p
            id="startLocation-error"
            role="alert"
          >
            {errors.startLocation}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="endLocation">
          Arrivo
        </label>

        <input
          id="endLocation"
          name="endLocation"
          type="text"
          value={values.endLocation}
          onChange={handleChange}
          disabled={isSubmitting}
          aria-invalid={
            Boolean(errors.endLocation)
          }
          aria-describedby={
            errors.endLocation
              ? "endLocation-error"
              : undefined
          }
        />

        {errors.endLocation && (
          <p
            id="endLocation-error"
            role="alert"
          >
            {errors.endLocation}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="distanceKm">
          Distanza (km)
        </label>

        <input
          id="distanceKm"
          name="distanceKm"
          type="number"
          step="0.01"
          min="0"
          value={values.distanceKm}
          onChange={handleChange}
          disabled={isSubmitting}
          aria-invalid={
            Boolean(errors.distanceKm)
          }
          aria-describedby={
            errors.distanceKm
              ? "distanceKm-error"
              : undefined
          }
        />

        {errors.distanceKm && (
          <p
            id="distanceKm-error"
            role="alert"
          >
            {errors.distanceKm}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="difficulty">
          Difficoltà
        </label>

        <select
          id="difficulty"
          name="difficulty"
          value={values.difficulty}
          onChange={handleChange}
          disabled={isSubmitting}
          aria-invalid={
            Boolean(errors.difficulty)
          }
          aria-describedby={
            errors.difficulty
              ? "difficulty-error"
              : undefined
          }
        >
          <option value="">
            Seleziona una difficoltà
          </option>

          <option value="EASY">
            Facile
          </option>

          <option value="MEDIUM">
            Media
          </option>

          <option value="HARD">
            Difficile
          </option>
        </select>

        {errors.difficulty && (
          <p
            id="difficulty-error"
            role="alert"
          >
            {errors.difficulty}
          </p>
        )}
      </div>

      <button
        type="submit"
        disabled={isSubmitting}
      >
        {isSubmitting
          ? "Salvataggio..."
          : submitLabel}
      </button>
    </form>
  );
}
