import {render, screen} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {
  describe,
  expect,
  test,
  vi,
} from "vitest";

import {RouteForm} from "./RouteForm.jsx";

const initialValues = {
  name: "Passo dello Stelvio",
  description: "Percorso panoramico",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: "47.50",
  difficulty: "HARD",
};

function renderRouteForm(props = {}) {
  const onSubmit = vi.fn();

  render(
    <RouteForm
      onSubmit={onSubmit}
      {...props}
    />,
  );

  return {
    onSubmit,
  };
}

describe("RouteForm", () => {
  test(
    "should display all route fields and the submit button",
    () => {
      renderRouteForm();

      expect(
        screen.getByLabelText("Nome"),
      ).toBeInTheDocument();

      expect(
        screen.getByLabelText("Descrizione"),
      ).toBeInTheDocument();

      expect(
        screen.getByLabelText("Partenza"),
      ).toBeInTheDocument();

      expect(
        screen.getByLabelText("Arrivo"),
      ).toBeInTheDocument();

      expect(
        screen.getByLabelText("Distanza (km)"),
      ).toBeInTheDocument();

      expect(
        screen.getByLabelText("Difficoltà"),
      ).toBeInTheDocument();

      expect(
        screen.getByRole("button", {
          name: "Salva itinerario",
        }),
      ).toBeInTheDocument();
    },
  );

  test(
    "should display initial route values",
    () => {
      renderRouteForm({
        initialValues,
        submitLabel: "Salva modifiche",
      });

      expect(
        screen.getByLabelText("Nome"),
      ).toHaveValue(
        "Passo dello Stelvio",
      );

      expect(
        screen.getByLabelText("Descrizione"),
      ).toHaveValue(
        "Percorso panoramico",
      );

      expect(
        screen.getByLabelText("Partenza"),
      ).toHaveValue("Bormio");

      expect(
        screen.getByLabelText("Arrivo"),
      ).toHaveValue(
        "Prato allo Stelvio",
      );

      expect(
        screen.getByLabelText("Distanza (km)"),
      ).toHaveValue(47.5);

      expect(
        screen.getByLabelText("Difficoltà"),
      ).toHaveValue("HARD");

      expect(
        screen.getByRole("button", {
          name: "Salva modifiche",
        }),
      ).toBeInTheDocument();
    },
  );

  test(
    "should update fields when the user enters route data",
    async () => {
      const user = userEvent.setup();

      renderRouteForm();

      await user.type(
        screen.getByLabelText("Nome"),
        "Passo dello Stelvio",
      );

      await user.type(
        screen.getByLabelText("Descrizione"),
        "Percorso panoramico",
      );

      await user.type(
        screen.getByLabelText("Partenza"),
        "Bormio",
      );

      await user.type(
        screen.getByLabelText("Arrivo"),
        "Prato allo Stelvio",
      );

      await user.type(
        screen.getByLabelText("Distanza (km)"),
        "47.50",
      );

      await user.selectOptions(
        screen.getByLabelText("Difficoltà"),
        "HARD",
      );

      expect(
        screen.getByLabelText("Nome"),
      ).toHaveValue(
        "Passo dello Stelvio",
      );

      expect(
        screen.getByLabelText("Descrizione"),
      ).toHaveValue(
        "Percorso panoramico",
      );

      expect(
        screen.getByLabelText("Partenza"),
      ).toHaveValue("Bormio");

      expect(
        screen.getByLabelText("Arrivo"),
      ).toHaveValue(
        "Prato allo Stelvio",
      );

      expect(
        screen.getByLabelText("Distanza (km)"),
      ).toHaveValue(47.5);

      expect(
        screen.getByLabelText("Difficoltà"),
      ).toHaveValue("HARD");
    },
  );

  test(
    "should block submission and show validation errors when values are invalid",
    async () => {
      const user = userEvent.setup();

      const {onSubmit} =
        renderRouteForm();

      await user.click(
        screen.getByRole("button", {
          name: "Salva itinerario",
        }),
      );

      expect(onSubmit)
        .not.toHaveBeenCalled();

      expect(
        screen.getByText(
          "Il nome è obbligatorio",
        ),
      ).toBeInTheDocument();

      expect(
        screen.getByText(
          "Il luogo di partenza è obbligatorio",
        ),
      ).toBeInTheDocument();

      expect(
        screen.getByText(
          "Il luogo di arrivo è obbligatorio",
        ),
      ).toBeInTheDocument();

      expect(
        screen.getByText(
          "La distanza deve essere maggiore di zero",
        ),
      ).toBeInTheDocument();

      expect(
        screen.getByText(
          "La difficoltà è obbligatoria",
        ),
      ).toBeInTheDocument();
    },
  );

  test(
    "should submit the normalized route payload when values are valid",
    async () => {
      const user = userEvent.setup();

      const {onSubmit} =
        renderRouteForm();

      await user.type(
        screen.getByLabelText("Nome"),
        "  Passo dello Stelvio  ",
      );

      await user.type(
        screen.getByLabelText("Descrizione"),
        "  Percorso panoramico  ",
      );

      await user.type(
        screen.getByLabelText("Partenza"),
        "  Bormio  ",
      );

      await user.type(
        screen.getByLabelText("Arrivo"),
        "  Prato allo Stelvio  ",
      );

      await user.type(
        screen.getByLabelText("Distanza (km)"),
        "47.50",
      );

      await user.selectOptions(
        screen.getByLabelText("Difficoltà"),
        "HARD",
      );

      await user.click(
        screen.getByRole("button", {
          name: "Salva itinerario",
        }),
      );

      expect(onSubmit)
        .toHaveBeenCalledTimes(1);

      expect(onSubmit)
        .toHaveBeenCalledWith({
          name: "Passo dello Stelvio",
          description:
            "Percorso panoramico",
          startLocation: "Bormio",
          endLocation:
            "Prato allo Stelvio",
          distanceKm: 47.5,
          difficulty: "HARD",
        });
    },
  );

  test(
    "should display server validation errors",
    () => {
      renderRouteForm({
        serverErrors: {
          name:
            "must not be blank",
          distanceKm:
            "must be greater than 0",
        },
      });

      expect(
        screen.getByText(
          "must not be blank",
        ),
      ).toBeInTheDocument();

      expect(
        screen.getByText(
          "must be greater than 0",
        ),
      ).toBeInTheDocument();
    },
  );

  test(
    "should disable fields and show submitting state",
    () => {
      renderRouteForm({
        isSubmitting: true,
        submitLabel: "Salva modifiche",
      });

      expect(
        screen.getByLabelText("Nome"),
      ).toBeDisabled();

      expect(
        screen.getByLabelText("Difficoltà"),
      ).toBeDisabled();

      expect(
        screen.getByRole("button", {
          name: "Salvataggio...",
        }),
      ).toBeDisabled();
    },
  );
});
