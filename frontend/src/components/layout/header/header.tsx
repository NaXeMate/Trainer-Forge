// Header component — main navigation bar shown on every page

import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../../context/AuthContext";
import "./header.css";

const navLinks = [
  { to: "/pokedex", label: "PokeDex" },
  { to: "/teams", label: "Teams" },
  { to: "/simulator", label: "Simulator" },
  { to: "/trainer-card", label: "Trainer Card" },
];

function UserIcon() {
  return (
    <svg
      className="header__user-icon"
      width="18"
      height="18"
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <circle cx="12" cy="8" r="4" stroke="currentColor" strokeWidth="2" />
      <path
        d="M5 20c0-4 3.5-6 7-6s7 2 7 6"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
      />
    </svg>
  );
}

function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const closeMenu = () => setMenuOpen(false);

  const handleLogin = () => {
    navigate("/login");
  };

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <header className="header">
      <div className="header__container">
        <div className="header__left">
          <NavLink to="/" className="header__brand" onClick={closeMenu}>
            <img
              src="/logo.png"
              alt="TrainerForge logo"
              className="header__logo"
            />
            <span className="header__title">TrainerForge</span>
          </NavLink>
        </div>

        <nav className="header__nav" aria-label="Main navigation">
          {navLinks.map(({ to, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                isActive ? "header__link active" : "header__link"
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="header__actions">
          {user ? (
            <>
              <span className="header__username">{user.userName}</span>
              <button
                type="button"
                className="header__btn header__btn--logout"
                onClick={handleLogout}
              >
                LOGOUT
              </button>
            </>
          ) : (
            <button
              type="button"
              className="header__btn header__btn--login"
              onClick={handleLogin}
            >
              <UserIcon />
              LOGIN
            </button>
          )}

          <button
            type="button"
            className="header__menu-toggle"
            aria-label="Toggle navigation menu"
            aria-expanded={menuOpen}
            onClick={() => setMenuOpen((open) => !open)}
          >
            ☰
          </button>
        </div>
      </div>

      {menuOpen && (
        <nav className="header__mobile-nav" aria-label="Mobile navigation">
          {navLinks.map(({ to, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                isActive ? "header__mobile-link active" : "header__mobile-link"
              }
              onClick={closeMenu}
            >
              {label}
            </NavLink>
          ))}
        </nav>
      )}
    </header>
  );
}

export default Header;
