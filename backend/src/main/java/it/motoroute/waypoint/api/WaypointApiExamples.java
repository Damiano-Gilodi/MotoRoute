package it.motoroute.waypoint.api;

final class WaypointApiExamples {

    static final String CREATE_REQUEST = """
        {
          "name": "Punto panoramico",
          "description": "Belvedere con vista sulle montagne",
          "position": 1,
          "latitude": 45.123456,
          "longitude": 8.765432
        }
        """;

    static final String UPDATE_REQUEST = """
        {
          "name": "Punto panoramico modificato",
          "description": "Belvedere con vista sulle montagne",
          "position": 1,
          "latitude": 45.123456,
          "longitude": 8.765432
        }
        """;

    static final String WAYPOINT_RESPONSE = """
        {
          "id": "22222222-2222-2222-2222-222222222222",
          "routeId": "11111111-1111-1111-1111-111111111111",
          "name": "Punto panoramico",
          "description": "Belvedere con vista sulle montagne",
          "position": 1,
          "latitude": 45.123456,
          "longitude": 8.765432,
          "createdAt": "2026-07-29T10:00:00Z",
          "updatedAt": "2026-07-29T10:00:00Z"
        }
        """;

    static final String WAYPOINT_PAGE_RESPONSE = """
        {
          "content": [
            {
              "id": "22222222-2222-2222-2222-222222222222",
              "name": "Punto panoramico",
              "description": "Belvedere con vista sulle montagne",
              "position": 1,
              "latitude": 45.123456,
              "longitude": 8.765432,
              "createdAt": "2026-07-29T10:00:00Z"
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
          "timestamp": "2026-07-29T10:00:00Z",
          "status": 400,
          "error": "Bad Request",
          "message": "One or more fields are invalid",
          "path": "/api/routes/11111111-1111-1111-1111-111111111111/waypoints",
          "fieldErrors": {
            "name": "must not be blank"
          }
        }
        """;

    static final String ROUTE_NOT_FOUND = """
        {
          "timestamp": "2026-07-29T10:00:00Z",
          "status": 404,
          "error": "Not Found",
          "message": "Route not found with id: 11111111-1111-1111-1111-111111111111",
          "path": "/api/routes/11111111-1111-1111-1111-111111111111/waypoints",
          "fieldErrors": {}
        }
        """;

    static final String WAYPOINT_NOT_FOUND = """
        {
          "timestamp": "2026-07-29T10:00:00Z",
          "status": 404,
          "error": "Not Found",
          "message": "Waypoint not found with id: 22222222-2222-2222-2222-222222222222",
          "path": "/api/routes/11111111-1111-1111-1111-111111111111/waypoints/22222222-2222-2222-2222-222222222222",
          "fieldErrors": {}
        }
        """;

    static final String POSITION_CONFLICT = """
        {
          "timestamp": "2026-07-29T10:00:00Z",
          "status": 409,
          "error": "Conflict",
          "message": "Waypoint position 1 already exists in route: 11111111-1111-1111-1111-111111111111",
          "path": "/api/routes/11111111-1111-1111-1111-111111111111/waypoints",
          "fieldErrors": {}
        }
        """;

    private WaypointApiExamples() {
    }
}
