import {render, screen} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {
  describe,
  expect,
  test,
  vi,
} from "vitest";

import {CreateRouteForm} from "./CreateRouteForm";

describe("CreateRouteForm", () => {
  test("should display all route fields and the submit button", () => {
    render(
      <CreateRouteForm
        onSubmit={vi.fn()}
        isSubmitting={false}
        serverErrors={{}}
      />,
    );

    expect(
      screen.getByRole("heading", {
        name: "Crea itinerario",
      }),
    ).toBeInTheDocument();

    expect(screen.getByLabelText("Nome")).toBeInTheDocument();
    expect(screen.getByLabelText("Descrizione")).toBeInTheDocument();

    expect(
      screen.getByLabelText("Luogo di partenza"),
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText("Luogo di arrivo"),
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText("Distanza in chilometri"),
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText("Difficoltà"),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", {
        name: "Crea itinerario",
      }),
    ).toBeInTheDocument();
  });

  test("should update fields when the user enters route data", async () => {
    const user = userEvent.setup();

    render(
      <CreateRouteForm
        onSubmit={vi.fn()}
        isSubmitting={false}
        serverErrors={{}}
      />,
    );

    await user.type(
      screen.getByLabelText("Nome"),
      "Passo dello Stelvio",
    );

    await user.type(
      screen.getByLabelText("Descrizione"),
      "Percorso panoramico",
    );

    await user.type(
      screen.getByLabelText("Luogo di partenza"),
      "Bormio",
    );

    await user.type(
      screen.getByLabelText("Luogo di arrivo"),
      "Prato allo Stelvio",
    );

    await user.type(
      screen.getByLabelText("Distanza in chilometri"),
      "47.50",
    );

    await user.selectOptions(
      screen.getByLabelText("Difficoltà"),
      "HARD",
    );

    expect(screen.getByLabelText("Nome"))
      .toHaveValue("Passo dello Stelvio");

    expect(screen.getByLabelText("Descrizione"))
      .toHaveValue("Percorso panoramico");

    expect(screen.getByLabelText("Luogo di partenza"))
      .toHaveValue("Bormio");

    expect(screen.getByLabelText("Luogo di arrivo"))
      .toHaveValue("Prato allo Stelvio");

    expect(screen.getByLabelText("Distanza in chilometri"))
      .toHaveValue(47.5);

    expect(screen.getByLabelText("Difficoltà"))
      .toHaveValue("HARD");
  });
});
