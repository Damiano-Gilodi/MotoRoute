import {render, screen} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {MemoryRouter, Route, Routes} from "react-router";
import {delay, http, HttpResponse} from "msw";
import {describe, expect, test} from "vitest";

import {server} from "../../../test/server.js";
import {CreateWaypointPage} from "./CreateWaypointPage.jsx";

const routeId = "11111111-1111-1111-1111-111111111111";
const waypointId = "22222222-2222-2222-2222-222222222222";

const apiUrl = new URL(
  `/api/routes/${routeId}/waypoints`,
  window.location.origin
).toString();

const createdWaypoint = {
  id: waypointId,
  routeId,
  name: "Punto panoramico",
  description: "Vista sulle montagne",
  position: 1,
  latitude: 45.123456,
  longitude: 8.765432,
  createdAt: "2026-07-28T10:00:00Z",
  updatedAt: "2026-07-28T10:00:00Z"
};

function renderCreateWaypointPage() {
  render(
    <MemoryRouter initialEntries={[`/routes/${routeId}/waypoints/new`]}>
      <Routes>
        <Route
          path="/routes/:routeId/waypoints/new"
          element={<CreateWaypointPage/>}
        />

        <Route
          path="/routes/:routeId"
          element={<p>Pagina dettaglio itinerario</p>}
        />
      </Routes>
    </MemoryRouter>
  );
}

async function fillValidForm(user) {
  await user.type(screen.getByLabelText("Nome"), "Punto panoramico");
  await user.type(screen.getByLabelText("Descrizione"), "Vista sulle montagne");
  await user.type(screen.getByLabelText("Posizione"), "1");
  await user.type(screen.getByLabelText("Latitudine"), "45.123456");
  await user.type(screen.getByLabelText("Longitudine"), "8.765432");
}

describe("CreateWaypointPage", () => {

  test("should show loading and navigate to route details after creation", async () => {
    let receivedPayload;

    server.use(
      http.post(apiUrl, async ({request}) => {
        receivedPayload = await request.json();

        await delay(100);

        return HttpResponse.json(createdWaypoint, {
          status: 201,
          headers: {
            Location: `/api/routes/${routeId}/waypoints/${waypointId}`
          }
        });
      })
    );

    const user = userEvent.setup();

    renderCreateWaypointPage();

    await fillValidForm(user);

    await user.click(screen.getByRole("button", {name: "Crea waypoint"}));

    expect(screen.getByRole("button", {name: "Salvataggio in corso..."})).toBeDisabled();

    expect(await screen.findByText("Pagina dettaglio itinerario")).toBeInTheDocument();

    expect(receivedPayload).toEqual({
      name: "Punto panoramico",
      description: "Vista sulle montagne",
      position: 1,
      latitude: 45.123456,
      longitude: 8.765432
    });

    expect(screen.queryByRole("button", {name: "Crea waypoint"})).not.toBeInTheDocument();
  });

  test("should show field errors returned by the API", async () => {
    server.use(
      http.post(apiUrl, () =>
        HttpResponse.json({
          timestamp: "2026-07-28T10:00:00Z",
          status: 400,
          error: "Bad Request",
          message: "One or more fields are invalid",
          path: `/api/routes/${routeId}/waypoints`,
          fieldErrors: {
            name: "must not be blank"
          }
        }, {status: 400})
      )
    );

    const user = userEvent.setup();

    renderCreateWaypointPage();

    await fillValidForm(user);

    await user.click(screen.getByRole("button", {name: "Crea waypoint"}));

    expect(await screen.findByText("One or more fields are invalid")).toBeInTheDocument();
    expect(screen.getByText("must not be blank")).toBeInTheDocument();
    expect(screen.getByRole("button", {name: "Crea waypoint"})).toBeEnabled();
  });

  test("should show an error when the waypoint position is occupied", async () => {
    const conflictMessage = `Waypoint position 1 already exists in route: ${routeId}`;

    server.use(
      http.post(apiUrl, () =>
        HttpResponse.json({
          timestamp: "2026-07-28T10:00:00Z",
          status: 409,
          error: "Conflict",
          message: conflictMessage,
          path: `/api/routes/${routeId}/waypoints`,
          fieldErrors: {}
        }, {status: 409})
      )
    );

    const user = userEvent.setup();

    renderCreateWaypointPage();

    await fillValidForm(user);

    await user.click(screen.getByRole("button", {name: "Crea waypoint"}));

    expect(await screen.findByText(conflictMessage)).toBeInTheDocument();
    expect(screen.getByRole("button", {name: "Crea waypoint"})).toBeEnabled();
  });

  test("should show an error when the route does not exist", async () => {
    const errorMessage = `Route not found with id: ${routeId}`;

    server.use(
      http.post(apiUrl, () =>
        HttpResponse.json({
          timestamp: "2026-07-28T10:00:00Z",
          status: 404,
          error: "Not Found",
          message: errorMessage,
          path: `/api/routes/${routeId}/waypoints`,
          fieldErrors: {}
        }, {status: 404})
      )
    );

    const user = userEvent.setup();

    renderCreateWaypointPage();

    await fillValidForm(user);

    await user.click(screen.getByRole("button", {name: "Crea waypoint"}));

    expect(await screen.findByText(errorMessage)).toBeInTheDocument();
    expect(screen.getByRole("button", {name: "Crea waypoint"})).toBeEnabled();
    expect(screen.queryByText("Pagina dettaglio itinerario")).not.toBeInTheDocument();
  });

  test("should show a generic message when the API returns an invalid response", async () => {
    server.use(
      http.post(apiUrl, () =>
        new HttpResponse("Internal server error", {
          status: 500,
          headers: {
            "Content-Type": "text/plain"
          }
        })
      )
    );

    const user = userEvent.setup();

    renderCreateWaypointPage();

    await fillValidForm(user);

    await user.click(screen.getByRole("button", {name: "Crea waypoint"}));

    expect(await screen.findByText("Impossibile creare il waypoint")).toBeInTheDocument();
    expect(screen.getByRole("button", {name: "Crea waypoint"})).toBeEnabled();
  });
});
