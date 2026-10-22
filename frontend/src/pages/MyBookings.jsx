// The logged-in user's bookings, with cancel / pay buttons
import { Link } from "react-router-dom";
import { api, dateTime, money } from "../api";
import { useAuth } from "../auth";
import { Badge, StatGrid, Status, useApi } from "../components/ui";

export default function MyBookings() {
  const { user } = useAuth();
  const bookings = useApi("/api/bookings/my?size=100&sort=createdAt,desc");
  const shows = useApi("/api/shows?size=500"); // to show event names next to bookings
  const dash = useApi(user.role === "USER" ? "/api/user/dashboard" : null);
  const showById = Object.fromEntries((shows.data?.content || []).map((s) => [s.id, s]));

  async function cancel(id) {
    if (!confirm("Cancel this booking? The amount will be refunded.")) return;
    try {
      const r = await api(`/api/bookings/${id}/cancel`, { method: "POST" });
      alert(`${r.message} Refund: ${money(r.refundAmount)}`);
      bookings.reload();
      dash.reload();
    } catch (e) {
      alert(e.message);
    }
  }

  const d = dash.data;
  return (
    <>
      <h1>My bookings</h1>
      {d && (
        <StatGrid stats={[["Total", d.totalBookings], ["Confirmed", d.confirmedBookings], ["Upcoming", d.upcomingBookings],
          ["Pending", d.pendingBookings], ["Cancelled", d.cancelledBookings], ["Total spent", money(d.totalSpent)]]} />
      )}
      <Status loading={bookings.loading} error={bookings.error} />
      {bookings.data?.content?.length === 0 && <p className="muted">No bookings yet. <Link to="/">Browse events</Link></p>}
      <table>
        <thead>
          <tr><th>#</th><th>Event</th><th>Show time</th><th>Seats</th><th>Amount</th><th>Status</th><th></th></tr>
        </thead>
        <tbody>
          {(bookings.data?.content || []).map((b) => {
            const s = showById[b.showId];
            return (
              <tr key={b.id}>
                <td>{b.id}</td>
                <td>{s ? `${s.eventTitle} @ ${s.venueName}` : `Show #${b.showId}`}</td>
                <td>{s ? dateTime(s.startTime) : "-"}</td>
                <td>{b.seats.map((x) => `${x.rowName}${x.seatNumber}`).join(", ")}</td>
                <td>{money(b.totalAmount)}</td>
                <td><Badge value={b.status} /></td>
                <td>
                  {b.status === "PENDING" && <Link className="btn-small" to={`/bookings/${b.id}/pay`}>Pay</Link>}
                  {b.status === "CONFIRMED" && <button className="btn-small danger" onClick={() => cancel(b.id)}>Cancel</button>}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </>
  );
}
