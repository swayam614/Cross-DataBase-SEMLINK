
import { BrowserRouter, Routes, Route, Link } from "react-router-dom"
import Home from "./pages/Home"
import OwlManager from "./pages/OwlManager"

function App() {
  return (
    <BrowserRouter>

      {/* 🔹 NAVBAR */}
      <nav style={{ padding: "20px", background: "#eee" }}>
        <Link to="/" style={{ marginRight: "20px" }}>🏠 Home</Link>
        <Link to="/owl">🧠 OWL Manager</Link>
      </nav>

      {/* 🔹 ROUTES */}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/owl" element={<OwlManager />} />
      </Routes>

    </BrowserRouter>
  )
}

export default App