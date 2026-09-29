import type { ButtonHTMLAttributes } from "react";

import "./accessButton.css";

export type AccessButtonProps = Omit<ButtonHTMLAttributes<HTMLButtonElement>, "children" | "type">;

export default function AccessButton({ className, ...props }: AccessButtonProps) {
  const buttonClassName = ["access-button", className].filter(Boolean).join(" ");

  return (
    <button {...props} className={buttonClassName} type="button">
      <span className="access-button__label">ENTER</span>
      <svg
        aria-hidden="true"
        className="access-button__icon"
        fill="none"
        focusable="false"
        stroke="currentColor"
        viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="9.75" strokeWidth="1.5" />
        <path
          d="m12.75 15 3-3m0 0-3-3m3 3h-7.5"
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth="1.5" />
      </svg>
    </button>
  );
}
