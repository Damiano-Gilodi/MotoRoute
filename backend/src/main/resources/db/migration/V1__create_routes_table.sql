CREATE TABLE routes
(
  id              UUID                     PRIMARY KEY,
  name            VARCHAR(120)             NOT NULL,
  description     VARCHAR(2000),
  start_location  VARCHAR(120)             NOT NULL,
  end_location    VARCHAR(120)             NOT NULL,
  distance_km     NUMERIC(8, 2)            NOT NULL,
  difficulty      VARCHAR(20)              NOT NULL,
  created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT chk_routes_name_not_blank
    CHECK (LENGTH(TRIM(name)) > 0),

  CONSTRAINT chk_routes_start_location_not_blank
    CHECK (LENGTH(TRIM(start_location)) > 0),

  CONSTRAINT chk_routes_end_location_not_blank
    CHECK (LENGTH(TRIM(end_location)) > 0),

  CONSTRAINT chk_routes_distance_positive
    CHECK (distance_km > 0),

  CONSTRAINT chk_routes_difficulty
    CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD'))
);
