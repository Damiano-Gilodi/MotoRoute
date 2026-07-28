import {useState} from "react";
import {validateCreateRoute} from "./createRouteValidation.js";

const initialValues = {
  name: "",
  description: "",
  startLocation: "",
  endLocation: "",
  distanceKm: "",
  difficulty: "",
};

export function CreateRouteForm({
                                  onSubmit,
                                  isSubmitting = false,
                                  serverErrors = {},
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

    const validationErrors = validateCreateRoute(values);

    setClientErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    onSubmit({
      ...values,
      name: values.name.trim(),
      description: values.description.trim(),
      startLocation: values.startLocation.trim(),
      endLocation: values.endLocation.trim(),
      distanceKm: Number(values.distanceKm),
    });
  }

  return (
    <section>
      <h1>Crea itinerario</h1>

      <form onSubmit={handleSubmit} noValidate>
        <div>
          <label htmlFor="route-name">Nome</label>
          <input
            id="route-name"
            name="name"
            type="text"
            value={values.name}
            onChange={handleChange}
          />

          {errors.name && (
            <p role="alert">{errors.name}</p>
          )}
        </div>

        <div>
          <label htmlFor="route-description">Descrizione</label>
          <textarea
            id="route-description"
            name="description"
            value={values.description}
            onChange={handleChange}
          />

          {errors.description && (
            <p role="alert">{errors.description}</p>
          )}
        </div>

        <div>
          <label htmlFor="route-start-location">
            Luogo di partenza
          </label>
          <input
            id="route-start-location"
            name="startLocation"
            type="text"
            value={values.startLocation}
            onChange={handleChange}
          />

          {errors.startLocation && (
            <p role="alert">{errors.startLocation}</p>
          )}
        </div>

        <div>
          <label htmlFor="route-end-location">
            Luogo di arrivo
          </label>
          <input
            id="route-end-location"
            name="endLocation"
            type="text"
            value={values.endLocation}
            onChange={handleChange}
          />

          {errors.endLocation && (
            <p role="alert">{errors.endLocation}</p>
          )}
        </div>

        <div>
          <label htmlFor="route-distance">
            Distanza in chilometri
          </label>
          <input
            id="route-distance"
            name="distanceKm"
            type="number"
            min="0"
            step="0.01"
            value={values.distanceKm}
            onChange={handleChange}
          />

          {errors.distanceKm && (
            <p role="alert">{errors.distanceKm}</p>
          )}
        </div>

        <div>
          <label htmlFor="route-difficulty">Difficoltà</label>
          <select
            id="route-difficulty"
            name="difficulty"
            value={values.difficulty}
            onChange={handleChange}
          >
            <option value="">Seleziona una difficoltà</option>
            <option value="EASY">Facile</option>
            <option value="MEDIUM">Media</option>
            <option value="HARD">Difficile</option>
          </select>

          {errors.difficulty && (
            <p role="alert">{errors.difficulty}</p>
          )}
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting
            ? "Creazione in corso..."
            : "Crea itinerario"}
        </button>
      </form>
    </section>
  );
}
