// Button — reusable button component used across the entire app

import "./button.css";
import type { ReactNode } from "react";

type ButtonVariant = "primary" | "secondary" | "danger" | "ghost";

type ButtonProps = {
  label: string; // Visible text inside the button
  onClick?: () => void; // Click handler
  variant?: ButtonVariant; // Visual style (default: "secondary")
  leadingIcon?: ReactNode; // Icon shown before the label (optional)
  trailingIcon?: ReactNode; // Icon shown after the label (optional)
  fullWidth?: boolean; // If true, button takes 100% width (default: false)
  type?: "button" | "submit" | "reset"; // HTML button type (default: "button")
  ariaLabel?: string; // Accessible label for screen readers (optional)
  uppercase?: boolean; // If true, applies text-transform uppercase (default: false)
};

function Button({
  label,
  onClick,
  variant = "secondary",
  leadingIcon,
  trailingIcon,
  fullWidth = false,
  type = "button",
  ariaLabel,
  uppercase = false,
}: ButtonProps) {
  const classNames = [
    "btn",
    `btn--${variant}`,
    fullWidth ? "btn--full-width" : "",
    uppercase ? "btn--uppercase" : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <button
      type={type}
      className={classNames}
      onClick={onClick}
      aria-label={ariaLabel}
    >
      {leadingIcon && (
        <span className="btn__icon btn__icon--leading" aria-hidden="true">
          {leadingIcon}
        </span>
      )}
      <span className="btn__label">{label}</span>
      {trailingIcon && (
        <span className="btn__icon btn__icon--trailing" aria-hidden="true">
          {trailingIcon}
        </span>
      )}
    </button>
  );
}

export default Button;
