package it.motoroute.waypoint.application;

import java.util.UUID;

public class WaypointNotFoundException extends RuntimeException {
    public WaypointNotFoundException(UUID waypointId) {
        super("Waypoint not found with id: " + waypointId);
    }
}


