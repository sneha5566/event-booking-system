// Admin: overall stats, analytics and user role management
import { api, money } from "../api";
import { StatGrid, Status, useApi } from "../components/ui";

const ROLES = ["USER", "ORGANIZER", "ADMIN"];

export default function Admin() {
  const dash = useApi("/api/admin/dashboard");
  const users = useApi("/api/admin/users");
  const revenue = useApi("/admin/analytics/revenue/monthly");
  const monthly = useApi("/admin/analytics/bookings/monthly");
  const topEvents = useApi("/admin/analytics/top-events");
  const topVenues = useApi("/admin/analytics/top-venues");
  const category = useApi("/admin/analytics/bookings/category");

  async function changeRole(id, role) {
    try { await api(`/api/admin/users/${id}/role`, { method: "PATCH", body: { role } }); users.reload(); }
    catch (e) { alert(e.message); }
  }

  const d = dash.data;
  return (
    <>
      <h1>Admin</h1>
      <Status loading={dash.loading} error={dash.error} />
      {d && (
        <StatGrid stats={[["Users", d.totalUsers], ["Organizers", d.totalOrganizers], ["Events", d.totalEvents],
          ["Venues", d.totalVenues], ["Shows", d.totalShows], ["Bookings", d.totalBookings],
          ["Confirmed", d.confirmedBookings], ["Cancelled", d.cancelledBookings], ["Revenue", money(d.totalRevenue)]]} />
      )}

      <h2>Analytics</h2>
      <div className="grid">
        <JsonTable title="Monthly revenue" q={revenue} />
        <JsonTable title="Monthly bookings" q={monthly} />
        <JsonTable title="Top events" q={topEvents} />
        <JsonTable title="Top venues" q={topVenues} />
        <JsonTable title="Bookings by category" q={category} />
      </div>

      <h2>Users</h2>
      <Status loading={users.loading} error={users.error} />
      <table>
        <thead><tr><th>#</th><th>Name</th><th>Email</th><th>Role</th></tr></thead>
        <tbody>
          {(users.data || []).map((u) => (
            <tr key={u.id}>
              <td>{u.id}</td><td>{u.name}</td><td>{u.email}</td>
              <td>
                <select value={u.role} onChange={(e) => changeRole(u.id, e.target.value)}>
                  {ROLES.map((r) => <option key={r}>{r}</option>)}
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}

// Shows any list of objects as a simple table (column names come from the data)
function JsonTable({ title, q }) {
  const rows = q.data || [];
  const cols = rows.length ? Object.keys(rows[0]) : [];
  return (
    <div className="card">
      <h3>{title}</h3>
      <Status loading={q.loading} error={q.error} />
      {!q.loading && !rows.length && <p className="muted">No data yet</p>}
      {rows.length > 0 && (
        <table>
          <thead><tr>{cols.map((c) => <th key={c}>{c}</th>)}</tr></thead>
          <tbody>{rows.map((r, i) => <tr key={i}>{cols.map((c) => <td key={c}>{String(r[c])}</td>)}</tr>)}</tbody>
        </table>
      )}
    </div>
  );
}
