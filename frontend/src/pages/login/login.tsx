// Login page — allows existing users to authenticate

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import Button from "../../components/common/button";
import "./login.css";

// Right arrow — used as trailingIcon in CONTINUE button
function ArrowIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path d="M5 12h14M13 6l6 6-6 6" stroke="currentColor" strokeWidth="2"
            strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

// Back arrow — used as leadingIcon in BACK button
function BackArrowIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path d="M19 12H5M11 6l-6 6 6 6" stroke="currentColor" strokeWidth="2"
            strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    if (!username || !password) {
      setError("Please fill in all fields.");
      return;
    }

    setError(null);
    setLoading(true);

    try {
      await login(username, password);
      navigate("/");
    } catch {
      setError("Invalid username or password.");
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    navigate(-1);
  };

  return (
    <main className="login-page">
      <div className="login-card">
        <div className="login-card__header">
          <img src="/logo.png" alt="TrainerForge logo" className="login-card__logo" />
          <h1 className="login-card__title">
            <span className="login-card__title--black">LOG</span>
            <span className="login-card__title--purple"> IN</span>
          </h1>
        </div>

        <div className="login-card__field">
          <label className="login-card__label" htmlFor="login-username">
            Username
          </label>
          <input
            id="login-username"
            type="text"
            className="login-card__input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </div>

        <div className="login-card__field">
          <label className="login-card__label" htmlFor="login-password">
            Password
          </label>
          <input
            id="login-password"
            type="password"
            className="login-card__input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <a href="#" className="login-card__forgot">
            Do you forgot your password?
          </a>
        </div>

        <div className="login-card__actions">
          <Button
            label="BACK"
            variant="ghost"
            leadingIcon={<BackArrowIcon />}
            uppercase
            onClick={handleBack}
          />
          <Button
            label="CONTINUE"
            variant="secondary"
            trailingIcon={<ArrowIcon />}
            uppercase
            onClick={handleSubmit}
          />
        </div>

        {error && <p className="login-card__error">{error}</p>}
        {loading && <p className="login-card__loading">Signing you in...</p>}
      </div>
    </main>
  );
}

export default Login;
