import AuthForm, { AuthField } from "../../components/layout/authForm";

/** Login screen with fields for an existing TrainerForge account. */
export default function Login() {
  return (
    <AuthForm mode="login">
      <AuthField label="Username" name="username" autoComplete="username" maxLength={30} />
      <div>
        <AuthField
          label="Password"
          name="password"
          type="password"
          autoComplete="current-password"
        />
        <button className="auth-card__recovery" type="button" disabled>Forgot your password?</button>
      </div>
    </AuthForm>
  );
}
