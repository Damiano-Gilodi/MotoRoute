import {useState} from "react";

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

  function handleChange(event) {
    const {name, value} = event.target;

    setValues((currentValues) => ({
      ...currentValues,
      [name]: value,
    }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    onSubmit(values);
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

          {serverErrors.name && (
            <p role="alert">{serverErrors.name}</p>
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

          {serverErrors.description && (
            <p role="alert">{serverErrors.description}</p>
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

          {serverErrors.startLocation && (
            <p role="alert">{serverErrors.startLocation}</p>
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

          {serverErrors.endLocation && (
            <p role="alert">{serverErrors.endLocation}</p>
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

          {serverErrors.distanceKm && (
            <p role="alert">{serverErrors.distanceKm}</p>
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

          {serverErrors.difficulty && (
            <p role="alert">{serverErrors.difficulty}</p>
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
