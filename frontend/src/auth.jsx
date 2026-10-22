// Keeps track of who is logged in (token, name, role) for the whole app.
import { createContext, useContext, useState } from "react";
import { api, getSession, saveSession } from "./api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(getSession());

  async function login(email, password) {
    const data = await api("/auth/login", { method: "POST", body: { email, password } });
    saveSession(data); // {token, userId, name, email, role}
    setUser(data);
    return data;
  }

  function logout() {
    saveSession(null);
    setUser(null);
  }

  return <AuthContext.Provider value={{ user, login, logout }}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
