import {render, screen} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {MemoryRouter, Route, Routes} from "react-router";
import {delay, http, HttpResponse} from "msw";
import {describe, expect, test} from "vitest";

import {server} from "../../../test/server.js";
import {WaypointsPage} from "./WaypointsPage.jsx";

const routeId = "11111111-1111-1111-1111-111111111111";

const apiUrl = new URL(
  `/api/routes/${routeId}/waypoints`,
  window.location.origin
).toString();

const firstWaypoint = {
  id: "22222222-2222-2222-2222-222222222222",
  name: "Punto panoramico",
  position: 1,
  latitude: 45.123456,
  longitude: 8.765432,
  createdAt: "2026-07-29T10:00:00Z"
};

function createPage({
                      content = [],
                      page = 0,
                      size = 20,
                      totalElements = content.length,
                      totalPages = content.length > 0 ? 1 : 0,
                      first = page === 0,
                      last = true
                    } = {}) {
  return {
    content,
    page,
    size,
    totalElements,
    totalPages,
    first,
    last
  };
}

function renderWaypointsPage() {
  return render(
    <MemoryRouter initialEntries={[`/routes/${routeId}/waypoints`]}>
      <Routes>
        <Route
          path="/routes/:routeId/waypoints"
          element={<WaypointsPage/>}
        />
      </Routes>
    </MemoryRouter>
  );
}

describe("WaypointsPage", () => {

  test("should show loading and then display waypoints", async () => {
    server.use(
      http.get(apiUrl, async () => {
        await delay(100);

        return HttpResponse.json(
          createPage({
            content: [firstWaypoint]
          })
        );
      })
    );

    renderWaypointsPage();

    expect(screen.getByRole("status")).toHaveTextContent("Caricamento waypoint...");
    expect(await screen.findByRole("heading", {name: "Punto panoramico"})).toBeInTheDocument();

    expect(screen.getByText("Posizione")).toBeInTheDocument();
    expect(screen.getByText("1")).toBeInTheDocument();
    expect(screen.getByText("45.123456")).toBeInTheDocument();
    expect(screen.getByText("8.765432")).toBeInTheDocument();

    expect(screen.queryByText("Caricamento waypoint...")).not.toBeInTheDocument();

    expect(screen.getByRole("link", {name: "Torna all'itinerario"})).toHaveAttribute("href", `/routes/${routeId}`);
    expect(screen.getByRole("link", {name: "Aggiungi waypoint"})).toHaveAttribute("href", `/routes/${routeId}/waypoints/new`);
  });

  test("should display the empty state", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(createPage())
      )
    );

    renderWaypointsPage();

    expect(await screen.findByText("Nessun waypoint disponibile.")).toBeInTheDocument();
    expect(screen.queryByRole("article")).not.toBeInTheDocument();
    expect(screen.queryByRole("navigation", {name: "Paginazione waypoints"})).not.toBeInTheDocument();
  });

  test("should display an API error", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 500,
            error: "Internal Server Error",
            message: "Errore durante il caricamento dei waypoint",
            path: `/api/routes/${routeId}/waypoints`,
            fieldErrors: {}
          },
          {status: 500}
        )
      )
    );

    renderWaypointsPage();

    expect(
      await screen.findByRole("alert")
    ).toHaveTextContent("Errore durante il caricamento dei waypoint");

    expect(screen.queryByRole("article")).not.toBeInTheDocument();
    expect(screen.queryByText("Nessun waypoint disponibile."))
      .not.toBeInTheDocument();
  });

  test("should display an error when the route does not exist", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 404,
            error: "Not Found",
            message: `Route not found with id: ${routeId}`,
            path: `/api/routes/${routeId}/waypoints`,
            fieldErrors: {}
          },
          {status: 404}
        )
      )
    );

    renderWaypointsPage();

    expect(
      await screen.findByRole("alert")
    ).toHaveTextContent(`Route not found with id: ${routeId}`);

    expect(screen.queryByRole("article")).not.toBeInTheDocument();
  });

  test("should load the next page", async () => {
    const requestedPages = [];

    const secondWaypoint = {
      ...firstWaypoint,
      id: "33333333-3333-3333-3333-333333333333",
      name: "Passo Pordoi",
      position: 21
    };

    const firstPageContent = [
      firstWaypoint,
      ...Array.from({length: 19}, (_, index) => ({
        ...firstWaypoint,
        id: `44444444-4444-4444-4444-${String(index + 2).padStart(12, "0")}`,
        name: `Waypoint ${index + 2}`,
        position: index + 2
      }))
    ];

    server.use(
      http.get(apiUrl, ({request}) => {
        const requestUrl = new URL(request.url);
        const requestedPage = Number(requestUrl.searchParams.get("page"));

        requestedPages.push(requestedPage);

        if (requestedPage === 0) {
          return HttpResponse.json(
            createPage({
              content: firstPageContent,
              page: 0,
              totalElements: 21,
              totalPages: 2,
              first: true,
              last: false
            })
          );
        }

        return HttpResponse.json(
          createPage({
            content: [secondWaypoint],
            page: 1,
            totalElements: 21,
            totalPages: 2,
            first: false,
            last: true
          })
        );
      })
    );

    const user = userEvent.setup();

    renderWaypointsPage();

    expect(await screen.findByRole("heading", {name: "Punto panoramico"})).toBeInTheDocument();
    expect(screen.getByText("Pagina 1 di 2")).toBeInTheDocument();

    await user.click(screen.getByRole("button", {name: "Pagina successiva"}));

    expect(await screen.findByRole("heading", {name: "Passo Pordoi"})).toBeInTheDocument();
    expect(screen.getByText("Pagina 2 di 2")).toBeInTheDocument();
    expect(screen.getByRole("button", {name: "Pagina successiva"})).toBeDisabled();
    expect(screen.queryByRole("heading", {name: "Punto panoramico"})).not.toBeInTheDocument();
    expect(requestedPages).toEqual([0, 1]);
  });

});
