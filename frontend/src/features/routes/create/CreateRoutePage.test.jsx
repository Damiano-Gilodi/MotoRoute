import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
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

import { server } from "../../../test/server";
import { CreateRoutePage } from "./CreateRoutePage";

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
}

describe("CreateRoutePage", () => {
  test("should show loading while creating the route and then show success", async () => {
    server.use(
      http.post(apiUrl, async () => {
        await delay(100);

        return HttpResponse.json(createdRoute, {
          status: 201,
          headers: {
            Location: `/api/routes/${createdRoute.id}`,
          },
        });
      }),
    );

    const user = userEvent.setup();

    render(<CreateRoutePage />);

    await fillValidForm(user);

    await user.click(
      screen.getByRole("button", {
        name: "Crea itinerario",
      }),
    );

    expect(
      screen.getByRole("button", {
        name: "Creazione in corso...",
      }),
    ).toBeDisabled();

    expect(
      await screen.findByRole("status"),
    ).toHaveTextContent(
      "Itinerario creato con successo: Passo dello Stelvio.",
    );

    expect(
      screen.getByRole("button", {
        name: "Crea itinerario",
      }),
    ).toBeEnabled();
  });

  test("should show field errors returned by the API", async () => {
    server.use(
      http.post(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-28T10:00:00Z",
            status: 400,
            error: "Bad Request",
            message: "One or more fields are invalid",
            path: "/api/routes",
            fieldErrors: {
              name: "must not be blank",
            },
          },
          {
            status: 400,
          },
        ),
      ),
    );

    const user = userEvent.setup();

    render(<CreateRoutePage />);

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
      screen.getByText("must not be blank"),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", {
        name: "Crea itinerario",
      }),
    ).toBeEnabled();
  });

  test("should show a generic message when the API returns an invalid response", async () => {
    server.use(
      http.post(
        apiUrl,
        () =>
          new HttpResponse("Internal server error", {
            status: 500,
            headers: {
              "Content-Type": "text/plain",
            },
          }),
      ),
    );

    const user = userEvent.setup();

    render(<CreateRoutePage />);

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
  });
});
