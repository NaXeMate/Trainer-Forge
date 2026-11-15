import type { TrainerProfile, TrainerProfileChanges } from "./trainerTypes";

export type LoginInput = {
  username: string;
  password: string;
};

export type RegisterInput = LoginInput & {
  email: string;
  realName?: string;
};

export type AuthResponse = {
  token: string;
  username: string;
};

export type AuthSession =
  | { status: "guest" }
  | { status: "checking" }
  | { status: "error" }
  | { status: "authenticated"; profile: TrainerProfile };

export type AuthContextValue = {
  session: AuthSession;
  login: (input: LoginInput) => Promise<void>;
  register: (input: RegisterInput) => Promise<void>;
  retry: () => void;
  logout: () => void;
  updateProfile: (changes: TrainerProfileChanges) => Promise<void>;
};
