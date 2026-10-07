import {render, screen} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {MemoryRouter, Route, Routes} from "react-router";
import {delay, http, HttpResponse} from "msw";
import {describe, expect, test} from "vitest";

import {server} from "../../../test/server.js";
import {WaypointEditPage} from "./WaypointEditPage.jsx";

const routeId = "11111111-1111-1111-1111-111111111111";
const waypointId = "22222222-2222-2222-2222-222222222222";

const apiUrl = new URL(
  `/api/routes/${routeId}/waypoints/${waypointId}`,
  window.location.origin
).toString();

const existingWaypoint = {
  id: waypointId,
  routeId,
  name: "Punta de la Marmolada",
  description: "Punto di osservazione panoramico",
  position: 1,
  latitude: 45.9167,
  longitude: 10.1167,
  createdAt: "2026-07-29T10:00:00Z",
  updatedAt: "2026-07-29T10:00:00Z"
};

function renderWaypointEditPage() {
  render(
    <MemoryRouter initialEntries={[`/routes/${routeId}/waypoints/${waypointId}/edit`]}>
      <Routes>
        <Route
          path="/routes/:routeId/waypoints/:waypointId/edit"
          element={<WaypointEditPage/>}
        />

        <Route
          path="/routes/:routeId/waypoints"
          element={<p>Elenco waypoint</p>}
        />
      </Routes>
    </MemoryRouter>
  );
}

function useExistingWaypointHandler() {
  server.use(
    http.get(apiUrl, () => HttpResponse.json(existingWaypoint, {status: 200}))
  );
}

describe("WaypointEditPage", () => {
  test("should load the waypoint and prefill the form", async () => {
    useExistingWaypointHandler();

    renderWaypointEditPage();

    expect(screen.getByText("Caricamento waypoint...")).toBeInTheDocument();

    expect(
      await screen.findByRole("heading", {name: "Modifica waypoint"})
    ).toBeInTheDocument();

    expect(screen.getByLabelText("Nome")).toHaveValue("Punta de la Marmolada");
    expect(screen.getByLabelText("Descrizione")).toHaveValue("Punto di osservazione panoramico");
    expect(screen.getByLabelText("Posizione")).toHaveValue(1);
    expect(screen.getByLabelText("Latitudine")).toHaveValue(45.9167);
    expect(screen.getByLabelText("Longitudine")).toHaveValue(10.1167);
    expect(screen.getByRole("button", {name: "Salva modifiche"})).toBeEnabled();
  });

  test("should update the waypoint and navigate to waypoint list", async () => {
    useExistingWaypointHandler();

    let receivedPayload;

    const updatedWaypoint = {
      ...existingWaypoint,
      name: "Marmolada aggiornata",
      description: "Descrizione aggiornata",
      position: 2,
      latitude: 46.123456,
      longitude: 11.123456,
      updatedAt: "2026-07-29T10:30:00Z"
    };

    server.use(
      http.put(apiUrl, async ({request}) => {
        receivedPayload = await request.json();
        return HttpResponse.json(updatedWaypoint, {status: 200});
      })
    );

    const user = userEvent.setup();

    renderWaypointEditPage();

    const nameInput = await screen.findByLabelText("Nome");
    const descriptionInput = screen.getByLabelText("Descrizione");
    const positionInput = screen.getByLabelText("Posizione");
    const latitudeInput = screen.getByLabelText("Latitudine");
    const longitudeInput = screen.getByLabelText("Longitudine");

    await user.clear(nameInput);
    await user.type(nameInput, "Marmolada aggiornata");

    await user.clear(descriptionInput);
    await user.type(descriptionInput, "Descrizione aggiornata");

    await user.clear(positionInput);
    await user.type(positionInput, "2");

    await user.clear(latitudeInput);
    await user.type(latitudeInput, "46.123456");

    await user.clear(longitudeInput);
    await user.type(longitudeInput, "11.123456");

    await user.click(screen.getByRole("button", {name: "Salva modifiche"}));

    expect(receivedPayload).toEqual({
      name: "Marmolada aggiornata",
      description: "Descrizione aggiornata",
      position: 2,
      latitude: 46.123456,
      longitude: 11.123456
    });

    expect(await screen.findByText("Elenco waypoint")).toBeInTheDocument();
  });

  test("should show submitting state while the waypoint is being updated", async () => {
    useExistingWaypointHandler();

    server.use(
      http.put(apiUrl, async () => {
        await delay(100);
        return HttpResponse.json(existingWaypoint, {status: 200});
      })
    );

    const user = userEvent.setup();

    renderWaypointEditPage();

    await screen.findByLabelText("Nome");

    await user.click(screen.getByRole("button", {name: "Salva modifiche"}));

    expect(screen.getByRole("button", {name: "Salvataggio in corso..."})).toBeDisabled();
    expect(await screen.findByText("Elenco waypoint")).toBeInTheDocument();
  });

  test("should show an error when the waypoint does not exist", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 404,
            error: "Not Found",
            message: "Waypoint not found with id: " + waypointId,
            path: `/api/routes/${routeId}/waypoints/${waypointId}`,
            fieldErrors: {}
          },
          {status: 404}
        )
      )
    );

    renderWaypointEditPage();

    expect(await screen.findByRole("heading", {name: "Modifica waypoint"})).toBeInTheDocument();
    expect(screen.getByRole("alert")).toHaveTextContent("Waypoint not found with id: " + waypointId);
    expect(screen.queryByRole("button", {name: "Salva modifiche"})).not.toBeInTheDocument();
  });

  test("should show a generic error when loading the waypoint fails", async () => {
    server.use(
      http.get(apiUrl, () =>
        new HttpResponse("Internal server error", {
          status: 500,
          headers: {"Content-Type": "text/plain"}
        })
      )
    );

    renderWaypointEditPage();

    expect(await screen.findByRole("alert")).toHaveTextContent("Impossibile caricare il waypoint");
    expect(screen.queryByRole("button", {name: "Salva modifiche"})).not.toBeInTheDocument();
  });

  test("should show field errors returned when the update request is invalid", async () => {
    useExistingWaypointHandler();

    server.use(
      http.put(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:30:00Z",
            status: 400,
            error: "Bad Request",
            message: "One or more fields are invalid",
            path: `/api/routes/${routeId}/waypoints/${waypointId}`,
            fieldErrors: {
              name: "must not be blank",
              position: "must be greater than zero"
            }
          },
          {status: 400}
        )
      )
    );

    const user = userEvent.setup();

    renderWaypointEditPage();

    await screen.findByLabelText("Nome");

    await user.click(screen.getByRole("button", {name: "Salva modifiche"}));

    expect(await screen.findByText("One or more fields are invalid")).toBeInTheDocument();
    expect(screen.getByText("must not be blank")).toBeInTheDocument();
    expect(screen.getByText("must be greater than zero")).toBeInTheDocument();
    expect(screen.getByRole("button", {name: "Salva modifiche"})).toBeEnabled();
  });

  test("should show an error when waypoint position is already occupied", async () => {
    useExistingWaypointHandler();

    server.use(
      http.put(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:30:00Z",
            status: 409,
            error: "Conflict",
            message: `Waypoint position 1 already exists in route: ${routeId}`,
            path: `/api/routes/${routeId}/waypoints/${waypointId}`,
            fieldErrors: {}
          },
          {status: 409}
        )
      )
    );

    const user = userEvent.setup();

    renderWaypointEditPage();

    await screen.findByLabelText("Nome");

    await user.click(screen.getByRole("button", {name: "Salva modifiche"}));

    expect(await screen.findByRole("alert")).toHaveTextContent(`Waypoint position 1 already exists in route: ${routeId}`);
    expect(screen.getByRole("button", {name: "Salva modifiche"})).toBeEnabled();
    expect(screen.queryByText("Elenco waypoint")).not.toBeInTheDocument();
  });

  test("should show a generic error when the update response is invalid", async () => {
    useExistingWaypointHandler();

    server.use(
      http.put(apiUrl, () =>
        new HttpResponse("Internal server error", {
          status: 500,
          headers: {"Content-Type": "text/plain"}
        })
      )
    );

    const user = userEvent.setup();

    renderWaypointEditPage();

    await screen.findByLabelText("Nome");

    await user.click(screen.getByRole("button", {name: "Salva modifiche"}));

    expect(await screen.findByRole("alert")).toHaveTextContent("Impossibile aggiornare il waypoint");
    expect(screen.getByRole("button", {name: "Salva modifiche"})).toBeEnabled();
  });
});
