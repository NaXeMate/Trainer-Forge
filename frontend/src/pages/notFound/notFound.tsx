// NotFound page — shown when the user navigates to a route that does not exist (404)

import { useNavigate } from "react-router-dom";
import Button from "../../components/common/button";
import "./notFound.css";

// Sad face SVG icon
function SadFaceIcon() {
  return (
    <svg
      className="not-found__svg"
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2" />
      <path d="M8 15s1.5-2 4-2 4 2 4 2" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
      <line x1="9" y1="9" x2="9.01" y2="9" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" />
      <line x1="15" y1="9" x2="15.01" y2="9" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" />
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

function NotFound() {
  const navigate = useNavigate();

  return (
    <main className="not-found">
      <div className="not-found__icon">
        <SadFaceIcon />
      </div>

      <p className="not-found__code">404</p>
      <h1 className="not-found__title">Page not found</h1>

      <p className="not-found__message">
        The page you are looking for does not exist or has been moved.
      </p>

      <div className="not-found__actions">
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

export default NotFound;
