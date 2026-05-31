// Register page — allows new users to create an account
// After the form is submitted, a pop-up collects additional trainer data
// before the final API call is made

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import Button from "../../components/common/button";
import "./register.css";

// Right arrow — used as trailingIcon in CONTINUE button
function ArrowIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path d="M5 12h14M13 6l6 6-6 6" stroke="currentColor" strokeWidth="2"
            strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

// Back arrow — used as leadingIcon in BACK button
function BackArrowIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path d="M19 12H5M11 6l-6 6 6 6" stroke="currentColor" strokeWidth="2"
            strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

// TODO: replace with API call
const REGIONS = [
  { id: 1, name: "Kanto" },
  { id: 2, name: "Johto" },
  { id: 3, name: "Hoenn" },
  { id: 4, name: "Sinnoh" },
  { id: 5, name: "Unova" },
  { id: 6, name: "Kalos" },
  { id: 7, name: "Alola" },
  { id: 8, name: "Galar" },
  { id: 9, name: "Paldea" },
];

function Register() {
  const navigate = useNavigate();
  const { register } = useAuth();

  // Form fields
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [realName, setRealName] = useState(""); // optional
  const [formError, setFormError] = useState<string | null>(null);

  // Pop-up state — shown after form submission to collect trainer-specific fields
  const [showPopup, setShowPopup] = useState(false);
  const [gender, setGender] = useState("");
  const [regionId, setRegionId] = useState<number | "">("");
  const [popupError, setPopupError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleFormSubmit = () => {
    if (!username || !email || !password || !confirmPassword) {
      setFormError("Please fill in all required fields.");
      return;
    }
    if (password !== confirmPassword) {
      setFormError("Passwords do not match.");
      return;
    }
    if (!email.includes("@")) {
      setFormError("Please enter a valid email.");
      return;
    }

    setFormError(null);
    setShowPopup(true);
  };

  const handlePopupSubmit = async () => {
    if (!gender || regionId === "") {
      setPopupError("Please fill in all fields.");
      return;
    }

    setPopupError(null);
    setLoading(true);

    const regionName = REGIONS.find((r) => r.id === regionId)?.name;

    try {
      await register({
        username,
        email,
        password,
        realName: realName || undefined,
        region: regionName,
        trainerClass: "NOVICE",
      });
      setShowPopup(false);
      navigate("/login", {
        state: { message: "Account created successfully. Please log in." },
      });
    } catch {
      setPopupError("Registration failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    navigate(-1);
  };

  return (
    <main className="register-page">
      {/* --- Registration form card --- */}
      <div className="register-card">
        <div className="register-card__header">
          <img src="/logo.png" alt="TrainerForge logo" className="register-card__logo" />
          <h1 className="register-card__title">
            <span className="register-card__title--black">SIGN</span>
            <span className="register-card__title--purple"> UP</span>
          </h1>
        </div>

        <div className="register-card__field">
          <label className="register-card__label" htmlFor="register-username">
            Username
          </label>
          <input
            id="register-username"
            type="text"
            className="register-card__input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </div>

        <div className="register-card__field">
          <label className="register-card__label" htmlFor="register-email">
            Email
          </label>
          <input
            id="register-email"
            type="email"
            className="register-card__input"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>

        <div className="register-card__field">
          <label className="register-card__label" htmlFor="register-password">
            Password
          </label>
          <input
            id="register-password"
            type="password"
            className="register-card__input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        <div className="register-card__field">
          <label className="register-card__label" htmlFor="register-confirm-password">
            Confirm Password
          </label>
          <input
            id="register-confirm-password"
            type="password"
            className="register-card__input"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />
        </div>

        <div className="register-card__field">
          <div className="register-card__label-row">
            <label className="register-card__label" htmlFor="register-real-name">
              Real Name
            </label>
            <span className="register-card__optional">(Optional)</span>
          </div>
          <input
            id="register-real-name"
            type="text"
            className="register-card__input"
            value={realName}
            onChange={(e) => setRealName(e.target.value)}
          />
        </div>

        {formError && <p className="register-card__error">{formError}</p>}

        <div className="register-card__actions">
          <Button
            label="BACK"
            variant="ghost"
            leadingIcon={<BackArrowIcon />}
            uppercase
            onClick={handleBack}
          />
          <Button
            label="CONTINUE"
            variant="secondary"
            trailingIcon={<ArrowIcon />}
            uppercase
            onClick={handleFormSubmit}
          />
        </div>
      </div>

      {/* --- Pop-up overlay: collects gender and region --- */}
      {showPopup && (
        <div
          className="register-popup-overlay"
          role="dialog"
          aria-modal="true"
          aria-labelledby="popup-title"
        >
          <div className="register-popup">
            <div className="register-popup__header">
              <img src="/logo.png" alt="TrainerForge logo" className="register-popup__logo" />
              <h2 id="popup-title" className="register-popup__title">
                <span className="register-popup__title--black">TRAINER</span>
                <span className="register-popup__title--purple"> DATA</span>
              </h2>
            </div>

            <p className="register-popup__subtitle">
              Tell us a bit more about your trainer profile.
            </p>

            <div className="register-card__field">
              <label className="register-card__label" htmlFor="popup-gender">
                Gender
              </label>
              <select
                id="popup-gender"
                className="register-popup__select"
                value={gender}
                onChange={(e) => setGender(e.target.value)}
              >
                <option value="" disabled>
                  Select gender
                </option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
            </div>

            <div className="register-card__field">
              <label className="register-card__label" htmlFor="popup-region">
                Region
              </label>
              <select
                id="popup-region"
                className="register-popup__select"
                value={regionId}
                onChange={(e) =>
                  setRegionId(e.target.value === "" ? "" : Number(e.target.value))
                }
              >
                <option value="" disabled>
                  Select region
                </option>
                {REGIONS.map((region) => (
                  <option key={region.id} value={region.id}>
                    {region.name}
                  </option>
                ))}
              </select>
            </div>

            {popupError && <p className="register-popup__error">{popupError}</p>}

            {loading && (
              <p className="register-popup__loading">Creating your account...</p>
            )}

            <div className="register-popup__actions">
              <Button
                label="BACK"
                variant="ghost"
                leadingIcon={<BackArrowIcon />}
                uppercase
                onClick={() => setShowPopup(false)}
              />
              <Button
                label="CONTINUE"
                variant="secondary"
                trailingIcon={<ArrowIcon />}
                uppercase
                onClick={handlePopupSubmit}
              />
            </div>
          </div>
        </div>
      )}
    </main>
  );
}

export default Register;
