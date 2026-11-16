import { useRef, useState, type InputHTMLAttributes, type ReactEventHandler, type ReactNode, type SubmitEvent } from "react";
import { Link, Navigate, useLocation } from "react-router";

import logo from "../../../assets/brand/logo/logo_no_background.png";
import backArrow from "../../../assets/icons/navigation/back.svg";
import continueArrow from "../../../assets/icons/navigation/continue.svg";
import { useAuth } from "../../../context/useAuth";
import { ApiError } from "../../../services/api";

import "./authForm.css";

type AuthFormProps = {
  mode: "login" | "register";
  children: ReactNode;
  onReset?: ReactEventHandler<HTMLFormElement>;
};

type AuthFieldProps = Pick<InputHTMLAttributes<HTMLInputElement>,
  "type" | "autoComplete" | "maxLength" | "value" | "onChange" | "aria-describedby"> & {
  label: string;
  name: string;
  optional?: boolean;
};

/** Editable form field shared by the login and registration screens. */
export function AuthField({ label, name, optional = false, ...inputProps }: AuthFieldProps) {
  return (
    <label className="auth-field">
      <span className="auth-field__label">
        {label}
        {optional && <span className="auth-field__optional">(Optional)</span>}
      </span>
      <input className="auth-field__input" name={name} required={!optional} {...inputProps} />
    </label>
  );
}

/** Shared layout for both the login and registration forms */
export default function AuthForm({ mode, children, onReset }: AuthFormProps) {
  const isRegister = mode === "register";
  const { session, login, register, retry, logout } = useAuth();
  const { state } = useLocation();
  const from = (state as { from?: unknown } | null)?.from;
  const destination = typeof from === "string" && /^\/(?:trainer|simulator|teams)\/?(?:[?#]|$)/i.test(from) ? from : "/";
  const submitting = useRef(false);
  const [pending, setPending] = useState(false);
  const [error, setError] = useState("");
  const checking = session.status === "checking";

  async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    if (submitting.current || session.status !== "guest") return;

    const formElement = event.currentTarget;
    const form = new FormData(formElement);
    const username = String(form.get("username") ?? "");
    const password = String(form.get("password") ?? "");

    if (!username.trim() || !password.trim()) {
      setError("Enter your username and password.");
      return;
    }
    if (isRegister && form.get("confirmPassword") !== password) {
      setError("The passwords do not match.");
      return;
    }

    submitting.current = true;
    setPending(true);
    setError("");

    try {
      if (isRegister) {
        const realName = String(form.get("realName") ?? "").trim();
        await register({
          username: username.trim(),
          password,
          email: String(form.get("email") ?? "").trim(),
          realName: realName || undefined,
        });
      } else {
        await login({ username, password });
      }
      formElement.reset();
    } catch (failure) {
      setError(failure instanceof ApiError && failure.status < 500
        ? failure.message
        : "Could not reach the service. Please try again.");
    } finally {
      submitting.current = false;
      setPending(false);
    }
  }

  if (session.status === "authenticated") return <Navigate to={destination} replace />;

  return (
    <main className={`auth-page auth-page--${mode}`} aria-labelledby="auth-title">
      <section className="auth-card">
        <header className="auth-card__heading">
          <img src={logo} width="77" height="77" alt="" />
          <h1 id="auth-title">{isRegister ? "Sign " : "Log "}<span>{isRegister ? "up" : "in"}</span></h1>
        </header>

        <form method="post" onSubmit={handleSubmit} onReset={onReset}>
          <fieldset className="auth-card__fields" disabled={pending || session.status !== "guest"} aria-label="Account details">
            {children}
          </fieldset>

          <div className="auth-card__actions">
            <Link to="/" className="auth-card__action auth-card__action--back">
              <img src={backArrow} width="18" height="16" alt="" />
              Back
            </Link>
            <button className="auth-card__action auth-card__action--continue" type="submit"
              disabled={pending || session.status !== "guest"}>
              Continue
              <img src={continueArrow} width="18" height="16" alt="" />
            </button>
          </div>
          {pending && <p className="auth-card__status" role="status">Sending…</p>}
          {checking && <p className="auth-card__status" role="status">Checking your session…</p>}
          {error && session.status === "guest" && <p className="auth-card__status" role="alert">{error}</p>}
          {session.status === "error" && (
            <div className="auth-card__status">
              <p role="alert">Could not load your profile.</p>
              <button type="button" onClick={retry}>Retry</button>{" · "}
              <button type="button" onClick={logout}>Use another account</button>
            </div>
          )}
        </form>
      </section>

      <p className="auth-page__switch">
        {isRegister ? "Already have an account? " : "New to TrainerForge? "}
        <Link to={isRegister ? "/login" : "/register"} state={state}>{isRegister ? "Log in" : "Sign up"}</Link>
      </p>
    </main>
  );
}
