package it.motoroute.route.infrastructure;

import it.motoroute.route.domain.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, UUID> {
}
