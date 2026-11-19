import { Link, useLocation, useNavigate } from "react-router";

import trainerForgeLogo from "../../../assets/brand/logo/logo_dark_header.png";
import LoginButton from "../../common/loginButton";
import { useAuth } from "../../../context/useAuth";

import "./header.css";

const navigationItems = [
  { label: "PokeDex", href: "/pokedex" },
  { label: "Teams", href: "/teams" },
  { label: "Simulator", href: "/simulator" },
  { label: "Trainer Card", href: "/trainer" },
] as const;

function Header() {
  const navigate = useNavigate();
  const { pathname, search, hash } = useLocation();
  const { session, logout } = useAuth();

  const handleLogin = () => {
    navigate("/login", { state: { from: pathname + search + hash } });
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

      {session.status === "authenticated" ? (
        <div className="site-header__login site-header__account">
          <Link to="/trainer" title={session.profile.username}>{session.profile.username}</Link>
          <button type="button" onClick={logout}>Log out</button>
        </div>
      ) : session.status === "checking" ? (
        <span className="site-header__login site-header__account" role="status">Checking session…</span>
      ) : (
        <LoginButton
          aria-label="Log in"
          className="site-header__login"
          onClick={handleLogin}
        />
      )}
    </header>
  );
}

export default Header;
