package it.motoroute.route.api;

final class RouteApiExamples {

    static final String CREATE_REQUEST = """
        {
          "name": "Passo dello Stelvio",
          "description": "Percorso panoramico",
          "startLocation": "Bormio",
          "endLocation": "Prato allo Stelvio",
          "distanceKm": 47.50,
          "difficulty": "HARD"
        }
        """;

    static final String UPDATE_REQUEST = """
        {
          "name": "Passo dello Stelvio",
          "description": "Percorso panoramico",
          "startLocation": "Bormio",
          "endLocation": "Prato allo Stelvio",
          "distanceKm": 40.00,
          "difficulty": "MEDIUM"
        }
        """;

    static final String ROUTE_RESPONSE = """
        {
          "id": "11111111-1111-1111-1111-111111111111",
          "name": "Passo dello Stelvio",
          "description": "Percorso panoramico",
          "startLocation": "Bormio",
          "endLocation": "Prato allo Stelvio",
          "distanceKm": 47.50,
          "difficulty": "HARD",
          "createdAt": "2026-07-27T10:00:00Z",
          "updatedAt": "2026-07-27T10:00:00Z"
        }
        """;

    static final String UPDATED_ROUTE_RESPONSE = """
        {
          "id": "11111111-1111-1111-1111-111111111111",
          "name": "Passo dello Stelvio",
          "description": "Percorso panoramico",
          "startLocation": "Bormio",
          "endLocation": "Prato allo Stelvio",
          "distanceKm": 40.00,
          "difficulty": "MEDIUM",
          "createdAt": "2026-07-27T10:00:00Z",
          "updatedAt": "2026-09-08T15:30:00Z"
        }
        """;

    static final String ROUTE_PAGE_RESPONSE = """
        {
          "content": [
            {
              "id": "11111111-1111-1111-1111-111111111111",
              "name": "Passo dello Stelvio",
              "startLocation": "Bormio",
              "endLocation": "Prato allo Stelvio",
              "distanceKm": 47.50,
              "difficulty": "HARD",
              "createdAt": "2026-07-27T10:00:00Z"
            }
          ],
          "page": 0,
          "size": 20,
          "totalElements": 1,
          "totalPages": 1,
          "first": true,
          "last": true
        }
        """;

    static final String VALIDATION_ERROR = """
        {
          "timestamp": "2026-07-27T10:00:00Z",
          "status": 400,
          "error": "Bad Request",
          "message": "One or more fields are invalid",
          "path": "/api/routes",
          "fieldErrors": {
            "name": "must not be blank"
          }
        }
        """;

    static final String INVALID_PAGE_SIZE = """
        {
          "timestamp": "2026-07-29T10:00:00Z",
          "status": 400,
          "error": "Bad Request",
          "message": "size must be between 1 and 100",
          "path": "/api/routes",
          "fieldErrors": {}
        }
        """;

    static final String INVALID_ROUTE_ID = """
        {
          "timestamp": "2026-07-29T10:00:00Z",
          "status": 400,
          "error": "Bad Request",
          "message": "Parameter 'routeId' must be of type UUID",
          "path": "/api/routes/abc",
          "fieldErrors": {}
        }
        """;

    static final String ROUTE_NOT_FOUND = """
        {
          "timestamp": "2026-07-29T10:00:00Z",
          "status": 404,
          "error": "Not Found",
          "message": "Route not found with id: 11111111-1111-1111-1111-111111111111",
          "path": "/api/routes/11111111-1111-1111-1111-111111111111",
          "fieldErrors": {}
        }
        """;

    private RouteApiExamples() {
    }
}
