import "./passwordStrength.css";

const STRENGTH_LABELS = ["No password entered", "Weak", "Fair", "Good", "Strong"];
const SEGMENTS = [1, 2, 3, 4];
const COMMON_PASSWORD = /^(?:password|contrase(?:ñ|n)a|qwerty|letmein|trainerforge)[\p{N}\p{P}\p{S}]*$/iu;

function getStrength(password: string) {
  if (!password.trim()) return 0;

  const characters = [...password];
  const distinctCharacters = new Set(characters.map(character => character.toLowerCase())).size;
  if (characters.length < 8 || distinctCharacters < 4 || COMMON_PASSWORD.test(password.trim())) return 1;

  const characterGroups = [/\p{Ll}/u, /\p{Lu}/u, /\p{N}/u, /[^\p{L}\p{N}\s]/u]
    .filter(pattern => pattern.test(password)).length;

  if (characters.length >= 20 || (characters.length >= 16 && characterGroups >= 3)) return 4;
  if (characters.length >= 16 || (characters.length >= 12 && characterGroups >= 2)) return 3;
  return 2;
}

export default function PasswordStrength({ id, password }: { id: string; password: string }) {
  const strength = getStrength(password);

  return (
    <div className="password-strength">
      <div className="password-strength__segments" role="meter" aria-label="Password strength"
        aria-valuemin={0} aria-valuemax={4} aria-valuenow={strength} aria-valuetext={STRENGTH_LABELS[strength]}>
        {SEGMENTS.map(segment => (
          <span key={segment} data-filled={segment <= strength} aria-hidden="true" />
        ))}
      </div>
      <span id={id} className="visually-hidden">Password strength: {STRENGTH_LABELS[strength]}.</span>
    </div>
  );
}
