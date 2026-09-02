import { Link } from "react-router";

import "./footer.css";

const modeIcon =
  "https://www.figma.com/api/mcp/asset/0309afa9-5411-4aee-9b82-ed36c89ee08b.svg";
const warningIcon =
  "https://www.figma.com/api/mcp/asset/a3117297-0a16-48bd-9c05-924f4a8e1263.svg";

const technicalLinks = [
  { label: "Contact and Contributions", href: "/contact" },
  { label: "Terms and Conditions", href: "/terms-and-conditions" },
  { label: "AI usage", href: "/ai-usage" },
] as const;

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
              <img className="site-footer__warning-icon" src={warningIcon} alt="" />
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
            <img src={modeIcon} alt="" />
            <span>MODE</span>
          </button>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
