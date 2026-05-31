/**
 * @file AuthContext.tsx
 * @description Provides authentication context for the app.
 * Handles user state, login, register, and session persistence via localStorage.
 */

import {
  createContext,
  useContext,
  useState,
  useEffect,
  type ReactNode,
} from "react";
import type { UserResponse, CreateUser } from "../types";
import { login as loginService, createUser } from "../services/userService";

// Shape of the auth context
type AuthContextType = {
  user: UserResponse | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<void>;
  register: (userData: CreateUser) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

type AuthProviderProps = {
  children: ReactNode;
};

// Provider component: wraps the app and manages auth state
export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Persist both the JWT and the lightweight session user
  const persistSession = (jwt: string, username: string) => {
    const sessionUser: UserResponse = { userName: username };
    setToken(jwt);
    setUser(sessionUser);
    localStorage.setItem("token", jwt);
    localStorage.setItem("user", JSON.stringify(sessionUser));
  };

  // Log in using the userService and store the returned JWT
  const login = async (username: string, password: string) => {
    try {
      setIsLoading(true);
      const auth = await loginService(username, password);
      persistSession(auth.token, auth.username);
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  // Register a new trainer and log them in automatically
  const register = async (userData: CreateUser) => {
    try {
      setIsLoading(true);
      const auth = await createUser(userData);
      persistSession(auth.token, auth.username);
    } catch (error) {
      console.error("Register failed:", error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  // Clear user session from state and localStorage
  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem("user");
    localStorage.removeItem("token");
  };

  // On app load, restore the session from localStorage if it exists
  useEffect(() => {
    try {
      const savedToken = localStorage.getItem("token");
      const savedUser = localStorage.getItem("user");
      if (savedToken) setToken(savedToken);
      if (savedUser) setUser(JSON.parse(savedUser));
    } catch (error) {
      console.error("Failed to load session from storage:", error);
      localStorage.removeItem("user");
      localStorage.removeItem("token");
    } finally {
      setIsLoading(false);
    }
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: Boolean(token),
        isLoading,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

// Custom hook to access auth context — must be used inside AuthProvider
export function useAuth() {
  const context = useContext(AuthContext);

  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }

  return context;
}
