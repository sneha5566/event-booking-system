// One small helper for every backend call.
// It adds the JWT token, converts JSON, and turns errors into readable messages.

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export function getSession() {
  try {
    return JSON.parse(localStorage.getItem("session")) || null;
  } catch {
    return null;
  }
}

export function saveSession(session) {
  if (session) localStorage.setItem("session", JSON.stringify(session));
  else localStorage.removeItem("session");
}

export async function api(path, { method = "GET", body } = {}) {
  const session = getSession();
  const headers = { "Content-Type": "application/json" };
  if (session?.token) headers.Authorization = `Bearer ${session.token}`;

  const res = await fetch(BASE_URL + path, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  // Token expired / not logged in -> clear session and go to login
  if (res.status === 401 && session && !path.startsWith("/auth")) {
    saveSession(null);
    window.location.href = "/login";
    throw new Error("Session expired. Please log in again.");
  }

  const text = await res.text();
  let data = text;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    /* plain text response, e.g. signup message */
  }

  if (!res.ok) {
    const message = (data && data.message) || (typeof data === "string" && data) || `Request failed (${res.status})`;
    throw new Error(message);
  }
  return data;
}

// Small formatting helpers
export const money = (n) => "₹" + Number(n ?? 0).toLocaleString("en-IN", { maximumFractionDigits: 2 });
export const dateTime = (s) =>
  s ? new Date(s).toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" }) : "-";
