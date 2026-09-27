CREATE TABLE waypoints
(
  id          UUID PRIMARY KEY,
  route_id    UUID                     NOT NULL,
  name        VARCHAR(120)             NOT NULL,
  description VARCHAR(2000),
  position    INTEGER                  NOT NULL,
  latitude    NUMERIC(8, 6)            NOT NULL,
  longitude   NUMERIC(9, 6)            NOT NULL,
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_waypoints_route
    FOREIGN KEY (route_id) REFERENCES routes (id)
      ON DELETE CASCADE,

  CONSTRAINT chk_waypoints_name_not_blank
    CHECK (LENGTH(TRIM(name)) > 0),

  CONSTRAINT chk_waypoints_position
    CHECK (position > 0),

  CONSTRAINT chk_waypoints_latitude
    CHECK (latitude BETWEEN -90 AND 90),

  CONSTRAINT chk_waypoints_longitude
    CHECK (longitude BETWEEN -180 AND 180),

  CONSTRAINT uk_waypoints_route_position
    UNIQUE (route_id, position)
);
