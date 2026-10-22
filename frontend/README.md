# EventBook – Frontend

Simple React (Vite) frontend for the Event Booking System backend.

## Run it locally (step by step)

1. **Start the backend** (from the project root). You need a PostgreSQL database.
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/eventdb
   export SPRING_DATASOURCE_USERNAME=postgres
   export SPRING_DATASOURCE_PASSWORD=yourpassword
   # creates the first admin account on startup (only if it doesn't exist yet)
   export ADMIN_EMAIL=admin@test.com
   export ADMIN_PASSWORD=admin123
   ./mvnw spring-boot:run
   ```
2. **Start the frontend** (in a second terminal):
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
3. Open **http://localhost:5173**

## What you can do

| Role | Pages |
|------|-------|
| Anyone | Browse events |
| USER | See shows → pick seats on the seat map → pay (mock gateway) → My Bookings / cancel |
| ORGANIZER | Manage: dashboard, create/edit events & venues, generate seat layouts, create shows |
| ADMIN | Everything above + Admin page (stats, analytics, change user roles) |

**First-time demo flow:** log in as admin → Manage → create an Event → create a Venue → "Seat layout" on the venue → Shows tab → create a show → sign up as a normal user in another browser → book seats.

## Demo data

On first start the backend fills an empty database with 6 movies (Harry Potter, Lord of the Rings, Marvel),
5 well-known cinemas (Prasads, AMB, PVR ICON, Sathyam, Raj Mandir), 60 shows over the next 4 days and a few bookings.

Demo logins (password `Demo@1234`, change it with the `DEMO_PASSWORD` env var):

| Name | Email | Role |
|------|-------|------|
| Aarav Sharma | aarav.sharma@demo.eventbook.in | USER |
| Priya Reddy | priya.reddy@demo.eventbook.in | USER |
| Rohan Verma | rohan.verma@demo.eventbook.in | USER |
| Cineverse Entertainment | organizer@demo.eventbook.in | ORGANIZER |

Set `DEMO_DATA_ENABLED=false` to turn this off.

## Settings

- `VITE_API_URL` – backend URL (default `http://localhost:8080`). Copy `.env.example` to `.env` to change it.
- When you deploy the frontend, add its URL to the backend's `CORS_ALLOWED_ORIGINS` env var.

## Check that all APIs work

With the backend running (started with ADMIN_EMAIL/ADMIN_PASSWORD as above):
```bash
ADMIN_EMAIL=admin@test.com ADMIN_PASSWORD=admin123 node scripts/api-smoke-test.mjs
```
It creates test data and calls every endpoint, printing ✅ / ❌ for each.
