package it.motoroute.route.application;

import java.util.UUID;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(UUID routeId) {
        super("Route not found with id: " + routeId);
    }
}
