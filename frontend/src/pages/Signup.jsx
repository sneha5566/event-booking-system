import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api";
import { useAuth } from "../auth";

export default function Signup() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function submit(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      await api("/auth/signup", { method: "POST", body: form });
      await login(form.email, form.password); // log in straight away
      navigate("/");
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });
  return (
    <form className="card narrow" onSubmit={submit}>
      <h2>Create account</h2>
      <label>Name<input required value={form.name} onChange={set("name")} /></label>
      <label>Email<input type="email" required value={form.email} onChange={set("email")} /></label>
      <label>Password (min 6)<input type="password" minLength={6} required value={form.password} onChange={set("password")} /></label>
      {error && <p className="error">{error}</p>}
      <button disabled={busy}>{busy ? "Creating…" : "Sign up"}</button>
      <p className="muted">Have an account? <Link to="/login">Log in</Link></p>
    </form>
  );
}
