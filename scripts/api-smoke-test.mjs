// End-to-end check of every API. Run with the backend already running:
//   ADMIN_EMAIL=admin@test.com ADMIN_PASSWORD=admin123 node scripts/api-smoke-test.mjs
// (start the backend with the same ADMIN_EMAIL / ADMIN_PASSWORD so the admin exists)
const BASE = process.env.API_URL || "http://localhost:8080";
const ADMIN = { email: process.env.ADMIN_EMAIL, password: process.env.ADMIN_PASSWORD };
let pass = 0, fail = 0;

async function call(method, path, token, body) {
  const res = await fetch(BASE + path, {
    method,
    headers: { "Content-Type": "application/json", ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  let data; try { data = JSON.parse(text); } catch { data = text; }
  return { status: res.status, data };
}
async function check(name, expected, method, path, token, body) {
  const r = await call(method, path, token, body);
  const ok = [].concat(expected).includes(r.status);
  ok ? pass++ : fail++;
  console.log(`${ok ? "✅" : "❌"} ${method} ${path} -> ${r.status} ${ok ? "" : JSON.stringify(r.data).slice(0, 200)}  (${name})`);
  return r.data;
}

const rnd = Date.now();
const userEmail = `user${rnd}@test.com`, orgEmail = `org${rnd}@test.com`;

console.log(`Testing ${BASE}\n`);
await check("public test endpoint (needs login)", [200, 401], "GET", "/test");
await check("signup user", 200, "POST", "/auth/signup", null, { name: "Test User", email: userEmail, password: "secret123" });
await check("duplicate signup rejected", 409, "POST", "/auth/signup", null, { name: "x", email: userEmail, password: "secret123" });
await check("signup validation", 400, "POST", "/auth/signup", null, { name: "", email: "bad", password: "1" });
await check("signup organizer", 200, "POST", "/auth/signup", null, { name: "Test Org", email: orgEmail, password: "secret123" });
await check("wrong password", 401, "POST", "/auth/login", null, { email: userEmail, password: "nope1234" });
const user = await check("login user", 200, "POST", "/auth/login", null, { email: userEmail, password: "secret123" });
await check("no token -> 401", 401, "GET", "/api/bookings/my");
await check("bad token -> 401", 401, "GET", "/api/bookings/my", "garbage.token.here");

if (!ADMIN.email) { console.log("\n⚠️  Set ADMIN_EMAIL / ADMIN_PASSWORD to test organizer + admin APIs."); process.exit(fail ? 1 : 0); }
const admin = await check("login admin", 200, "POST", "/auth/login", null, ADMIN);
const A = admin.token, U = user.token;

await check("user cannot list users", 403, "GET", "/api/admin/users", U);
const users = await check("admin lists users", 200, "GET", "/api/admin/users", A);
const org = users.find((u) => u.email === orgEmail);
await check("promote to organizer (PATCH)", 200, "PATCH", `/api/admin/users/${org.id}/role`, A, { role: "ORGANIZER" });
const O = (await call("POST", "/auth/login", null, { email: orgEmail, password: "secret123" })).data.token;

await check("user cannot create event", 403, "POST", "/api/events", U, { title: "x", category: "MOVIE", duration: 1, language: "x", ageRating: "U" });
const ev = await check("create event", [200, 201], "POST", "/api/events", O, { title: `Smoke Test ${rnd}`, description: "test", category: "CONCERT", duration: 120, language: "English", ageRating: "U" });
await check("list events (public)", 200, "GET", "/api/events?size=5");
await check("get event", 200, "GET", `/api/events/${ev.id}`);
await check("update event", 200, "PUT", `/api/events/${ev.id}`, O, { title: `Smoke Test ${rnd} (edited)`, description: "test", category: "CONCERT", duration: 120, language: "English", ageRating: "U" });
await check("filter events", 200, "GET", "/api/events/filter?category=CONCERT", O);
await check("missing event -> 404", 404, "GET", "/api/events/99999999");

const venue = await check("create venue", [200, 201], "POST", "/api/venues", O, { name: `Hall ${rnd}`, address: "1 Main St", city: "Amaravati", state: "AP", totalSeats: 20 });
await check("list venues", 200, "GET", "/api/venues", O);
await check("get venue", 200, "GET", `/api/venues/${venue.id}`, O);
await check("update venue", 200, "PUT", `/api/venues/${venue.id}`, O, { name: `Hall ${rnd}`, address: "2 Main St", city: "Amaravati", state: "AP", totalSeats: 20 });
await check("filter venues", 200, "GET", "/api/venues/filter?city=Amaravati", O);
await check("show without seats rejected", 400, "POST", "/api/shows", O, { eventId: ev.id, venueId: venue.id, startTime: "2099-01-01T18:00:00", endTime: "2099-01-01T21:00:00", vipPrice: 500, premiumPrice: 300, regularPrice: 100 });
await check("generate seats", [200, 201], "POST", "/api/seats/generate", O, { venueId: venue.id, rows: 4, seatsPerRow: 5, vipRows: 1, premiumRows: 1 });
await check("seats by venue", 200, "GET", `/api/seats/venue/${venue.id}`, O);

const start = new Date(Date.now() + 86400000 * 3), end = new Date(start.getTime() + 3 * 3600000);
const iso = (d) => d.toISOString().slice(0, 19);
const show = await check("create show", [200, 201], "POST", "/api/shows", O, { eventId: ev.id, venueId: venue.id, startTime: iso(start), endTime: iso(end), vipPrice: 500, premiumPrice: 300, regularPrice: 100 });
await check("overlapping show rejected", 400, "POST", "/api/shows", O, { eventId: ev.id, venueId: venue.id, startTime: iso(start), endTime: iso(end), vipPrice: 500, premiumPrice: 300, regularPrice: 100 });
await check("list shows", 200, "GET", "/api/shows", U);
await check("get show", 200, "GET", `/api/shows/${show.id}`, U);
await check("filter shows", 200, "GET", `/api/shows/filter?eventId=${ev.id}`, O);
const seats = await check("show seat map", 200, "GET", `/api/shows/${show.id}/seats`, U);

const [s1, s2, s3] = seats;
const b1 = await check("create booking", [200, 201], "POST", "/api/bookings", U, { showId: show.id, showSeatIds: [s1.id, s2.id] });
await check("same seats again rejected", 400, "POST", "/api/bookings", U, { showId: show.id, showSeatIds: [s1.id] });
await check("get booking", 200, "GET", `/api/bookings/${b1.id}`, U);
await check("cancel pending rejected", 400, "POST", `/api/bookings/${b1.id}/cancel`, U);
const p1 = await check("pay booking", [200, 201], "POST", "/api/payments", U, { bookingId: b1.id, paymentMethod: "UPI", simulateSuccess: true });
await check("pay twice rejected", 400, "POST", "/api/payments", U, { bookingId: b1.id, paymentMethod: "UPI", simulateSuccess: true });
await check("get payment", 200, "GET", `/api/payments/${p1.id}`, U);
await check("payments by booking", 200, "GET", `/api/payments/booking/${b1.id}`, U);
await check("other user cannot see payment", [403, 404], "GET", `/api/payments/${p1.id}`, O);

const b2 = await check("booking for failed payment", [200, 201], "POST", "/api/bookings", U, { showId: show.id, showSeatIds: [s3.id] });
const p2 = await check("failed payment", [200, 201], "POST", "/api/payments", U, { bookingId: b2.id, paymentMethod: "CARD", simulateSuccess: false });
if (p2.status !== "FAILED") { fail++; console.log("❌ failed payment should have status FAILED"); }

await check("my bookings", 200, "GET", "/api/bookings/my", U);
await check("filter my bookings", 200, "GET", "/api/bookings/my/filter?status=CONFIRMED", U);
await check("user dashboard", 200, "GET", "/api/user/dashboard", U);
await check("organizer dashboard", 200, "GET", "/api/organizer/dashboard", O);
await check("admin dashboard", 200, "GET", "/api/admin/dashboard", A);
for (const p of ["revenue/monthly", "bookings/monthly", "top-events", "top-venues", "bookings/category"])
  await check("analytics", 200, "GET", `/admin/analytics/${p}`, A);

await check("cancel confirmed booking", 200, "POST", `/api/bookings/${b1.id}/cancel`, U);
const after = await call("GET", `/api/shows/${show.id}/seats`, U);
const freed = after.data.filter((s) => [s1.id, s2.id, s3.id].includes(s.id)).every((s) => s.status === "AVAILABLE");
freed ? pass++ : fail++;
console.log(`${freed ? "✅" : "❌"} seats released after cancel / failed payment`);

await check("delete venue", [200, 204], "DELETE", `/api/venues/${venue.id}`, O);
await check("delete event", [200, 204], "DELETE", `/api/events/${ev.id}`, O);

console.log(`\n${pass} passed, ${fail} failed`);
process.exit(fail ? 1 : 0);
