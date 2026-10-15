#!/usr/bin/env bash

set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"
ROUTE_COUNT="${ROUTE_COUNT:-3}"
WAYPOINT_COUNT="${WAYPOINT_COUNT:-21}"

echo "======================================"
echo "MotoRoute - Seed sviluppo"
echo "======================================"
echo "Backend: $BASE_URL"
echo "Route: $ROUTE_COUNT"
echo "Waypoint per route: $WAYPOINT_COUNT"
echo

extract_id() {
  echo "$1" |
    tr -d '\n' |
    sed -E 's/.*"id"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/'
}

create_route() {
  local index="$1"

  local response

  response=$(curl --fail --silent --show-error \
    -X POST "$BASE_URL/api/routes" \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -d "{
      \"name\": \"DEV Route $index\",
      \"description\": \"Itinerario generato automaticamente per test di sviluppo\",
      \"startLocation\": \"Partenza test $index\",
      \"endLocation\": \"Arrivo test $index\",
      \"distanceKm\": $((50 + index)).00,
      \"difficulty\": \"HARD\"
    }")

  extract_id "$response"
}

create_waypoint() {
  local route_id="$1"
  local route_index="$2"
  local position="$3"

  local lat
  local lon

  lat=$(printf "45.%06d" "$((route_index * 1000 + position))")
  lon=$(printf "9.%06d" "$((route_index * 1000 + position))")

  curl --fail --silent --show-error \
    -X POST "$BASE_URL/api/routes/$route_id/waypoints" \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -d "{
      \"name\": \"DEV Waypoint $route_index-$position\",
      \"description\": \"Waypoint generato automaticamente per test\",
      \"position\": $position,
      \"latitude\": $lat,
      \"longitude\": $lon
    }" > /dev/null
}

for route_index in $(seq 1 "$ROUTE_COUNT"); do
  echo "Creazione route $route_index/$ROUTE_COUNT..."

  route_id=$(create_route "$route_index")

  if [ -z "$route_id" ]; then
    echo "ERRORE: impossibile recuperare l'id della route $route_index"
    exit 1
  fi

  echo "Route creata: $route_id"

  for position in $(seq 1 "$WAYPOINT_COUNT"); do
    create_waypoint "$route_id" "$route_index" "$position"

    echo "  Waypoint $position/$WAYPOINT_COUNT"
  done

  echo
done

echo "======================================"
echo "Seed completato"
echo "======================================"
