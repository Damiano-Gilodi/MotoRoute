import {
  describe,
  expect,
  test,
} from "vitest";

import {
  normalizeRouteValues,
  validateRoute,
} from "./routeValidation.js";

const validValues = {
  name: "Passo dello Stelvio",
  description: "Percorso panoramico",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: "47.50",
  difficulty: "HARD",
};

describe("validateRoute", () => {
  test(
    "should return no errors when values are valid",
    () => {
      expect(
        validateRoute(validValues),
      ).toEqual({});
    },
  );

  test(
    "should require the route name",
    () => {
      const errors = validateRoute({
        ...validValues,
        name: "  ",
      });

      expect(errors.name).toBe(
        "Il nome è obbligatorio",
      );
    },
  );

  test(
    "should reject a name longer than 120 characters",
    () => {
      const errors = validateRoute({
        ...validValues,
        name: "a".repeat(121),
      });

      expect(errors.name).toBe(
        "Il nome non può superare 120 caratteri",
      );
    },
  );

  test(
    "should reject a description longer than 2000 characters",
    () => {
      const errors = validateRoute({
        ...validValues,
        description:
          "a".repeat(2001),
      });

      expect(
        errors.description,
      ).toBe(
        "La descrizione non può superare 2000 caratteri",
      );
    },
  );

  test(
    "should require the start location",
    () => {
      const errors = validateRoute({
        ...validValues,
        startLocation: "",
      });

      expect(
        errors.startLocation,
      ).toBe(
        "Il luogo di partenza è obbligatorio",
      );
    },
  );

  test(
    "should reject a start location longer than 120 characters",
    () => {
      const errors = validateRoute({
        ...validValues,
        startLocation:
          "a".repeat(121),
      });

      expect(
        errors.startLocation,
      ).toBe(
        "Il luogo di partenza non può superare 120 caratteri",
      );
    },
  );

  test(
    "should require the end location",
    () => {
      const errors = validateRoute({
        ...validValues,
        endLocation: "",
      });

      expect(errors.endLocation)
        .toBe(
          "Il luogo di arrivo è obbligatorio",
        );
    },
  );

  test(
    "should reject an end location longer than 120 characters",
    () => {
      const errors = validateRoute({
        ...validValues,
        endLocation:
          "a".repeat(121),
      });

      expect(
        errors.endLocation,
      ).toBe(
        "Il luogo di arrivo non può superare 120 caratteri",
      );
    },
  );

  test.each([
    "",
    "0",
    "-10",
    "not-a-number",
  ])(
    "should reject invalid distance %s",
    (distanceKm) => {
      const errors = validateRoute({
        ...validValues,
        distanceKm,
      });

      expect(
        errors.distanceKm,
      ).toBe(
        "La distanza deve essere maggiore di zero",
      );
    },
  );

  test(
    "should require a difficulty",
    () => {
      const errors = validateRoute({
        ...validValues,
        difficulty: "",
      });

      expect(
        errors.difficulty,
      ).toBe(
        "La difficoltà è obbligatoria",
      );
    },
  );

  test(
    "should reject an unsupported difficulty",
    () => {
      const errors = validateRoute({
        ...validValues,
        difficulty: "EXTREME",
      });

      expect(
        errors.difficulty,
      ).toBe(
        "La difficoltà selezionata non è valida",
      );
    },
  );

  test(
    "should normalize route values",
    () => {
      expect(
        normalizeRouteValues({
          name: "  Passo dello Stelvio  ",
          description: "   ",
          startLocation: "  Bormio  ",
          endLocation: "  Prato allo Stelvio  ",
          distanceKm: "47.50",
          difficulty: "HARD",
        }),
      ).toEqual({
        name: "Passo dello Stelvio",
        description: null,
        startLocation: "Bormio",
        endLocation: "Prato allo Stelvio",
        distanceKm: 47.5,
        difficulty: "HARD",
      });
    },
  );
});
