import { Link } from "react-router";

import "./error.css";

export default function ErrorPage() {
  const handleRetry = () => {
    window.location.reload();
  };

  return (
    <main className="error-page" aria-labelledby="error-page-title">
      <section className="error-page__panel">
        <p className="error-page__code" aria-hidden="true">
          500
        </p>
        <p className="error-page__eyebrow">Unexpected error</p>
        <h1 className="error-page__title" id="error-page-title">
          Something went wrong
        </h1>
        <p className="error-page__description">
          We couldn&apos;t complete your request. Please try again or return to
          the home page.
        </p>

        <div className="error-page__actions">
          <button
            className="error-page__action error-page__action--primary"
            type="button"
            onClick={handleRetry}>
            <span>Try again</span>
            <svg
              aria-hidden="true"
              className="error-page__action-icon"
              focusable="false"
              viewBox="0 0 24 24">
              <path
                d="M20 11a8 8 0 0 0-14.65-4L3 9m0 0V4m0 5h5M4 13a8 8 0 0 0 14.65 4L21 15m0 0v5m0-5h-5"
                fill="none"
                stroke="currentColor"
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"/>
            </svg>
          </button>

          <Link className="error-page__action error-page__action--secondary" to="/">
            <span>Back to home</span>
          </Link>
        </div>
      </section>
    </main>
  );
}
