import {
  http,
  HttpResponse,
} from "msw";
import {
  describe,
  expect,
  test,
} from "vitest";

import {server} from "../../../test/server";
import {listRoutes} from "./listRoutesApi";
import {ApiRequestError} from "../api/ApiRequestError.js";

const apiUrl = new URL(
  "/api/routes",
  window.location.origin,
).toString();

const routePage = {
  content: [
    {
      id: "11111111-1111-1111-1111-111111111111",
      name: "Passo dello Stelvio",
      startLocation: "Bormio",
      endLocation: "Prato allo Stelvio",
      distanceKm: 47.5,
      difficulty: "HARD",
      createdAt: "2026-07-29T10:00:00Z",
    },
  ],
  page: 0,
  size: 20,
  totalElements: 1,
  totalPages: 1,
  first: true,
  last: true,
};

async function getRejectedError(promise) {
  try {
    await promise;
  } catch (error) {
    return error;
  }

  throw new Error("Expected the promise to reject");
}

describe("listRoutes", () => {
  test("should send pagination parameters and return the route page", async () => {
    server.use(
      http.get(apiUrl, ({request}) => {
        const requestUrl = new URL(request.url);

        expect(
          requestUrl.searchParams.get("page"),
        ).toBe("2");

        expect(
          requestUrl.searchParams.get("size"),
        ).toBe("5");

        expect(
          requestUrl.searchParams.get("sort"),
        ).toBe("name,asc");

        expect(
          request.headers.get("Accept"),
        ).toBe("application/json");

        return HttpResponse.json({
          ...routePage,
          page: 2,
          size: 5,
        });
      }),
    );

    const result = await listRoutes({
      page: 2,
      size: 5,
      sort: "name,asc",
    });

    expect(result.page).toBe(2);
    expect(result.size).toBe(5);
    expect(result.content).toHaveLength(1);
  });

  test("should use default pagination parameters", async () => {
    server.use(
      http.get(apiUrl, ({request}) => {
        const requestUrl = new URL(request.url);

        expect(
          requestUrl.searchParams.get("page"),
        ).toBe("0");

        expect(
          requestUrl.searchParams.get("size"),
        ).toBe("20");

        expect(
          requestUrl.searchParams.get("sort"),
        ).toBe("createdAt,desc");

        return HttpResponse.json(routePage);
      }),
    );

    const result = await listRoutes();

    expect(result).toEqual(routePage);
  });

  test("should throw a structured API error", async () => {
    server.use(
      http.get(apiUrl, () =>
        HttpResponse.json(
          {
            timestamp: "2026-07-29T10:00:00Z",
            status: 400,
            error: "Bad Request",
            message: "size must be between 1 and 100",
            path: "/api/routes",
            fieldErrors: {},
          },
          {
            status: 400,
          },
        ),
      ),
    );

    const error = await getRejectedError(
      listRoutes(),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      name: "ApiRequestError",
      status: 400,
      message: "size must be between 1 and 100",
      fieldErrors: {},
    });
  });

  test("should use a generic message when the response is not JSON", async () => {
    server.use(
      http.get(
        apiUrl,
        () =>
          new HttpResponse(
            "Internal server error",
            {
              status: 500,
              headers: {
                "Content-Type": "text/plain",
              },
            },
          ),
      ),
    );

    const error = await getRejectedError(
      listRoutes(),
    );

    expect(error).toBeInstanceOf(ApiRequestError);

    expect(error).toMatchObject({
      status: 500,
      message:
        "Impossibile caricare gli itinerari",
      fieldErrors: {},
    });
  });
});
