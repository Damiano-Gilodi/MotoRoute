import {render, screen} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {describe, expect, test, vi} from "vitest";

import {WaypointForm} from "./WaypointForm.jsx";

const initialValues = {
  name: "Punto panoramico",
  description: "Vista sulle montagne",
  position: "2",
  latitude: "45.123456",
  longitude: "8.765432"
};

function renderWaypointForm(props = {}) {
  const onSubmit = vi.fn();

  render(<WaypointForm onSubmit={onSubmit} {...props}/>);

  return {onSubmit};
}

describe("WaypointForm", () => {

  test("should render empty waypoint fields", () => {
    renderWaypointForm();

    expect(screen.getByLabelText("Nome")).toHaveValue("");
    expect(screen.getByLabelText("Descrizione")).toHaveValue("");
    expect(screen.getByLabelText("Posizione")).toHaveValue(null);
    expect(screen.getByLabelText("Latitudine")).toHaveValue(null);
    expect(screen.getByLabelText("Longitudine")).toHaveValue(null);
    expect(screen.getByRole("button", {name: "Salva waypoint"})).toBeEnabled();
  });

  test("should display initial waypoint values", () => {
    renderWaypointForm({
      initialValues,
      submitLabel: "Salva modifiche"
    });

    expect(screen.getByLabelText("Nome")).toHaveValue("Punto panoramico");
    expect(screen.getByLabelText("Descrizione")).toHaveValue("Vista sulle montagne");
    expect(screen.getByLabelText("Posizione")).toHaveValue(2);
    expect(screen.getByLabelText("Latitudine")).toHaveValue(45.123456);
    expect(screen.getByLabelText("Longitudine")).toHaveValue(8.765432);
    expect(screen.getByRole("button", {name: "Salva modifiche"})).toBeEnabled();
  });

  test("should update fields when the user enters waypoint data", async () => {
    const user = userEvent.setup();

    renderWaypointForm();

    await user.type(screen.getByLabelText("Nome"), "Punto panoramico");
    await user.type(screen.getByLabelText("Descrizione"), "Vista sulle montagne");
    await user.type(screen.getByLabelText("Posizione"), "2");
    await user.type(screen.getByLabelText("Latitudine"), "45.123456");
    await user.type(screen.getByLabelText("Longitudine"), "8.765432");

    expect(screen.getByLabelText("Nome")).toHaveValue("Punto panoramico");
    expect(screen.getByLabelText("Descrizione")).toHaveValue("Vista sulle montagne");
    expect(screen.getByLabelText("Posizione")).toHaveValue(2);
    expect(screen.getByLabelText("Latitudine")).toHaveValue(45.123456);
    expect(screen.getByLabelText("Longitudine")).toHaveValue(8.765432);
  });

  test("should block submission and show validation errors when values are invalid", async () => {
    const user = userEvent.setup();
    const {onSubmit} = renderWaypointForm();

    await user.click(screen.getByRole("button", {name: "Salva waypoint"}));

    expect(onSubmit).not.toHaveBeenCalled();

    expect(screen.getByText("Il nome è obbligatorio")).toBeInTheDocument();
    expect(screen.getByText("La posizione è obbligatoria")).toBeInTheDocument();
    expect(screen.getByText("La latitudine è obbligatoria")).toBeInTheDocument();
    expect(screen.getByText("La longitudine è obbligatoria")).toBeInTheDocument();
  });

  test("should submit the normalized waypoint payload when values are valid", async () => {
    const user = userEvent.setup();
    const {onSubmit} = renderWaypointForm();

    await user.type(screen.getByLabelText("Nome"), "  Punto panoramico  ");
    await user.type(screen.getByLabelText("Descrizione"), "  Vista sulle montagne  ");
    await user.type(screen.getByLabelText("Posizione"), "2");
    await user.type(screen.getByLabelText("Latitudine"), "45.123456");
    await user.type(screen.getByLabelText("Longitudine"), "8.765432");

    await user.click(screen.getByRole("button", {name: "Salva waypoint"}));

    expect(onSubmit).toHaveBeenCalledTimes(1);
    expect(onSubmit).toHaveBeenCalledWith({
      name: "Punto panoramico",
      description: "Vista sulle montagne",
      position: 2,
      latitude: 45.123456,
      longitude: 8.765432
    });
  });

  test("should normalize an empty description to null", async () => {
    const user = userEvent.setup();
    const {onSubmit} = renderWaypointForm({
      initialValues: {
        ...initialValues,
        description: ""
      }
    });

    await user.click(screen.getByRole("button", {name: "Salva waypoint"}));

    expect(onSubmit).toHaveBeenCalledWith({
      name: "Punto panoramico",
      description: null,
      position: 2,
      latitude: 45.123456,
      longitude: 8.765432
    });
  });

  test("should clear a client error when the corresponding field changes", async () => {
    const user = userEvent.setup();
    renderWaypointForm();

    await user.click(screen.getByRole("button", {name: "Salva waypoint"}));

    expect(screen.getByText("Il nome è obbligatorio")).toBeInTheDocument();

    await user.type(screen.getByLabelText("Nome"), "Punto panoramico");

    expect(screen.queryByText("Il nome è obbligatorio")).not.toBeInTheDocument();
    expect(screen.getByText("La posizione è obbligatoria")).toBeInTheDocument();
  });

  test("should display server validation errors", () => {
    renderWaypointForm({
      serverErrors: {
        name: "must not be blank",
        position: "must be greater than 0"
      }
    });

    expect(screen.getByText("must not be blank")).toBeInTheDocument();
    expect(screen.getByText("must be greater than 0")).toBeInTheDocument();

    expect(screen.getByLabelText("Nome")).toHaveAttribute("aria-invalid", "true");
    expect(screen.getByLabelText("Posizione")).toHaveAttribute("aria-invalid", "true");
  });

  test("should disable fields and show submitting state", () => {
    renderWaypointForm({
      isSubmitting: true,
      submitLabel: "Salva modifiche"
    });

    expect(screen.getByLabelText("Nome")).toBeDisabled();
    expect(screen.getByLabelText("Descrizione")).toBeDisabled();
    expect(screen.getByLabelText("Posizione")).toBeDisabled();
    expect(screen.getByLabelText("Latitudine")).toBeDisabled();
    expect(screen.getByLabelText("Longitudine")).toBeDisabled();

    expect(screen.getByRole("button", {name: "Salvataggio in corso..."})).toBeDisabled();
  });

});
