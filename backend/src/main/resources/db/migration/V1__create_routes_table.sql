CREATE TABLE routes
(
  id           UUID                     PRIMARY KEY,
  name         VARCHAR(120)             NOT NULL,
  description  TEXT,
  distance_km  NUMERIC(8, 2),
  created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
