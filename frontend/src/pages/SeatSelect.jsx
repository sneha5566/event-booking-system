// Seat map for a show. Click seats, then "Book" -> creates a PENDING booking -> payment page
import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { api, dateTime, money } from "../api";
import { Status, useApi } from "../components/ui";

export default function SeatSelect() {
  const { id } = useParams();
  const navigate = useNavigate();
  const show = useApi(`/api/shows/${id}`);
  const seats = useApi(`/api/shows/${id}/seats`);
  const [selected, setSelected] = useState([]);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  // group seats by row: { A: [...], B: [...] }
  const rows = {};
  (seats.data || []).forEach((s) => (rows[s.rowName] ||= []).push(s));

  const toggle = (seat) => {
    if (seat.status !== "AVAILABLE") return;
    setSelected((sel) => (sel.includes(seat.id) ? sel.filter((x) => x !== seat.id) : [...sel, seat.id]));
  };
  const total = (seats.data || []).filter((s) => selected.includes(s.id)).reduce((sum, s) => sum + Number(s.price), 0);

  async function book() {
    setError("");
    setBusy(true);
    try {
      const booking = await api("/api/bookings", { method: "POST", body: { showId: Number(id), showSeatIds: selected } });
      navigate(`/bookings/${booking.id}/pay`);
    } catch (e) {
      setError(e.message);
      seats.reload();
      setSelected([]);
    } finally {
      setBusy(false);
    }
  }

  return (
    <>
      {show.data && (
        <div className="card">
          <h1>{show.data.eventTitle}</h1>
          <p className="muted">{show.data.venueName} · {dateTime(show.data.startTime)}</p>
        </div>
      )}
      <Status loading={seats.loading} error={seats.error || show.error} />

      <div className="card seatmap">
        <div className="screen">STAGE / SCREEN</div>
        {Object.entries(rows).map(([row, list]) => (
          <div className="seat-row" key={row}>
            <span className="row-label">{row}</span>
            {list.map((s) => (
              <button
                key={s.id}
                title={`${row}${s.seatNumber} · ${s.seatType} · ${money(s.price)}`}
                className={`seat seat-${s.seatType.toLowerCase()} ${s.status !== "AVAILABLE" ? "taken" : ""} ${selected.includes(s.id) ? "selected" : ""}`}
                onClick={() => toggle(s)}
                disabled={s.status !== "AVAILABLE"}
              >
                {s.seatNumber}
              </button>
            ))}
          </div>
        ))}
        <div className="legend">
          <span><i className="seat seat-vip" /> VIP</span>
          <span><i className="seat seat-premium" /> Premium</span>
          <span><i className="seat seat-regular" /> Regular</span>
          <span><i className="seat selected" /> Selected</span>
          <span><i className="seat taken" /> Taken</span>
        </div>
      </div>

      <div className="card checkout">
        <div>{selected.length} seat(s) · <b>{money(total)}</b></div>
        {error && <p className="error">{error}</p>}
        <button disabled={!selected.length || busy} onClick={book}>{busy ? "Booking…" : "Book & pay"}</button>
      </div>
    </>
  );
}
