import {
  Navigate,
  Route,
  Routes,
} from "react-router";

import {CreateRoutePage} from "./features/routes/create/CreateRoutePage";
import {RoutesPage} from "./features/routes/list/RoutesPage";
import {RouteDetailsPage} from "./features/routes/details/RouteDetailsPage.jsx";
import {RouteEditPage} from "./features/routes/edit/RouteEditPage.jsx";

export default function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <Navigate
            to="/routes"
            replace
          />
        }
      />

      <Route
        path="/routes"
        element={<RoutesPage/>}
      />

      <Route
        path="/routes/new"
        element={<CreateRoutePage/>}
      />

      <Route
        path="/routes/:routeId"
        element={<RouteDetailsPage/>}
      />

      <Route
        path="/routes/:routeId/edit"
        element={<RouteEditPage/>}
      />
    </Routes>
  );
}
