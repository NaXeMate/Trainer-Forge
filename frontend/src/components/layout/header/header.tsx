import { Link, useNavigate } from "react-router";

import trainerForgeLogo from "../../../assets/brand/logo/logo_no_background.png";
import LoginButton from "../../common/loginButton";

import "./header.css";

const navigationItems = [
  { label: "PokeDex", href: "/pokedex" },
  { label: "Teams", href: "/teams" },
  { label: "Simulator", href: "/simulator" },
  { label: "Trainer Card", href: "/trainer" },
] as const;

function Header() {
  const navigate = useNavigate();

  const handleLogin = () => {
    navigate("/login");
  };

  return (
    <header className="site-header">
      <Link className="site-header__brand" to="/" aria-label="TrainerForge home">
        <img src={trainerForgeLogo} alt="" />
      </Link>

      <nav className="site-header__navigation" aria-label="Main navigation">
        <ul className="site-header__navigation-list">
          {navigationItems.map(({ label, href }) => (
            <li key={href}>
              <Link className="site-header__link" to={href}>
                {label}
              </Link>
            </li>
          ))}
        </ul>
      </nav>

      <LoginButton
        aria-label="Log in"
        className="site-header__login"
        onClick={handleLogin}/>
    </header>
  );
}

export default Header;
