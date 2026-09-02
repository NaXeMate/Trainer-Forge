import { Link } from "react-router";

import "./notFound.css";

export default function NotFound() {
  return (
    <div className="not-found">

      <main className="not-found__main" aria-labelledby="not-found-title">
        <section className="not-found__panel">
          <p className="not-found__code" aria-hidden="true">404</p>
          <p className="not-found__eyebrow">Error 404</p>
          
          <h1 className="not-found__title" id="not-found-title">Page not found</h1>
          <p className="not-found__description">The page you&apos;re looking for doesn&apos;t exist or may have moved.</p>
          
          <Link className="not-found__action" to="/">
            <span>Back to home</span>
            <svg
              aria-hidden="true"
              className="not-found__action-icon"
              focusable="false"
              viewBox="0 0 24 24">
              <path
                d="M5 12h13m-6-6 6 6-6 6"
                fill="none"
                stroke="currentColor"
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"/>
            </svg>
          </Link>
        </section>
      </main>
    </div>
  );
}
