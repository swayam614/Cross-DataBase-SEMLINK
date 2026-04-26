<<<<<<< HEAD
=======
// import { BrowserRouter, Routes, Route, Link } from "react-router-dom"
// import Home from "./pages/Home"
// import OwlManager from "./pages/OwlManager"

// function App() {
//   return (
//     <BrowserRouter>

//       {/* 🔹 NAVBAR */}
//         <nav style={{
//             padding: "12px 20px",
//             background: "#3b82f6",
//             display: "flex",
//             gap: "20px",
//             boxShadow: "0 2px 6px rgba(0,0,0,0.1)"
//         }}>
//             <Link
//                 to="/"
//                 style={{
//                     marginRight: "10px",
//                     color: "#cbd5f5",
//                     textDecoration: "none",
//                     padding: "6px 12px",
//                     borderRadius: "6px"
//                 }}
//                 onMouseEnter={(e) => e.currentTarget.style.background = "#334155"}
//                 onMouseLeave={(e) => e.currentTarget.style.background = "transparent"}
//             >
//                  Home
//             </Link>

//             <Link
//                 to="/owl"
//                 style={{
//                     color: "#cbd5f5",
//                     textDecoration: "none",
//                     padding: "6px 12px",
//                     borderRadius: "6px"
//                 }}
//                 onMouseEnter={(e) => e.currentTarget.style.background = "#334155"}
//                 onMouseLeave={(e) => e.currentTarget.style.background = "transparent"}
//             >
//                  OWL Manager
//             </Link>
//         </nav>

//       {/* 🔹 ROUTES */}
//       <Routes>
//         <Route path="/" element={<Home />} />
//         <Route path="/owl" element={<OwlManager />} />
//       </Routes>

//     </BrowserRouter>
//   )
// }

// export default App
>>>>>>> 3683453 (Add manual mapping and remove the bug of duplication by making price and rating as class and assumed that this two data properties are changing so add in that time stamp and make it temporal)

import { BrowserRouter, Routes, Route, Link, useLocation } from "react-router-dom"
import Home from "./pages/Home"
import OwlManager from "./pages/OwlManager"

function Navbar() {
  const location = useLocation()

  const linkStyle = (path: string) =>
    `px-4 py-2 rounded-lg transition ${
      location.pathname === path
        ? "bg-white text-blue-600 font-semibold"
        : "text-white hover:bg-blue-500"
    }`

  return (
    <nav className="bg-blue-600 px-6 py-3 flex gap-4 shadow-md">

      <Link to="/" className={linkStyle("/")}>
        Home
      </Link>

      <Link to="/owl" className={linkStyle("/owl")}>
        OWL Manager
      </Link>

    </nav>
  )
}

function App() {
  return (
    <BrowserRouter>

      {/* ✅ NEW CLEAN NAVBAR */}
      <Navbar />

      {/* ROUTES */}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/owl" element={<OwlManager />} />
      </Routes>

    </BrowserRouter>
  )
}

export default App