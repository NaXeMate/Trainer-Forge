// Base URL for all API requests — points to the Spring Boot backend
export const API_BASE_URL = "http://localhost:8080/api";

type HttpMethod = "GET" | "POST" | "PUT" | "DELETE";

// Generic function to make API requests and return typed responses
export async function apiRequest<T>(
  method: HttpMethod,
  endpoint: string,
  body?: unknown
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };

  // Attach the JWT (if the user is logged in) so protected endpoints accept the request
  const token = localStorage.getItem("token");
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const options: RequestInit = {
    method,
    headers,
  };

  // Only include body for POST/PUT requests
  if (body) {
    options.body = JSON.stringify(body);
  }

  const response = await fetch(url, options);

  // Throw an error for non-2xx responses
  if (!response.ok) {
    throw new Error(`Error ${response.status}: ${response.statusText}`);
  }

  return (await response.json()) as T;
}
