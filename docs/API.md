# API Reference

Base URL (live): `https://event-booking-system-3-x07x.onrender.com`
Interactive docs (Swagger UI): [`/swagger-ui/index.html`](https://event-booking-system-3-x07x.onrender.com/swagger-ui/index.html)

All request and response bodies are JSON. Protected endpoints need the header:

```
Authorization: Bearer <token from /auth/login>
```

**Who can call it:** 🌍 anyone · 👤 any logged-in user · 🎬 ORGANIZER or ADMIN · 🛡️ ADMIN only

List endpoints are paginated with Spring's `?page=0&size=20&sort=field,asc` parameters and return
`{ "content": [...], "totalElements": n, "totalPages": n, ... }`.

---

## 1. Authentication

| Method | Path | Access | Body | Returns |
|---|---|---|---|---|
| POST | `/auth/signup` | 🌍 | `{ "name", "email", "password" }` (password ≥ 6 chars) | `"User registered successfully"` · **409** if email exists |
| POST | `/auth/login` | 🌍 | `{ "email", "password" }` | `{ "token", "userId", "name", "email", "role" }` · **401** if wrong |

Every new sign-up gets the role `USER`. An admin can promote users (see §8).

## 2. Events (movies, concerts, …)

| Method | Path | Access | Notes |
|---|---|---|---|
| GET | `/api/events` | 🌍 | Paginated list |
| GET | `/api/events/{id}` | 🌍 | One event · **404** if missing |
| GET | `/api/events/filter?status=&category=&language=` | 🎬 | Filter |
| POST | `/api/events` | 🎬 | Create · body below |
| PUT | `/api/events/{id}` | 🎬 | Update · same body |
| DELETE | `/api/events/{id}` | 🎬 | Soft delete (status → `INACTIVE`) |

```json
{ "title": "Avengers: Endgame", "description": "…", "category": "MOVIE",
  "duration": 181, "language": "English", "ageRating": "UA", "posterUrl": null }
```
`category`: `MOVIE · CONCERT · SPORTS · THEATRE · COMEDY · CONFERENCE`

## 3. Venues (cinemas, halls)

| Method | Path | Access | Notes |
|---|---|---|---|
| GET | `/api/venues` | 👤 | Paginated list |
| GET | `/api/venues/{id}` | 👤 | One venue |
| GET | `/api/venues/filter?status=&city=&state=&name=` | 🎬 | Filter |
| POST | `/api/venues` | 🎬 | `{ "name", "address", "city", "state", "totalSeats" }` |
| PUT | `/api/venues/{id}` | 🎬 | Update |
| DELETE | `/api/venues/{id}` | 🎬 | Soft delete |

## 4. Seat layout

| Method | Path | Access | Notes |
|---|---|---|---|
| POST | `/api/seats/generate` | 🎬 | `{ "venueId", "rows", "seatsPerRow", "vipRows", "premiumRows" }` – creates rows A, B, C… (max 26). First rows are VIP, then Premium, the rest Regular. Only once per venue. |
| GET | `/api/seats/venue/{venueId}` | 👤 | The venue's physical seats |

## 5. Shows (an event at a venue at a time)

| Method | Path | Access | Notes |
|---|---|---|---|
| GET | `/api/shows` | 👤 | Active shows, paginated |
| GET | `/api/shows/{id}` | 👤 | One show |
| GET | `/api/shows/{id}/seats` | 👤 | **Seat map** for this show: `[{ id, rowName, seatNumber, seatType, price, status }]` where status is `AVAILABLE · LOCKED · BOOKED`. The `id` here is what you send when booking. |
| GET | `/api/shows/filter?status=&eventId=&venueId=&organizerId=&startFrom=&startTo=` | 🎬 | Filter |
| POST | `/api/shows` | 🎬 | Create · body below |

```json
{ "eventId": 5, "venueId": 1, "startTime": "2026-11-01T19:00:00", "endTime": "2026-11-01T22:20:00",
  "vipPrice": 450, "premiumPrice": 320, "regularPrice": 220 }
```
Rules: the venue must have a seat layout, `startTime` must be in the future, `endTime` after `startTime`,
and the venue must be free (no overlapping show). Creating a show copies every venue seat into a
per-show seat with its price.

## 6. Bookings

| Method | Path | Access | Notes |
|---|---|---|---|
| POST | `/api/bookings` | 👤 | `{ "showId", "showSeatIds": [..] }` → booking with status `PENDING`; seats become `LOCKED` |
| GET | `/api/bookings/my` | 👤 | My bookings, paginated |
| GET | `/api/bookings/my/filter?status=&showId=&bookingFrom=&bookingTo=` | 👤 | Filter my bookings |
| GET | `/api/bookings/{id}` | 👤 | One of my bookings |
| POST | `/api/bookings/{id}/cancel` | 👤 | Only `CONFIRMED` bookings, before the show starts. Payment → `REFUNDED`, seats → `AVAILABLE` |

A `PENDING` booking that is not paid within **5 minutes** is cancelled automatically by a scheduler and its seats are released.

## 7. Payments (mock gateway)

| Method | Path | Access | Notes |
|---|---|---|---|
| POST | `/api/payments` | 👤 | `{ "bookingId", "paymentMethod": "UPI·CARD·NET_BANKING·WALLET", "simulateSuccess": true }` |
| GET | `/api/payments/{id}` | 👤 | Only the owner · **403** otherwise |
| GET | `/api/payments/booking/{bookingId}` | 👤 | All payment attempts for my booking |

`simulateSuccess: true` → payment `SUCCESS`, booking `CONFIRMED`, seats `BOOKED`.
`simulateSuccess: false` → payment `FAILED`, booking `CANCELLED`, seats released.

## 8. Users, dashboards and analytics

| Method | Path | Access | Returns |
|---|---|---|---|
| GET | `/api/admin/users` | 🛡️ | All users |
| PATCH | `/api/admin/users/{id}/role` | 🛡️ | `{ "role": "USER·ORGANIZER·ADMIN" }` |
| GET | `/api/user/dashboard` | USER | My totals: bookings, confirmed, cancelled, pending, upcoming, total spent |
| GET | `/api/organizer/dashboard` | ORGANIZER | My shows, tickets sold, revenue |
| GET | `/api/admin/dashboard` | 🛡️ | Whole-system totals and revenue |
| GET | `/admin/analytics/revenue/monthly` | 🛡️ | Revenue per month |
| GET | `/admin/analytics/bookings/monthly` | 🛡️ | Bookings per month |
| GET | `/admin/analytics/top-events` | 🛡️ | Best-selling events |
| GET | `/admin/analytics/top-venues` | 🛡️ | Busiest venues |
| GET | `/admin/analytics/bookings/category` | 🛡️ | Bookings per event category |

## Errors

Every error has the same shape and a meaningful HTTP status:

```json
{ "status": 404, "message": "Show not found" }
```

| Status | Meaning | Example |
|---|---|---|
| 400 | Invalid input or rule broken | seat already taken, overlapping show, validation message |
| 401 | Not logged in / token invalid or expired | missing `Authorization` header |
| 403 | Logged in but not allowed | a USER calling an admin endpoint |
| 404 | Not found | wrong id |
| 409 | Conflict | email already registered, two people booking the same seat at once |
