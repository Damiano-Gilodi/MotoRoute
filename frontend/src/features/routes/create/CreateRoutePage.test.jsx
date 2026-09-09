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
import {CreateRoutePage} from "./CreateRoutePage.jsx";

const apiUrl = new URL(
  "/api/routes",
  window.location.origin,
).toString();

const createdRoute = {
  id: "11111111-1111-1111-1111-111111111111",
  name: "Passo dello Stelvio",
  description: "Percorso panoramico",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: 47.5,
  difficulty: "HARD",
  createdAt: "2026-07-28T10:00:00Z",
  updatedAt: "2026-07-28T10:00:00Z",
};

function renderCreateRoutePage() {
  render(
    <MemoryRouter
      initialEntries={["/routes/new"]}
    >
      <Routes>
        <Route
          path="/routes/new"
          element={<CreateRoutePage/>}
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

async function fillValidForm(user) {
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
}

describe("CreateRoutePage", () => {
  test(
    "should show loading and navigate to route details after creation",
    async () => {
      server.use(
        http.post(apiUrl, async () => {
          await delay(100);

          return HttpResponse.json(
            createdRoute,
            {
              status: 201,
              headers: {
                Location:
                  `/api/routes/${createdRoute.id}`,
              },
            },
          );
        }),
      );

      const user = userEvent.setup();

      renderCreateRoutePage();

      await fillValidForm(user);

      await user.click(
        screen.getByRole("button", {
          name: "Crea itinerario",
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

      expect(
        screen.queryByRole("button", {
          name: "Crea itinerario",
        }),
      ).not.toBeInTheDocument();
    },
  );

  test(
    "should show field errors returned by the API",
    async () => {
      server.use(
        http.post(
          apiUrl,
          () =>
            HttpResponse.json(
              {
                timestamp:
                  "2026-07-28T10:00:00Z",
                status: 400,
                error: "Bad Request",
                message:
                  "One or more fields are invalid",
                path: "/api/routes",
                fieldErrors: {
                  name:
                    "must not be blank",
                },
              },
              {
                status: 400,
              },
            ),
        ),
      );

      const user = userEvent.setup();

      renderCreateRoutePage();

      await fillValidForm(user);

      await user.click(
        screen.getByRole("button", {
          name: "Crea itinerario",
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
        screen.getByRole("button", {
          name: "Crea itinerario",
        }),
      ).toBeEnabled();
    },
  );

  test(
    "should show a generic message when the API returns an invalid response",
    async () => {
      server.use(
        http.post(
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

      renderCreateRoutePage();

      await fillValidForm(user);

      await user.click(
        screen.getByRole("button", {
          name: "Crea itinerario",
        }),
      );

      expect(
        await screen.findByText(
          "Impossibile creare l’itinerario",
        ),
      ).toBeInTheDocument();

      expect(
        screen.getByRole("button", {
          name: "Crea itinerario",
        }),
      ).toBeEnabled();
    },
  );
});
