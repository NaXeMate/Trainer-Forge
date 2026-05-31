// Footer component — shown at the bottom of every page

import "./footer.css";

const technicalLinks = [
  { label: "Contact and Contributions", href: "#" },
  { label: "Terms and Conditions", href: "#" },
  { label: "AI usage", href: "#" },
];

function Footer() {
  return (
    <footer className="footer">
      <div className="footer__container">
        <div className="footer__top">
          <div className="footer__column">
            <h2 className="footer-heading">About TrainerForge</h2>
            <p className="footer__text">
              TrainerForge is a website designed to complement any Pokémon game.
              Here you can find and plan everything you need to enhance your
              gameplay experience, as well as make contact with other trainers
              around the world.
            </p>
          </div>

          <div className="footer__column">
            <h2 className="footer-heading footer-heading--warning">
              ⚠ Warning
            </h2>
            <p className="footer__text">
              TrainerForge is a fan project created for non-commercial purposes.
              All non-original multimedia content, as well as Pokémon names and
              descriptions, belong to The Pokémon Company International and its
              affiliates.
            </p>
          </div>
        </div>

        <section className="footer__middle" aria-labelledby="footer-technical-links">
          <h2 id="footer-technical-links" className="overline">
            Technical Links
          </h2>
          <div className="footer__links">
            {technicalLinks.map((link, index) => (
              <span key={link.label} className="footer__link-item">
                {index > 0 && (
                  <span className="footer__divider" aria-hidden="true">
                    |
                  </span>
                )}
                <a href={link.href} className="footer__link">
                  {link.label}
                </a>
              </span>
            ))}
          </div>
        </section>

        <div className="footer__bottom">
          <p className="footer__copyright">© 2026 TrainerForge</p>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
