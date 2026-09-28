package it.motoroute.waypoint.infrastructure;

import it.motoroute.waypoint.domain.Waypoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WaypointRepository extends JpaRepository<Waypoint, UUID> {

    boolean existsByRoute_IdAndPosition(UUID routeId, Integer position);

    Page<Waypoint> findAllByRoute_Id(UUID routeId, Pageable pageable);
}
