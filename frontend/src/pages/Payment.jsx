// Pay for a PENDING booking (mock gateway). Seats are held for 5 minutes.
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api, money } from "../api";
import { Badge, Status, useApi } from "../components/ui";

const METHODS = ["UPI", "CARD", "NET_BANKING", "WALLET"];

export default function Payment() {
  const { id } = useParams();
  const booking = useApi(`/api/bookings/${id}`);
  const [method, setMethod] = useState("UPI");
  const [simulateSuccess, setSimulateSuccess] = useState(true);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [now, setNow] = useState(Date.now());

  useEffect(() => {
    const t = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(t);
  }, []);

  const b = booking.data;
  const secondsLeft = b ? Math.max(0, Math.floor((new Date(b.createdAt).getTime() + 5 * 60000 - now) / 1000)) : 0;

  async function pay() {
    setError("");
    try {
      const p = await api("/api/payments", { method: "POST", body: { bookingId: Number(id), paymentMethod: method, simulateSuccess } });
      setResult(p);
      booking.reload();
    } catch (e) {
      setError(e.message);
    }
  }

  return (
    <div className="card narrow">
      <h2>Payment</h2>
      <Status loading={booking.loading} error={booking.error} />
      {b && (
        <>
          <p>Booking #{b.id} <Badge value={b.status} /></p>
          <p>Seats: {b.seats.map((s) => `${s.rowName}${s.seatNumber}`).join(", ")}</p>
          <p className="big">{money(b.totalAmount)}</p>

          {b.status === "PENDING" && !result && (
            <>
              <p className="muted">Seats held for {Math.floor(secondsLeft / 60)}:{String(secondsLeft % 60).padStart(2, "0")}</p>
              <label>Payment method
                <select value={method} onChange={(e) => setMethod(e.target.value)}>
                  {METHODS.map((m) => <option key={m}>{m}</option>)}
                </select>
              </label>
              <label className="row">
                <input type="checkbox" checked={simulateSuccess} onChange={(e) => setSimulateSuccess(e.target.checked)} />
                Simulate successful payment (test gateway)
              </label>
              {error && <p className="error">{error}</p>}
              <button onClick={pay} disabled={secondsLeft === 0}>Pay {money(b.totalAmount)}</button>
            </>
          )}

          {result && (
            <p className={result.status === "SUCCESS" ? "success" : "error"}>
              Payment {result.status}{result.transactionId ? ` · ${result.transactionId}` : ""}
            </p>
          )}
          <Link to="/bookings">Go to My Bookings →</Link>
        </>
      )}
    </div>
  );
}
