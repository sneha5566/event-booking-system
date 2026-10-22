// The list of pages (routes) and the top navigation bar.
import { Link, NavLink, Route, Routes, useNavigate } from "react-router-dom";
import { useAuth } from "./auth";
import { RequireRole } from "./components/ui";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Events from "./pages/Events";
import EventDetail from "./pages/EventDetail";
import SeatSelect from "./pages/SeatSelect";
import Payment from "./pages/Payment";
import MyBookings from "./pages/MyBookings";
import Manage from "./pages/Manage";
import Admin from "./pages/Admin";

export default function App() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const canManage = user && ["ORGANIZER", "ADMIN"].includes(user.role);

  return (
    <>
      <header className="nav">
        <Link to="/" className="brand">🎟️ EventBook</Link>
        <nav>
          <NavLink to="/" end>Events</NavLink>
          {user && <NavLink to="/bookings">My Bookings</NavLink>}
          {canManage && <NavLink to="/manage">Manage</NavLink>}
          {user?.role === "ADMIN" && <NavLink to="/admin">Admin</NavLink>}
        </nav>
        <div className="nav-right">
          {user ? (
            <>
              <span className="muted">{user.name} · {user.role}</span>
              <button className="btn-small" onClick={() => { logout(); navigate("/login"); }}>Log out</button>
            </>
          ) : (
            <>
              <NavLink to="/login">Log in</NavLink>
              <NavLink to="/signup">Sign up</NavLink>
            </>
          )}
        </div>
      </header>

      <main className="container">
        <Routes>
          <Route path="/" element={<Events />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/events/:id" element={<EventDetail />} />
          <Route path="/shows/:id" element={<RequireRole><SeatSelect /></RequireRole>} />
          <Route path="/bookings/:id/pay" element={<RequireRole><Payment /></RequireRole>} />
          <Route path="/bookings" element={<RequireRole><MyBookings /></RequireRole>} />
          <Route path="/manage" element={<RequireRole roles={["ORGANIZER", "ADMIN"]}><Manage /></RequireRole>} />
          <Route path="/admin" element={<RequireRole roles={["ADMIN"]}><Admin /></RequireRole>} />
          <Route path="*" element={<p>Page not found.</p>} />
        </Routes>
      </main>
    </>
  );
}
