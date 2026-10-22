// Home page: all events, with a category filter (public, no login needed)
import { useState } from "react";
import { Link } from "react-router-dom";
import { Badge, Status, useApi } from "../components/ui";

const CATEGORIES = ["ALL", "MOVIE", "CONCERT", "SPORTS", "THEATRE", "COMEDY", "CONFERENCE"];

export default function Events() {
  const { data, loading, error } = useApi("/api/events?size=100&sort=id,desc");
  const [category, setCategory] = useState("ALL");
  const [search, setSearch] = useState("");

  const events = (data?.content || []).filter(
    (e) =>
      e.status === "ACTIVE" &&
      (category === "ALL" || e.category === category) &&
      e.title.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <>
      <h1>Upcoming events</h1>
      <div className="toolbar">
        <input placeholder="Search events…" value={search} onChange={(e) => setSearch(e.target.value)} />
        <div className="chips">
          {CATEGORIES.map((c) => (
            <button key={c} className={`chip ${c === category ? "active" : ""}`} onClick={() => setCategory(c)}>{c}</button>
          ))}
        </div>
      </div>
      <Status loading={loading} error={error} />
      {!loading && !error && events.length === 0 && <p className="muted">No events found.</p>}
      <div className="grid">
        {events.map((e) => (
          <Link to={`/events/${e.id}`} key={e.id} className="card event-card">
            {e.posterUrl ? <img src={e.posterUrl} alt="" /> : <div className="poster-placeholder">{e.category}</div>}
            <h3>{e.title}</h3>
            <p className="muted">{e.language} · {e.duration} min · {e.ageRating}</p>
            <Badge value={e.status} />
          </Link>
        ))}
      </div>
    </>
  );
}
