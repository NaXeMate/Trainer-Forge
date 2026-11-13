import { useState, type InputHTMLAttributes, type ReactNode } from "react";
import { Link } from "react-router";

import logo from "../../../assets/brand/logo/logo_no_background.png";
import backArrow from "../../../assets/icons/navigation/back.svg";
import continueArrow from "../../../assets/icons/navigation/continue.svg";

import "./authForm.css";

type AuthFormProps = {
  mode: "login" | "register";
  children: ReactNode;
};

type AuthFieldProps = Pick<InputHTMLAttributes<HTMLInputElement>, "type" | "autoComplete" | "maxLength"> & {
  label: string;
  name: string;
  optional?: boolean;
};

/** An editable form field shared by the login and registration screens. */
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

/** The shared layout for the login and registration forms. */
export default function AuthForm({ mode, children }: AuthFormProps) {
  const isRegister = mode === "register";
  const [submitted, setSubmitted] = useState(false);

  return (
    <main className={`auth-page auth-page--${mode}`} aria-labelledby="auth-title">
      <section className="auth-card">
        <header className="auth-card__heading">
          <img src={logo} width="77" height="77" alt="" />
          <h1 id="auth-title">{isRegister ? "Sign " : "Log "}<span>{isRegister ? "up" : "in"}</span></h1>
        </header>

        <form method="post" onSubmit={event => {
          event.preventDefault();
          setSubmitted(true);
        }}>
          <div className="auth-card__fields">{children}</div>

          <div className="auth-card__actions">
            <Link to="/" className="auth-card__action auth-card__action--back">
              <img src={backArrow} width="18" height="16" alt="" />
              Back
            </Link>
            <button className="auth-card__action auth-card__action--continue" type="submit">
              Continue
              <img src={continueArrow} width="18" height="16" alt="" />
            </button>
          </div>
          {submitted && (
            <p className="auth-card__status" role="status">
              {isRegister ? "Registration" : "Login"} is not available yet.
            </p>
          )}
        </form>
      </section>

      <p className="auth-page__switch">
        {isRegister ? "Already have an account? " : "New to TrainerForge? "}
        <Link to={isRegister ? "/login" : "/register"}>{isRegister ? "Log in" : "Sign up"}</Link>
      </p>
    </main>
  );
}
