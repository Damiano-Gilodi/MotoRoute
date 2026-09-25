package it.motoroute.waypoint.application;

import java.util.UUID;

public class WaypointPositionConflictException extends RuntimeException {
    public WaypointPositionConflictException(UUID routeId, Integer position) {
        super("Waypoint position " + position + " already exists in route: " + routeId);
    }
}
