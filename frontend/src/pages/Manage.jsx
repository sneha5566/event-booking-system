// Organizer / Admin area: create events, venues (+ seat layout) and shows
import { useState } from "react";
import { api, dateTime, money } from "../api";
import { useAuth } from "../auth";
import { Badge, StatGrid, Status, useApi } from "../components/ui";

const CATEGORIES = ["MOVIE", "CONCERT", "SPORTS", "THEATRE", "COMEDY", "CONFERENCE"];

export default function Manage() {
  const { user } = useAuth();
  const [tab, setTab] = useState(user.role === "ORGANIZER" ? "dashboard" : "events");
  const tabs = [...(user.role === "ORGANIZER" ? ["dashboard"] : []), "events", "venues", "shows"];
  return (
    <>
      <h1>Manage</h1>
      <div className="tabs">
        {tabs.map((t) => (
          <button key={t} className={`chip ${tab === t ? "active" : ""}`} onClick={() => setTab(t)}>{t}</button>
        ))}
      </div>
      {tab === "dashboard" && <OrganizerDashboard />}
      {tab === "events" && <EventsAdmin />}
      {tab === "venues" && <VenuesAdmin />}
      {tab === "shows" && <ShowsAdmin />}
    </>
  );
}

function OrganizerDashboard() {
  const { data, loading, error } = useApi("/api/organizer/dashboard");
  return (
    <>
      <Status loading={loading} error={error} />
      {data && (
        <StatGrid stats={[["Shows", data.totalShows], ["Active", data.activeShows], ["Upcoming", data.upcomingShows],
          ["Bookings", data.totalBookings], ["Confirmed", data.confirmedBookings], ["Cancelled", data.cancelledBookings],
          ["Tickets sold", data.ticketsSold], ["Revenue", money(data.revenue)]]} />
      )}
    </>
  );
}

// Generic little form: fields = [{name, label, type, options}]
function SimpleForm({ fields, initial, onSubmit, submitLabel = "Save", onCancel }) {
  const [form, setForm] = useState(initial);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  async function submit(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      await onSubmit(form);
      setForm(initial);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }
  return (
    <form className="card form-grid" onSubmit={submit}>
      {fields.map((f) => (
        <label key={f.name} className={f.type === "textarea" ? "span2" : ""}>
          {f.label}
          {f.options ? (
            <select required value={form[f.name] ?? ""} onChange={(e) => setForm({ ...form, [f.name]: e.target.value })}>
              <option value="" disabled>Select…</option>
              {f.options.map((o) => <option key={o.value ?? o} value={o.value ?? o}>{o.label ?? o}</option>)}
            </select>
          ) : f.type === "textarea" ? (
            <textarea value={form[f.name] ?? ""} onChange={(e) => setForm({ ...form, [f.name]: e.target.value })} />
          ) : (
            <input type={f.type || "text"} required={!f.optional} step={f.type === "number" ? "any" : undefined}
              value={form[f.name] ?? ""} onChange={(e) => setForm({ ...form, [f.name]: e.target.value })} />
          )}
        </label>
      ))}
      {error && <p className="error span2">{error}</p>}
      <div className="span2 row">
        <button disabled={busy}>{busy ? "Saving…" : submitLabel}</button>
        {onCancel && <button type="button" className="secondary" onClick={onCancel}>Cancel</button>}
      </div>
    </form>
  );
}

/* ---------- Events ---------- */
const EVENT_FIELDS = [
  { name: "title", label: "Title" },
  { name: "category", label: "Category", options: CATEGORIES },
  { name: "duration", label: "Duration (minutes)", type: "number" },
  { name: "language", label: "Language" },
  { name: "ageRating", label: "Age rating (e.g. U, UA, A)" },
  { name: "posterUrl", label: "Poster image URL", optional: true },
  { name: "description", label: "Description", type: "textarea" },
];
const EMPTY_EVENT = { title: "", category: "", duration: "", language: "", ageRating: "", posterUrl: "", description: "" };

function EventsAdmin() {
  const list = useApi("/api/events?size=100&sort=id,desc");
  const [editing, setEditing] = useState(null);
  const toBody = (f) => ({ ...f, duration: Number(f.duration) });

  async function remove(id) {
    if (!confirm("Delete this event?")) return;
    try { await api(`/api/events/${id}`, { method: "DELETE" }); list.reload(); } catch (e) { alert(e.message); }
  }

  return (
    <>
      <h2>{editing ? `Edit event #${editing.id}` : "New event"}</h2>
      <SimpleForm
        key={editing?.id || "new"}
        fields={EVENT_FIELDS}
        initial={editing || EMPTY_EVENT}
        submitLabel={editing ? "Update event" : "Create event"}
        onCancel={editing ? () => setEditing(null) : undefined}
        onSubmit={async (f) => {
          if (editing) await api(`/api/events/${editing.id}`, { method: "PUT", body: toBody(f) });
          else await api("/api/events", { method: "POST", body: toBody(f) });
          setEditing(null);
          list.reload();
        }}
      />
      <h2>All events</h2>
      <Status loading={list.loading} error={list.error} />
      <table>
        <thead><tr><th>#</th><th>Title</th><th>Category</th><th>Status</th><th></th></tr></thead>
        <tbody>
          {(list.data?.content || []).map((e) => (
            <tr key={e.id}>
              <td>{e.id}</td><td>{e.title}</td><td>{e.category}</td><td><Badge value={e.status} /></td>
              <td>
                <button className="btn-small" onClick={() => setEditing(e)}>Edit</button>{" "}
                <button className="btn-small danger" onClick={() => remove(e.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}

/* ---------- Venues ---------- */
const VENUE_FIELDS = [
  { name: "name", label: "Venue name" },
  { name: "address", label: "Address" },
  { name: "city", label: "City" },
  { name: "state", label: "State" },
  { name: "totalSeats", label: "Total seats", type: "number" },
];
const EMPTY_VENUE = { name: "", address: "", city: "", state: "", totalSeats: "" };

function VenuesAdmin() {
  const list = useApi("/api/venues?size=100&sort=id,desc");
  const [editing, setEditing] = useState(null);
  const [seatVenue, setSeatVenue] = useState(null);
  const toBody = (f) => ({ ...f, totalSeats: Number(f.totalSeats) });

  async function remove(id) {
    if (!confirm("Delete this venue?")) return;
    try { await api(`/api/venues/${id}`, { method: "DELETE" }); list.reload(); } catch (e) { alert(e.message); }
  }

  return (
    <>
      <h2>{editing ? `Edit venue #${editing.id}` : "New venue"}</h2>
      <SimpleForm
        key={editing?.id || "new"}
        fields={VENUE_FIELDS}
        initial={editing || EMPTY_VENUE}
        submitLabel={editing ? "Update venue" : "Create venue"}
        onCancel={editing ? () => setEditing(null) : undefined}
        onSubmit={async (f) => {
          if (editing) await api(`/api/venues/${editing.id}`, { method: "PUT", body: toBody(f) });
          else await api("/api/venues", { method: "POST", body: toBody(f) });
          setEditing(null);
          list.reload();
        }}
      />

      {seatVenue && (
        <>
          <h2>Generate seat layout for “{seatVenue.name}”</h2>
          <p className="muted">Rows are named A, B, C… The first rows are VIP, then Premium, the rest Regular. You can only do this once per venue.</p>
          <SimpleForm
            fields={[
              { name: "rows", label: "Rows (max 26)", type: "number" },
              { name: "seatsPerRow", label: "Seats per row", type: "number" },
              { name: "vipRows", label: "VIP rows", type: "number" },
              { name: "premiumRows", label: "Premium rows", type: "number" },
            ]}
            initial={{ rows: 8, seatsPerRow: 12, vipRows: 2, premiumRows: 2 }}
            submitLabel="Generate seats"
            onCancel={() => setSeatVenue(null)}
            onSubmit={async (f) => {
              const seats = await api("/api/seats/generate", {
                method: "POST",
                body: { venueId: seatVenue.id, rows: +f.rows, seatsPerRow: +f.seatsPerRow, vipRows: +f.vipRows, premiumRows: +f.premiumRows },
              });
              alert(`${seats.length} seats created`);
              setSeatVenue(null);
            }}
          />
        </>
      )}

      <h2>All venues</h2>
      <Status loading={list.loading} error={list.error} />
      <table>
        <thead><tr><th>#</th><th>Name</th><th>City</th><th>Seats</th><th>Status</th><th></th></tr></thead>
        <tbody>
          {(list.data?.content || []).map((v) => (
            <tr key={v.id}>
              <td>{v.id}</td><td>{v.name}</td><td>{v.city}</td><td>{v.totalSeats}</td><td><Badge value={v.status} /></td>
              <td>
                <button className="btn-small" onClick={() => setSeatVenue(v)}>Seat layout</button>{" "}
                <button className="btn-small" onClick={() => setEditing(v)}>Edit</button>{" "}
                <button className="btn-small danger" onClick={() => remove(v.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}

/* ---------- Shows ---------- */
function ShowsAdmin() {
  const events = useApi("/api/events?size=200");
  const venues = useApi("/api/venues?size=200");
  const shows = useApi("/api/shows/filter?size=100&sort=startTime,desc");

  const fields = [
    { name: "eventId", label: "Event", options: (events.data?.content || []).map((e) => ({ value: e.id, label: e.title })) },
    { name: "venueId", label: "Venue", options: (venues.data?.content || []).map((v) => ({ value: v.id, label: `${v.name}, ${v.city}` })) },
    { name: "startTime", label: "Starts at", type: "datetime-local" },
    { name: "endTime", label: "Ends at", type: "datetime-local" },
    { name: "vipPrice", label: "VIP price (₹)", type: "number" },
    { name: "premiumPrice", label: "Premium price (₹)", type: "number" },
    { name: "regularPrice", label: "Regular price (₹)", type: "number" },
  ];

  return (
    <>
      <h2>New show</h2>
      <p className="muted">Tip: the venue needs a seat layout first (Venues tab → Seat layout).</p>
      <SimpleForm
        fields={fields}
        initial={{ eventId: "", venueId: "", startTime: "", endTime: "", vipPrice: 500, premiumPrice: 300, regularPrice: 150 }}
        submitLabel="Create show"
        onSubmit={async (f) => {
          await api("/api/shows", {
            method: "POST",
            body: {
              eventId: +f.eventId, venueId: +f.venueId,
              startTime: f.startTime + ":00", endTime: f.endTime + ":00",
              vipPrice: +f.vipPrice, premiumPrice: +f.premiumPrice, regularPrice: +f.regularPrice,
            },
          });
          shows.reload();
        }}
      />
      <h2>All shows</h2>
      <Status loading={shows.loading} error={shows.error} />
      <table>
        <thead><tr><th>#</th><th>Event</th><th>Venue</th><th>Starts</th><th>Organizer</th><th>Status</th></tr></thead>
        <tbody>
          {(shows.data?.content || []).map((s) => (
            <tr key={s.id}>
              <td>{s.id}</td><td>{s.eventTitle}</td><td>{s.venueName}</td><td>{dateTime(s.startTime)}</td>
              <td>{s.organizerName}</td><td><Badge value={s.status} /></td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
