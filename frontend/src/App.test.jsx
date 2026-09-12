import {
  render,
  screen,
} from "@testing-library/react";
import {MemoryRouter} from "react-router";
import {
  beforeEach,
  describe,
  expect,
  test,
} from "vitest";
import {
  http,
  HttpResponse,
} from "msw";

import {server} from "./test/server.js";
import App from "./App.jsx";

const apiUrl = new URL(
  "/api/routes",
  window.location.origin,
).toString();

beforeEach(() => {
  server.use(
    http.get(apiUrl, () =>
      HttpResponse.json({
        content: [],
        page: 0,
        size: 20,
        totalElements: 0,
        totalPages: 0,
        first: true,
        last: true,
      }),
    ),
  );
});

describe("App routing", () => {
  test(
    "should render the route list at /routes",
    async () => {
      render(
        <MemoryRouter
          initialEntries={["/routes"]}
        >
          <App/>
        </MemoryRouter>,
      );

      expect(
        await screen.findByRole(
          "heading",
          {
            name: "Itinerari",
          },
        ),
      ).toBeInTheDocument();
    },
  );

  test(
    "should render the create route page at /routes/new",
    () => {
      render(
        <MemoryRouter
          initialEntries={["/routes/new"]}
        >
          <App/>
        </MemoryRouter>,
      );

      expect(
        screen.getByRole("heading", {
          name: "Crea itinerario",
        }),
      ).toBeInTheDocument();
    },
  );

  test(
    "should redirect the root path to the route list",
    async () => {
      render(
        <MemoryRouter
          initialEntries={["/"]}
        >
          <App/>
        </MemoryRouter>,
      );

      expect(
        await screen.findByRole(
          "heading",
          {
            name: "Itinerari",
          },
        ),
      ).toBeInTheDocument();
    },
  );

  test(
    "should render the edit route page at /routes/:routeId/edit",
    async () => {
      const routeId =
        "11111111-1111-1111-1111-111111111111";

      const apiUrl = new URL(
        `/api/routes/${routeId}`,
        window.location.origin,
      ).toString();

      server.use(
        http.get(apiUrl, () =>
          HttpResponse.json({
            id: routeId,
            name: "Passo dello Stelvio",
            description: "Percorso panoramico",
            startLocation: "Bormio",
            endLocation: "Prato allo Stelvio",
            distanceKm: 47.5,
            difficulty: "HARD",
            createdAt: "2026-07-29T10:00:00Z",
            updatedAt: "2026-07-29T10:00:00Z",
          }),
        ),
      );

      render(
        <MemoryRouter
          initialEntries={[
            `/routes/${routeId}/edit`,
          ]}
        >
          <App/>
        </MemoryRouter>,
      );

      expect(
        await screen.findByRole("heading", {
          name: "Modifica itinerario",
        }),
      ).toBeInTheDocument();
    },
  );
});
