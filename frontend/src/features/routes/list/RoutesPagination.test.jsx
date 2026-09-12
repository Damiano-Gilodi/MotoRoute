import {
  render,
  screen,
} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {
  describe,
  expect,
  test,
  vi,
} from "vitest";

import {RoutesPagination} from "./RoutesPagination";

describe("RoutesPagination", () => {
  test("should not render when there is only one page", () => {
    const {container} = render(
      <RoutesPagination
        page={0}
        totalPages={1}
        onPageChange={vi.fn()}
      />,
    );

    expect(container).toBeEmptyDOMElement();
  });

  test("should disable the previous button on the first page", () => {
    render(
      <RoutesPagination
        page={0}
        totalPages={3}
        onPageChange={vi.fn()}
      />,
    );

    expect(
      screen.getByRole("button", {
        name: "Pagina precedente",
      }),
    ).toBeDisabled();

    expect(
      screen.getByRole("button", {
        name: "Pagina successiva",
      }),
    ).toBeEnabled();

    expect(
      screen.getByText("Pagina 1 di 3"),
    ).toBeInTheDocument();
  });

  test("should disable the next button on the last page", () => {
    render(
      <RoutesPagination
        page={2}
        totalPages={3}
        onPageChange={vi.fn()}
      />,
    );

    expect(
      screen.getByRole("button", {
        name: "Pagina precedente",
      }),
    ).toBeEnabled();

    expect(
      screen.getByRole("button", {
        name: "Pagina successiva",
      }),
    ).toBeDisabled();
  });

  test("should request the previous and next pages", async () => {
    const user = userEvent.setup();
    const onPageChange = vi.fn();

    render(
      <RoutesPagination
        page={1}
        totalPages={3}
        onPageChange={onPageChange}
      />,
    );

    await user.click(
      screen.getByRole("button", {
        name: "Pagina precedente",
      }),
    );

    expect(onPageChange).toHaveBeenCalledWith(0);

    await user.click(
      screen.getByRole("button", {
        name: "Pagina successiva",
      }),
    );

    expect(onPageChange).toHaveBeenCalledWith(2);
  });

  test("should disable navigation while loading", () => {
    render(
      <RoutesPagination
        page={1}
        totalPages={3}
        isLoading
        onPageChange={vi.fn()}
      />,
    );

    expect(
      screen.getByRole("button", {
        name: "Pagina precedente",
      }),
    ).toBeDisabled();

    expect(
      screen.getByRole("button", {
        name: "Pagina successiva",
      }),
    ).toBeDisabled();
  });
});
