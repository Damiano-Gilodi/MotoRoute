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

import {server} from "./test/server";
import App from "./App";

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
  test("should render the route list at /routes", async () => {
    render(
      <MemoryRouter initialEntries={["/routes"]}>
        <App/>
      </MemoryRouter>,
    );

    expect(
      await screen.findByRole("heading", {
        name: "Itinerari",
      }),
    ).toBeInTheDocument();
  });

  test("should render the create route page at /routes/new", async () => {
    render(
      <MemoryRouter initialEntries={["/routes/new"]}>
        <App/>
      </MemoryRouter>,
    );

    expect(
      await screen.findByRole("heading", {
        name: "Crea itinerario",
      }),
    ).toBeInTheDocument();
  });

  test("should redirect the root path to the route list", async () => {
    render(
      <MemoryRouter initialEntries={["/"]}>
        <App/>
      </MemoryRouter>,
    );

    expect(
      await screen.findByRole("heading", {
        name: "Itinerari",
      }),
    ).toBeInTheDocument();
  });
});
