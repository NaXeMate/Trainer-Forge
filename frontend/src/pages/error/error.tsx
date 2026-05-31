// Error page — shown when the router catches an unexpected runtime error

import { useRouteError } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import Button from "../../components/common/button";
import "./error.css";

// Warning triangle SVG icon
function WarningIcon() {
  return (
    <svg
      className="error-page__svg"
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <path
        d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <line x1="12" y1="9" x2="12" y2="13" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
      <line x1="12" y1="17" x2="12.01" y2="17" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
    </svg>
  );
}

// Home icon for the back button
function HomeIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path d="M3 9.5L12 3l9 6.5V20a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V9.5z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M9 21V12h6v9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function Error() {
  // useRouteError gives us the error thrown by the router
  const error = useRouteError() as {
    status?: number;
    statusText?: string;
    message?: string;
  };

  const navigate = useNavigate();

  return (
    <main className="error-page">
      <div className="error-page__icon">
        <WarningIcon />
      </div>

      <h1 className="error-page__title">Something went wrong!</h1>

      <p className="error-page__message">
        {error?.statusText || error?.message || "An unexpected error has occurred."}
      </p>

      <div className="error-page__actions">
        <Button
          label="Back to Home"
          variant="secondary"
          leadingIcon={<HomeIcon />}
          uppercase
          onClick={() => navigate("/")}
        />
      </div>
    </main>
  );
}

export default Error;
