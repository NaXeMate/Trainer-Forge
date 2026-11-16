import { useId, useState } from "react";
import AuthForm, { AuthField } from "../../components/layout/authForm";
import PasswordStrength from "./passwordStrength";

export default function Register() {
  const [password, setPassword] = useState("");
  const strengthId = useId();

  return (
    <AuthForm mode="register" onReset={() => setPassword("")}>
      <AuthField label="Username" name="username" autoComplete="username" maxLength={30} />
      <AuthField label="Email" name="email" type="email" autoComplete="email" maxLength={254} />
      <div className="register-password">
        <AuthField label="Password" name="password" type="password" autoComplete="new-password"
          value={password} onChange={event => setPassword(event.currentTarget.value)} aria-describedby={strengthId} />
        <PasswordStrength id={strengthId} password={password} />
      </div>
      <AuthField label="Confirm password" name="confirmPassword" type="password" autoComplete="new-password" />
      <AuthField label="Real name" name="realName" autoComplete="name" maxLength={50} optional />
    </AuthForm>
  );
}
