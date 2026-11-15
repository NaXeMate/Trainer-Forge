import AuthForm, { AuthField } from "../../components/layout/authForm";

/** Registration screen for a new TrainerForge account. */
export default function Register() {
  return (
    <AuthForm mode="register">
      <AuthField label="Username" name="username" autoComplete="username" maxLength={30} />
      <AuthField label="Email" name="email" type="email" autoComplete="email" maxLength={254} />
      <AuthField label="Password" name="password" type="password" autoComplete="new-password" />
      <AuthField label="Confirm password" name="confirmPassword" type="password" autoComplete="new-password" />
      <AuthField label="Real name" name="realName" autoComplete="name" maxLength={50} optional />
    </AuthForm>
  );
}
