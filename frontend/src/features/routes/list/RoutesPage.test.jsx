import {
  render,
  screen,
} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {MemoryRouter} from "react-router";
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

import {server} from "../../../test/server";
import {RoutesPage} from "./RoutesPage";

const apiUrl = new URL(
  "/api/routes",
  window.location.origin,
).toString();

const firstRoute = {
  id: "11111111-1111-1111-1111-111111111111",
  name: "Passo dello Stelvio",
  startLocation: "Bormio",
  endLocation: "Prato allo Stelvio",
  distanceKm: 47.5,
  difficulty: "HARD",
  createdAt: "2026-07-29T10:00:00Z",
};

function createPage({
                      content = [],
                      page = 0,
                      size = 20,
                      totalElements = content.length,
                      totalPages = content.length > 0 ? 1 : 0,
                      first = page === 0,
                      last = true,
                    } = {}) {
  return {
    content,
    page,
    size,
    totalElements,
    totalPages,
    first,
    last,
  };
}

function renderRoutesPage() {
  return render(
    <MemoryRouter>
      <RoutesPage/>
    </MemoryRouter>,
  );
}

describe("RoutesPage", () => {
  test("should show loading and then display routes", async () => {
    server.use(
      http.get(apiUrl, async () => {
        await delay(100);

        return HttpResponse.json(
          createPage({
            content: [firstRoute],
          }),
        );
      }),
    );

    renderRoutesPage();

    expect(
      screen.getByRole("status"),
    ).toHaveTextContent(
      "Caricamento itinerari...",
    );

    expect(
      await screen.findByRole("heading", {
        name: "Passo dello Stelvio",
      }),
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
      screen.queryByText(
        "Caricamento itinerari...",
      ),
    ).not.toBeInTheDocument();
  });

  test("should display the empty state", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(createPage()),
      ),
    );

    renderRoutesPage();

    expect(
      await screen.findByText(
        "Nessun itinerario disponibile.",
      ),
    ).toBeInTheDocument();
  });

  test("should display an API error", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 500,
            error: "Internal Server Error",
            message:
              "Errore durante il caricamento degli itinerari",
            path: "/api/routes",
            fieldErrors: {},
          },
          {
            status: 500,
          },
        ),
      ),
    );

    renderRoutesPage();

    expect(
      await screen.findByRole("alert"),
    ).toHaveTextContent(
      "Errore durante il caricamento degli itinerari",
    );

    expect(
      screen.queryByRole("article"),
    ).not.toBeInTheDocument();
  });

  test("should load the next page", async () => {
    const requestedPages = [];

    const secondRoute = {
      ...firstRoute,
      id: "22222222-2222-2222-2222-222222222222",
      name: "Lago di Garda",
      startLocation: "Riva del Garda",
      endLocation: "Sirmione",
    };

    server.use(
      http.get(apiUrl, ({request}) => {
        const requestUrl = new URL(request.url);
        const requestedPage = Number(
          requestUrl.searchParams.get("page"),
        );

        requestedPages.push(requestedPage);

        if (requestedPage === 0) {
          return HttpResponse.json(
            createPage({
              content: [firstRoute],
              page: 0,
              totalElements: 2,
              totalPages: 2,
              first: true,
              last: false,
            }),
          );
        }

        return HttpResponse.json(
          createPage({
            content: [secondRoute],
            page: 1,
            totalElements: 2,
            totalPages: 2,
            first: false,
            last: true,
          }),
        );
      }),
    );

    const user = userEvent.setup();

    renderRoutesPage();

    expect(
      await screen.findByRole("heading", {
        name: "Passo dello Stelvio",
      }),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Pagina 1 di 2"),
    ).toBeInTheDocument();

    await user.click(
      screen.getByRole("button", {
        name: "Pagina successiva",
      }),
    );

    expect(
      await screen.findByRole("heading", {
        name: "Lago di Garda",
      }),
    ).toBeInTheDocument();

    expect(
      screen.getByText("Pagina 2 di 2"),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", {
        name: "Pagina successiva",
      }),
    ).toBeDisabled();

    expect(requestedPages).toEqual([0, 1]);
  });
});
