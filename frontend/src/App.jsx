import {
  Navigate,
  Route,
  Routes,
} from "react-router";

import {CreateRoutePage} from "./features/routes/create/CreateRoutePage";

export default function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <Navigate
            to="/routes/new"
            replace
          />
        }
      />

      <Route
        path="/routes/new"
        element={<CreateRoutePage/>}
      />
    </Routes>
  );
}
