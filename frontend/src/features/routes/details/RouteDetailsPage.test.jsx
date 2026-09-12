import {
  render,
  screen,
} from "@testing-library/react";
import {
  MemoryRouter,
  Route,
  Routes,
} from "react-router";
import {
  delay,
  http,
  HttpResponse,
} from "msw";
import {
  afterEach,
  describe,
  expect,
  test,
  vi,
} from "vitest";
import userEvent from "@testing-library/user-event";

import {server} from "../../../test/server.js";
import {RouteDetailsPage} from "./RouteDetailsPage.jsx";

const routeId = "11111111-1111-1111-1111-111111111111";

const apiUrl = new URL(
  `/api/routes/${routeId}`,
  window.location.origin,
).toString();

const route = {
  id: routeId,
  name: "Passo dello Stelvio",
  description: "Percorso panoramico",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: 47.5,
  difficulty: "HARD",
  createdAt: "2026-07-29T10:00:00Z",
  updatedAt: "2026-07-29T12:30:00Z",
};

function renderRouteDetailsPage() {
  render(
    <MemoryRouter
      initialEntries={[
        `/routes/${routeId}`,
      ]}
    >
      <Routes>
        <Route
          path="/routes/:routeId"
          element={<RouteDetailsPage/>}
        />

        <Route
          path="/routes"
          element={
            <p>
              Pagina elenco itinerari
            </p>
          }
        />
      </Routes>
    </MemoryRouter>,
  );
}

afterEach(() => {
  vi.restoreAllMocks();
});

describe("RouteDetailsPage", () => {
  test("should show loading and then route details", async () => {
    server.use(
      http.get(apiUrl, async () => {
        await delay(100);

        return HttpResponse.json(route);
      }),
    );

    renderRouteDetailsPage();

    expect(
      screen.getByRole("status"),
    ).toHaveTextContent(
      "Caricamento itinerario...",
    );

    expect(
      screen.queryByRole("link", {
        name: "Modifica itinerario",
      }),
    ).not.toBeInTheDocument();

    expect(
      await screen.findByRole("heading", {
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
      screen.getByText("Difficile"),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("link", {
        name: "Modifica itinerario",
      }),
    ).toHaveAttribute(
      "href",
      `/routes/${routeId}/edit`,
    );

    expect(
      screen.queryByRole("status"),
    ).not.toBeInTheDocument();
  });

  test("should use route ID from the URL", async () => {
    server.use(
      http.get(apiUrl, ({request}) => {
        const requestUrl = new URL(request.url);

        expect(requestUrl.pathname)
          .toBe(`/api/routes/${routeId}`);

        return HttpResponse.json(route);
      }),
    );

    renderRouteDetailsPage();

    expect(
      await screen.findByRole("heading", {
        name: "Passo dello Stelvio",
      }),
    ).toBeInTheDocument();
  });

  test("should show not found when route does not exist", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 404,
            error: "Not Found",
            message:
              "Route not found with id: " + routeId,
            path: `/api/routes/${routeId}`,
            fieldErrors: {},
          },
          {
            status: 404,
          },
        ),
      ),
    );

    renderRouteDetailsPage();

    expect(
      await screen.findByRole("heading", {
        level: 1,
        name: "Itinerario non trovato",
      }),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        "L’itinerario richiesto non esiste.",
      ),
    ).toBeInTheDocument();

    expect(
      screen.queryByRole("alert"),
    ).not.toBeInTheDocument();

    expect(
      screen.queryByText("Passo dello Stelvio"),
    ).not.toBeInTheDocument();

    expect(
      screen.queryByRole("link", {
        name: "Modifica itinerario",
      }),
    ).not.toBeInTheDocument();
  });

  test("should show API error when request fails", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 500,
            error: "Internal Server Error",
            message: "An unexpected error occurred",
            path: `/api/routes/${routeId}`,
            fieldErrors: {},
          },
          {
            status: 500,
          },
        ),
      ),
    );

    renderRouteDetailsPage();

    expect(
      await screen.findByRole("alert"),
    ).toHaveTextContent(
      "An unexpected error occurred",
    );

    expect(
      screen.queryByRole("status"),
    ).not.toBeInTheDocument();

    expect(
      screen.queryByText("Passo dello Stelvio"),
    ).not.toBeInTheDocument();
  });

  test("should not delete the route when the user cancels confirmation", async () => {
      server.use(
        http.get(apiUrl, () =>
          HttpResponse.json(route),
        ),
      );

      let deleteRequests = 0;

      server.use(
        http.delete(apiUrl, () => {
            deleteRequests += 1;

            return new HttpResponse(
              null,
              {
                status: 204,
              },
            );
          },
        ),
      );

      const confirmSpy =
        vi.spyOn(
          window,
          "confirm",
        ).mockReturnValue(false);

      const user = userEvent.setup();

      renderRouteDetailsPage();

      await screen.findByRole(
        "heading",
        {
          name: "Passo dello Stelvio",
        },
      );

      await user.click(
        screen.getByRole("button", {
          name: "Elimina itinerario",
        }),
      );

      expect(confirmSpy)
        .toHaveBeenCalledWith(
          "Vuoi davvero eliminare questo itinerario?",
        );

      expect(deleteRequests)
        .toBe(0);

      expect(
        screen.getByRole(
          "heading",
          {
            name: "Passo dello Stelvio",
          },
        ),
      ).toBeInTheDocument();
    },
  );

  test("should delete the route and navigate to the route list", async () => {
      server.use(
        http.get(apiUrl, () =>
          HttpResponse.json(route),
        ),
      );

      server.use(
        http.delete(apiUrl, () =>
          new HttpResponse(
            null,
            {
              status: 204,
            },
          ),
        ),
      );

      vi.spyOn(
        window,
        "confirm",
      ).mockReturnValue(true);

      const user = userEvent.setup();

      renderRouteDetailsPage();

      await screen.findByRole(
        "heading",
        {
          name: "Passo dello Stelvio",
        },
      );

      await user.click(
        screen.getByRole("button", {
          name: "Elimina itinerario",
        }),
      );

      expect(
        await screen.findByText(
          "Pagina elenco itinerari",
        ),
      ).toBeInTheDocument();

      expect(
        screen.queryByText(
          "Passo dello Stelvio",
        ),
      ).not.toBeInTheDocument();
    },
  );

  test("should show deleting state while the route is being deleted", async () => {
      server.use(
        http.get(apiUrl, () =>
          HttpResponse.json(route),
        ),
      );

      server.use(
        http.delete(apiUrl, async () => {
            await delay(100);

            return new HttpResponse(
              null,
              {
                status: 204,
              },
            );
          },
        ),
      );

      vi.spyOn(
        window,
        "confirm",
      ).mockReturnValue(true);

      const user = userEvent.setup();

      renderRouteDetailsPage();

      await screen.findByRole(
        "heading",
        {
          name: "Passo dello Stelvio",
        },
      );

      await user.click(
        screen.getByRole("button", {
          name: "Elimina itinerario",
        }),
      );

      expect(
        screen.getByRole("button", {
          name:
            "Eliminazione in corso...",
        }),
      ).toBeDisabled();

      expect(
        await screen.findByText(
          "Pagina elenco itinerari",
        ),
      ).toBeInTheDocument();
    },
  );

  test("should show an error when route deletion fails", async () => {
      server.use(
        http.get(apiUrl, () =>
          HttpResponse.json(route),
        ),
      );

      server.use(
        http.delete(apiUrl, () =>
          HttpResponse.json(
            {
              timestamp:
                "2026-09-10T10:00:00Z",
              status: 500,
              error:
                "Internal Server Error",
              message:
                "An unexpected error occurred",
              path:
                `/api/routes/${routeId}`,
              fieldErrors: {},
            },
            {
              status: 500,
            },
          ),
        ),
      );

      vi.spyOn(
        window,
        "confirm",
      ).mockReturnValue(true);

      const user = userEvent.setup();

      renderRouteDetailsPage();

      await screen.findByRole(
        "heading",
        {
          name: "Passo dello Stelvio",
        },
      );

      await user.click(
        screen.getByRole("button", {
          name: "Elimina itinerario",
        }),
      );

      expect(
        await screen.findByRole(
          "alert",
        ),
      ).toHaveTextContent(
        "An unexpected error occurred",
      );

      expect(
        screen.getByRole(
          "heading",
          {
            name: "Passo dello Stelvio",
          },
        ),
      ).toBeInTheDocument();

      expect(
        screen.getByRole("button", {
          name: "Elimina itinerario",
        }),
      ).toBeEnabled();

      expect(
        screen.queryByText(
          "Pagina elenco itinerari",
        ),
      ).not.toBeInTheDocument();
    },
  );
});
