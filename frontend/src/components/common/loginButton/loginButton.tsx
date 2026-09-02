import type { ButtonHTMLAttributes } from "react";

import "./loginButton.css";

export type LoginButtonProps = Omit<ButtonHTMLAttributes<HTMLButtonElement>, "children" | "type">;

export default function LoginButton({ className, ...props }: LoginButtonProps) {
  const buttonClassName = ["login-button", className].filter(Boolean).join(" ");

  return (
    <button {...props} className={buttonClassName} type="button">
      <svg
        aria-hidden="true"
        className="login-button__icon"
        fill="currentColor"
        focusable="false"
        viewBox="0 0 24 24">
        
        {/* UserIcon from Heroicons, by Tailwind Labs. */}
        <path
          clipRule="evenodd"
          d="M7.5 6a4.5 4.5 0 1 1 9 0 4.5 4.5 0 0 1-9 0ZM3.751 20.105a8.25 8.25 0 0 1 16.498 0 .75.75 0 0 1-.437.695A18.683 18.683 0 0 1 12 22.5c-2.786 0-5.433-.608-7.812-1.7a.75.75 0 0 1-.437-.695Z"
          fillRule="evenodd"/>
      </svg>

      <span className="login-button__label">LOGIN</span>
    </button>
  );
}
