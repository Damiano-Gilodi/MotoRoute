import {render, screen} from "@testing-library/react";
import {MemoryRouter} from "react-router";
import {
  describe,
  expect,
  test,
} from "vitest";

import App from "./App";

describe("App routing", () => {
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

  test("should redirect the root path to the create route page", async () => {
    render(
      <MemoryRouter initialEntries={["/"]}>
        <App/>
      </MemoryRouter>,
    );

    expect(
      await screen.findByRole("heading", {
        name: "Crea itinerario",
      }),
    ).toBeInTheDocument();
  });
});
