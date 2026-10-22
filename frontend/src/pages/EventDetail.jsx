// One event + the shows (date/venue) you can book for it
import { Link, useParams } from "react-router-dom";
import { dateTime, money } from "../api";
import { useAuth } from "../auth";
import { Badge, Status, useApi } from "../components/ui";

export default function EventDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const event = useApi(`/api/events/${id}`);
  // Shows API needs login. It returns all active shows; we keep the ones for this event.
  const shows = useApi(user ? "/api/shows?size=200&sort=startTime" : null);
  const eventShows = (shows.data?.content || []).filter((s) => String(s.eventId) === id);

  return (
    <>
      <Status loading={event.loading} error={event.error} />
      {event.data && (
        <div className="card">
          <h1>{event.data.title} <Badge value={event.data.category} /></h1>
          <p className="muted">{event.data.language} · {event.data.duration} min · {event.data.ageRating}</p>
          <p>{event.data.description}</p>
        </div>
      )}

      <h2>Shows</h2>
      {!user ? (
        <p><Link to="/login">Log in</Link> to see show times and book seats.</p>
      ) : (
        <>
          <Status loading={shows.loading} error={shows.error} />
          {!shows.loading && eventShows.length === 0 && <p className="muted">No shows scheduled yet.</p>}
          <table>
            <tbody>
              {eventShows.map((s) => (
                <tr key={s.id}>
                  <td>{dateTime(s.startTime)}</td>
                  <td>{s.venueName}</td>
                  <td className="muted">VIP {money(s.vipPrice)} · Premium {money(s.premiumPrice)} · Regular {money(s.regularPrice)}</td>
                  <td><Link className="btn" to={`/shows/${s.id}`}>Book seats</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </>
  );
}
