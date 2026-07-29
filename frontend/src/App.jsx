import {
  Navigate,
  Route,
  Routes,
} from "react-router";

import {CreateRoutePage} from "./features/routes/create/CreateRoutePage";
import {RoutesPage} from "./features/routes/list/RoutesPage";

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
    </Routes>
  );
}
