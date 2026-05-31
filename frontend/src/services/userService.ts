import { apiRequest } from "./api";
import type { AuthResponse, CreateUser, UserResponse } from "../types/index.ts";

// Get all trainers (admin use)
export async function getAllUsers(): Promise<UserResponse[]> {
  return apiRequest<UserResponse[]>("GET", `/users`);
}

// Get a single trainer by ID
export async function getUserById(id: number): Promise<UserResponse> {
  return apiRequest<UserResponse>("GET", `/users/${id}`);
}

// Log in with username and password — returns a JWT token
export async function login(
  username: string,
  password: string
): Promise<AuthResponse> {
  return apiRequest<AuthResponse>("POST", `/auth/login`, { username, password });
}

// Register a new trainer — returns a JWT token (mirrors backend TrainerInputDto)
export async function createUser(userData: CreateUser): Promise<AuthResponse> {
  return apiRequest<AuthResponse>("POST", `/auth/register`, userData);
}

// Update an existing trainer's data
export async function updateUser(
  id: number,
  userData: CreateUser
): Promise<UserResponse> {
  return apiRequest<UserResponse>("PUT", `/users/${id}`, userData);
}

// Delete a trainer account
export async function deleteUser(id: number): Promise<void> {
  return apiRequest<void>("DELETE", `/users/${id}`);
}
