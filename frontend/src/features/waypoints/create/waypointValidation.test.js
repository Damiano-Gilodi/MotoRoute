import {normalizeWaypointValues, validateWaypoint} from "./waypointValidation.js";
import {describe, expect, test} from "vitest";

const validValues = {
  name: "Punto di interesse",
  description: "Descrizione del punto di interesse",
  position: "1",
  latitude: "45.07",
  longitude: "9.07",
}

describe("validateWaypoint", () => {

  test("should return no errors when values is valid", () => {

    expect(validateWaypoint(validValues)).toEqual({});
  })

  test("should require the waypoint name", () => {

    const errors = validateWaypoint({
      ...validValues,
      name: "  ",
    })

    expect(errors.name).toBe("Il nome è obbligatorio");
  })

  test("should reject a name longer than 120 characters", () => {

    const errors = validateWaypoint({
      ...validValues,
      name: "a".repeat(121),
    })

    expect(errors.name).toBe("Il nome non può superare 120 caratteri");
  })

  test("should reject a description longer than 2000 characters", () => {

    const errors = validateWaypoint({
      ...validValues,
      description: "a".repeat(2001),
    })

    expect(errors.description).toBe("La descrizione non può superare 2000 caratteri");
  })

  test("should require the position", () => {

    const errors = validateWaypoint({
      ...validValues,
      position: "  ",
    })

    expect(errors.position).toBe("La posizione è obbligatoria");
  })

  test.each(
    ["-1",
      "0",]
  )("should reject a non-positive position: %s", (position) => {

    const errors = validateWaypoint({
      ...validValues,
      position: position,
    })

    expect(errors.position).toBe("La posizione deve essere maggiore di zero");
  })

  test.each([
    "1.5",
    "2.7",
    "abc"]
  )("should reject an invalid position: %s", (position) => {
      const errors = validateWaypoint({
        ...validValues,
        position,
      });

      expect(errors.position).toBe("La posizione deve essere un numero intero",);
    }
  )

  test("should accept zero coordinates", () => {
    const errors = validateWaypoint({
      ...validValues,
      latitude: "0",
      longitude: "0",
    });

    expect(errors).toEqual({});
  })

  test("should reject non-numeric coordinates", () => {
    const errors = validateWaypoint({
      ...validValues,
      latitude: "abc",
      longitude: "invalid",
    })

    expect(errors.latitude).toBe("La latitudine deve essere un numero valido");
    expect(errors.longitude).toBe("La longitudine deve essere un numero valido");
  })

  test("should require the latitude", () => {

    const errors = validateWaypoint({
      ...validValues,
      latitude: "  ",
    })

    expect(errors.latitude).toBe("La latitudine è obbligatoria");
  })

  test.each(
    ["-91",
      "91",
      "-90.000001",
      "90.000001",
    ]
  )("should reject a latitude not between -90 and 90", (latitude) => {

    const errors = validateWaypoint({
      ...validValues,
      latitude: latitude,
    })

    expect(errors.latitude).toBe("La latitudine deve essere compresa tra -90 e 90");
  })

  test.each(
    ["-25.0000001",
      "25.0000001",
      "-89.0000034",
      "89.0000034",
    ]
  )("should reject a latitude not limited at 6 decimal places", (latitude) => {

    const errors = validateWaypoint({
      ...validValues,
      latitude: latitude,
    })

    expect(errors.latitude).toBe("La latitudine deve avere al massimo 6 decimali");
  })

  test("should require the longitude", () => {

    const errors = validateWaypoint({
      ...validValues,
      longitude: "  ",
    })

    expect(errors.longitude).toBe("La longitudine è obbligatoria");
  })

  test.each(
    ["-181",
      "181",
      "-180.000001",
      "180.000001",
    ]
  )("should reject a longitude not between -180 and 180", (longitude) => {

    const errors = validateWaypoint({
      ...validValues,
      longitude: longitude,
    })

    expect(errors.longitude).toBe("La longitudine deve essere compresa tra -180 e 180");
  })

  test.each(
    ["-25.0000001",
      "25.0000001",
      "-89.0000034",
      "89.0000034",
    ]
  )("should reject a longitudine not limited at 6 decimal places", (longitude) => {

    const errors = validateWaypoint({
      ...validValues,
      longitude: longitude,
    })

    expect(errors.longitude).toBe("La longitudine deve avere al massimo 6 decimali");
  })

  test("should normalize waypoint values", () => {
    const normalized = normalizeWaypointValues({
      name: "  Punto panoramico  ",
      description: "   ",
      position: "1",
      latitude: "45.123456",
      longitude: "8.765432",
    });

    expect(normalized).toEqual({
      name: "Punto panoramico",
      description: null,
      position: 1,
      latitude: 45.123456,
      longitude: 8.765432,
    });
  });

  test("should preserve a non-empty description", () => {
    const normalized = normalizeWaypointValues({
      name: "Punto panoramico",
      description: "  Vista sulle montagne  ",
      position: "1",
      latitude: "0",
      longitude: "0",
    });

    expect(normalized.description).toBe(
      "Vista sulle montagne",
    );

    expect(normalized.latitude).toBe(0);
    expect(normalized.longitude).toBe(0);
  });
})
