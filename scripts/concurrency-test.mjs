// Seat-locking race test: several users try to book the SAME seat at the SAME moment.
// Expected result: exactly ONE booking succeeds for each seat, all others are rejected.
//
// Run (needs Node.js 18+):
//   node scripts/concurrency-test.mjs
// Options (environment variables):
//   API_URL       backend to test      (default: the live Render backend)
//   DEMO_PASSWORD demo users' password (default: Demo@1234)
//   ROUNDS        how many seats to race for (default: 5)
//
// The test bookings are left PENDING, so the scheduler frees the seats again after 5 minutes.

const API = process.env.API_URL || "https://event-booking-system-3-x07x.onrender.com";
const PASSWORD = process.env.DEMO_PASSWORD || "Demo@1234";
const ROUNDS = Number(process.env.ROUNDS || 5);
const USERS = [
  "aarav.sharma@demo.eventbook.in",
  "priya.reddy@demo.eventbook.in",
  "rohan.verma@demo.eventbook.in",
];

async function call(method, path, token, body) {
  const res = await fetch(API + path, {
    method,
    headers: { "Content-Type": "application/json", ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }
  return { status: res.status, data };
}

console.log(`Testing ${API} (first request may take ~1 minute if the server is asleep)\n`);

// 1. Log in every demo user
const tokens = [];
for (const email of USERS) {
  const r = await call("POST", "/auth/login", null, { email, password: PASSWORD });
  if (r.status !== 200) { console.error(`Login failed for ${email}: ${r.status}`); process.exit(1); }
  tokens.push({ email, token: r.data.token });
}

// 2. Pick the latest show (least likely to be busy) and its free seats
const shows = (await call("GET", "/api/shows?size=200&sort=startTime,desc", tokens[0].token)).data.content;
const show = shows[0];
const seats = (await call("GET", `/api/shows/${show.id}/seats`, tokens[0].token)).data;
const free = seats.filter((s) => s.status === "AVAILABLE").slice(-ROUNDS); // back rows
console.log(`Show #${show.id}: ${show.eventTitle} @ ${show.venueName}`);
console.log(`${USERS.length} users will race for each of ${free.length} seats\n`);

// 3. For each seat, fire all users' booking requests at the same time
let passed = 0;
for (const seat of free) {
  const results = await Promise.all(
    tokens.map((u) => call("POST", "/api/bookings", u.token, { showId: show.id, showSeatIds: [seat.id] })
      .then((r) => ({ ...r, email: u.email })))
  );
  const winners = results.filter((r) => r.status === 200 || r.status === 201);
  const losers = results.filter((r) => !(r.status === 200 || r.status === 201));
  const ok = winners.length === 1;
  if (ok) passed++;
  console.log(`${ok ? "✅" : "❌"} Seat ${seat.rowName}${seat.seatNumber}: ${winners.length} booked, ${losers.length} rejected`);
  winners.forEach((w) => console.log(`     booked by   ${w.email.split("@")[0]}`));
  losers.forEach((l) => console.log(`     rejected    ${l.email.split("@")[0]} → ${l.status} ${l.data?.message ?? ""}`));
}

// 4. Confirm the seats really show as LOCKED now
const after = (await call("GET", `/api/shows/${show.id}/seats`, tokens[0].token)).data;
const locked = after.filter((s) => free.some((f) => f.id === s.id) && s.status === "LOCKED").length;
console.log(`\nSeats now LOCKED: ${locked}/${free.length} (released automatically after 5 minutes if unpaid)`);
console.log(`\nResult: ${passed}/${free.length} seats had exactly one winner`);
process.exit(passed === free.length ? 0 : 1);
