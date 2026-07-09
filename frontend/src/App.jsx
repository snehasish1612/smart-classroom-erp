import { useState } from "react";
import "./App.css";
import { clearSession, getStoredSession } from "./api";
import AuthPage from "./pages/AuthPage";
import Dashboard from "./pages/Dashboard";

function App() {
  const [session, setSession] = useState(() => getStoredSession());

  if (!session) {
    return <AuthPage onAuthenticated={setSession} />;
  }

  const logout = () => {
    clearSession();
    setSession(null);
  };

  return <Dashboard user={session.user} onLogout={logout} />;
}

export default App;
