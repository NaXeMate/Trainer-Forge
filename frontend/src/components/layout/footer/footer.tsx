import { Link } from "react-router";

import "./footer.css";

const technicalLinks = [
  { label: "Contact and Contributions", href: "/contact" },
  { label: "Terms and Conditions", href: "/terms-and-conditions" },
  { label: "AI usage", href: "/ai-usage" },
] as const;

function WarningIcon() {
  return (
    <svg
      aria-hidden="true"
      className="site-footer__warning-icon"
      fill="none"
      focusable="false"
      viewBox="0 0 24 24">
      <path
        d="m10.29 3.86-8.82 14a2 2 0 0 0 1.7 3.04h17.66a2 2 0 0 0 1.7-3.04l-8.82-14a2 2 0 0 0-3.42 0Z"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.8"
      />
      <path
        d="M12 9v4m0 4h.01"
        stroke="currentColor"
        strokeLinecap="round"
        strokeWidth="1.8"
      />
    </svg>
  );
}

function ModeIcon() {
  return (
    <svg
      aria-hidden="true"
      className="site-footer__mode-icon"
      fill="none"
      focusable="false"
      viewBox="0 0 24 24">
      <path
        d="M20.7 15.3A8.5 8.5 0 0 1 8.7 3.3 8.5 8.5 0 1 0 20.7 15.3Z"
        fill="currentColor"
      />
    </svg>
  );
}

function Footer() {
  return (
    <footer className="site-footer">
      <div className="site-footer__main">
        <div className="site-footer__information">
          <section className="site-footer__section">
            <h2 className="site-footer__title">ABOUT TRAINERFORGE</h2>
            <p className="site-footer__copy">
              TrainerForge is a website designed to complement any Pokémon game. Here you can find and plan everything you need to enhance your gameplay experience, as well as make contact with other trainers around the world.
            </p>
          </section>

          <section className="site-footer__section">
            <h2 className="site-footer__title site-footer__title--warning">
              <WarningIcon />
              <span>WARNING</span>
            </h2>
            <p className="site-footer__copy">
              TrainerForge is a fan project created for non-commercial purposes. All non-original multimedia content, as well as Pokémon names and descriptions, belong to The Pokémon Company International and its affiliates.
            </p>
          </section>
        </div>

        <hr className="site-footer__divider" />

        <nav className="site-footer__technical" aria-label="Technical links">
          <h2 className="site-footer__technical-title">TECHNICAL LINKS</h2>
          <ul className="site-footer__technical-list">
            {technicalLinks.map(({ label, href }, index) => (
              <li key={href}>
                {index > 0 && (
                  <span className="site-footer__separator" aria-hidden="true">
                    |
                  </span>
                )}
                <Link className="site-footer__technical-link" to={href}>
                  {label}
                </Link>
              </li>
            ))}
          </ul>
        </nav>
      </div>

      <div className="site-footer__bottom">
        <div className="site-footer__bottom-content">
          <p className="site-footer__copyright">
            <span>©2026 TrainerForge</span>
            <span>v1.0.0</span>
          </p>

          <button
            className="site-footer__mode"
            type="button"
            aria-label="Change color mode">
            <ModeIcon />
            <span>MODE</span>
          </button>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
