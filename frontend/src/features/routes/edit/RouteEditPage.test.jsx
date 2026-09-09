import {
  render,
  screen,
} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
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
  describe,
  expect,
  test,
} from "vitest";

import {server} from "../../../test/server.js";
import {RouteEditPage} from "./RouteEditPage.jsx";

const routeId =
  "11111111-1111-1111-1111-111111111111";

const apiUrl = new URL(
  `/api/routes/${routeId}`,
  window.location.origin,
).toString();

const existingRoute = {
  id: routeId,
  name: "Passo dello Stelvio",
  description: "Percorso panoramico",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: 47.5,
  difficulty: "HARD",
  createdAt: "2026-07-29T10:00:00Z",
  updatedAt: "2026-07-29T10:00:00Z",
};

function renderRouteEditPage() {
  render(
    <MemoryRouter
      initialEntries={[
        `/routes/${routeId}/edit`,
      ]}
    >
      <Routes>
        <Route
          path="/routes/:routeId/edit"
          element={<RouteEditPage/>}
        />

        <Route
          path="/routes/:routeId"
          element={
            <p>
              Pagina dettaglio itinerario
            </p>
          }
        />
      </Routes>
    </MemoryRouter>,
  );
}

function useExistingRouteHandler() {
  server.use(
    http.get(
      apiUrl,
      () =>
        HttpResponse.json(
          existingRoute,
          {
            status: 200,
          },
        ),
    ),
  );
}

describe("RouteEditPage", () => {
  test(
    "should load the route and prefill the form",
    async () => {
      useExistingRouteHandler();

      renderRouteEditPage();

      expect(
        screen.getByText(
          "Caricamento itinerario...",
        ),
      ).toBeInTheDocument();

      expect(
        await screen.findByRole(
          "heading",
          {
            name: "Modifica itinerario",
          },
        ),
      ).toBeInTheDocument();

      expect(
        screen.getByLabelText("Nome"),
      ).toHaveValue(
        "Passo dello Stelvio",
      );

      expect(
        screen.getByLabelText(
          "Descrizione",
        ),
      ).toHaveValue(
        "Percorso panoramico",
      );

      expect(
        screen.getByLabelText(
          "Partenza",
        ),
      ).toHaveValue("Bormio");

      expect(
        screen.getByLabelText("Arrivo"),
      ).toHaveValue(
        "Prato allo Stelvio",
      );

      expect(
        screen.getByLabelText(
          "Distanza (km)",
        ),
      ).toHaveValue(47.5);

      expect(
        screen.getByLabelText(
          "Difficoltà",
        ),
      ).toHaveValue("HARD");

      expect(
        screen.getByRole("button", {
          name: "Salva modifiche",
        }),
      ).toBeEnabled();
    },
  );

  test(
    "should update the route and navigate to route details",
    async () => {
      useExistingRouteHandler();

      let receivedPayload;

      const updatedRoute = {
        ...existingRoute,
        name: "Stelvio aggiornato",
        description:
          "Percorso aggiornato",
        distanceKm: 40.5,
        difficulty: "MEDIUM",
        updatedAt:
          "2026-07-29T10:30:00Z",
      };

      server.use(
        http.put(
          apiUrl,
          async ({request}) => {
            receivedPayload =
              await request.json();

            return HttpResponse.json(
              updatedRoute,
              {
                status: 200,
              },
            );
          },
        ),
      );

      const user = userEvent.setup();

      renderRouteEditPage();

      const nameInput =
        await screen.findByLabelText(
          "Nome",
        );

      await user.clear(nameInput);

      await user.type(
        nameInput,
        "Stelvio aggiornato",
      );

      const descriptionInput =
        screen.getByLabelText(
          "Descrizione",
        );

      await user.clear(
        descriptionInput,
      );

      await user.type(
        descriptionInput,
        "Percorso aggiornato",
      );

      const distanceInput =
        screen.getByLabelText(
          "Distanza (km)",
        );

      await user.clear(
        distanceInput,
      );

      await user.type(
        distanceInput,
        "40.5",
      );

      await user.selectOptions(
        screen.getByLabelText(
          "Difficoltà",
        ),
        "MEDIUM",
      );

      await user.click(
        screen.getByRole("button", {
          name: "Salva modifiche",
        }),
      );

      expect(receivedPayload).toEqual({
        name: "Stelvio aggiornato",
        description:
          "Percorso aggiornato",
        startLocation: "Bormio",
        endLocation:
          "Prato allo Stelvio",
        distanceKm: 40.5,
        difficulty: "MEDIUM",
      });

      expect(
        await screen.findByText(
          "Pagina dettaglio itinerario",
        ),
      ).toBeInTheDocument();
    },
  );

  test(
    "should show submitting state while the route is being updated",
    async () => {
      useExistingRouteHandler();

      server.use(
        http.put(
          apiUrl,
          async () => {
            await delay(100);

            return HttpResponse.json(
              existingRoute,
              {
                status: 200,
              },
            );
          },
        ),
      );

      const user = userEvent.setup();

      renderRouteEditPage();

      await screen.findByLabelText(
        "Nome",
      );

      await user.click(
        screen.getByRole("button", {
          name: "Salva modifiche",
        }),
      );

      expect(
        screen.getByRole("button", {
          name: "Salvataggio...",
        }),
      ).toBeDisabled();

      expect(
        await screen.findByText(
          "Pagina dettaglio itinerario",
        ),
      ).toBeInTheDocument();
    },
  );

  test(
    "should show an error when the route does not exist",
    async () => {
      server.use(
        http.get(
          apiUrl,
          () =>
            HttpResponse.json(
              {
                timestamp:
                  "2026-07-29T10:00:00Z",
                status: 404,
                error: "Not Found",
                message:
                  "Route not found with id: "
                  + routeId,
                path:
                  `/api/routes/${routeId}`,
                fieldErrors: {},
              },
              {
                status: 404,
              },
            ),
        ),
      );

      renderRouteEditPage();

      expect(
        await screen.findByRole(
          "heading",
          {
            name: "Modifica itinerario",
          },
        ),
      ).toBeInTheDocument();

      expect(
        screen.getByText(
          "Route not found with id: "
          + routeId,
        ),
      ).toBeInTheDocument();

      expect(
        screen.queryByRole(
          "button",
          {
            name: "Salva modifiche",
          },
        ),
      ).not.toBeInTheDocument();
    },
  );

  test(
    "should show a generic error when loading the route fails",
    async () => {
      server.use(
        http.get(
          apiUrl,
          () =>
            new HttpResponse(
              "Internal server error",
              {
                status: 500,
                headers: {
                  "Content-Type":
                    "text/plain",
                },
              },
            ),
        ),
      );

      renderRouteEditPage();

      expect(
        await screen.findByText(
          "Impossibile caricare l’itinerario",
        ),
      ).toBeInTheDocument();

      expect(
        screen.queryByRole(
          "button",
          {
            name: "Salva modifiche",
          },
        ),
      ).not.toBeInTheDocument();
    },
  );

  test(
    "should show field errors returned when the update request is invalid",
    async () => {
      useExistingRouteHandler();

      server.use(
        http.put(
          apiUrl,
          () =>
            HttpResponse.json(
              {
                timestamp:
                  "2026-07-29T10:30:00Z",
                status: 400,
                error: "Bad Request",
                message:
                  "One or more fields are invalid",
                path:
                  `/api/routes/${routeId}`,
                fieldErrors: {
                  name:
                    "must not be blank",
                  distanceKm:
                    "must be greater than 0",
                },
              },
              {
                status: 400,
              },
            ),
        ),
      );

      const user = userEvent.setup();

      renderRouteEditPage();

      await screen.findByLabelText(
        "Nome",
      );

      await user.click(
        screen.getByRole("button", {
          name: "Salva modifiche",
        }),
      );

      expect(
        await screen.findByText(
          "One or more fields are invalid",
        ),
      ).toBeInTheDocument();

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

      expect(
        screen.getByRole("button", {
          name: "Salva modifiche",
        }),
      ).toBeEnabled();
    },
  );

  test(
    "should show a generic error when the update response is invalid",
    async () => {
      useExistingRouteHandler();

      server.use(
        http.put(
          apiUrl,
          () =>
            new HttpResponse(
              "Internal server error",
              {
                status: 500,
                headers: {
                  "Content-Type":
                    "text/plain",
                },
              },
            ),
        ),
      );

      const user = userEvent.setup();

      renderRouteEditPage();

      await screen.findByLabelText(
        "Nome",
      );

      await user.click(
        screen.getByRole("button", {
          name: "Salva modifiche",
        }),
      );

      expect(
        await screen.findByText(
          "Impossibile aggiornare l’itinerario",
        ),
      ).toBeInTheDocument();

      expect(
        screen.getByRole("button", {
          name: "Salva modifiche",
        }),
      ).toBeEnabled();
    },
  );
});
