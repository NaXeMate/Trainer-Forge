// Data returned by the auth endpoints (/api/auth/login, /api/auth/register)
export type AuthResponse = {
  token: string;
  username: string;
};

// Lightweight session user kept in the app/localStorage
export type UserResponse = {
  userName: string;
  id?: number;
  email?: string;
  registerTime?: string;
};

// Data required to register a new trainer — mirrors the backend TrainerInputDto
export type CreateUser = {
  username: string;
  password: string;
  email?: string;
  realName?: string;
  region?: string; // region name, e.g. "Kanto"
  trainerClass?: string; // e.g. "NOVICE"
};
