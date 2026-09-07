import {render, screen} from "@testing-library/react";
import {
  describe,
  expect,
  test,
} from "vitest";
import {RouteDetails} from "./RouteDetails.jsx";

const route = {
  id: "11111111-1111-1111-1111-111111111111",
  name: "Passo dello Stelvio",
  description: "Percorso panoramico",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: 47.5,
  difficulty: "HARD",
  createdAt: "2026-07-29T10:00:00Z",
  updatedAt: "2026-07-29T12:30:00Z",
};

describe("RouteDetails", () => {
  test("should render all route details", () => {
    render(<RouteDetails route={route}/>);

    expect(
      screen.getByRole("heading", {
        level: 1,
        name: "Passo dello Stelvio",
      }),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Percorso panoramico"),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Bormio"),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Prato allo Stelvio"),
    ).toBeInTheDocument();

    expect(
      screen.getByText("47.5 km"),
    ).toBeInTheDocument();

    expect(
      screen.getByText("HARD"),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Partenza"),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Arrivo"),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Distanza"),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Difficoltà"),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Creato il"),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Aggiornato il"),
    ).toBeInTheDocument();
  });

  test("should not render description when it is missing", () => {
    render(
      <RouteDetails
        route={{
          ...route,
          description: null,
        }}
      />,
    );

    expect(
      screen.queryByText("Percorso panoramico"),
    ).not.toBeInTheDocument();
  });
});
