export const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api").replace(/\/+$/, "");

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

export async function apiRequest<T>(
  method: HttpMethod,
  endpoint: string,
  body?: unknown,
  signal?: AbortSignal,
  token?: string,
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;
  const headers = new Headers();

  const options: RequestInit = {
    method,
    signal,
    headers,
  };

  if (token) headers.set("Authorization", `Bearer ${token}`);
  if (body !== undefined) {
    headers.set("Content-Type", "application/json");
    options.body = JSON.stringify(body);
  }

  const response = await fetch(url, options);

  if (!response.ok) {
    const error = await response.json().catch(() => null) as { message?: unknown } | null;
    const message = typeof error?.message === "string" && error.message
      ? error.message
      : `Error ${response.status}: ${response.statusText || "Request failed"}`;
    throw new ApiError(response.status, message);
  }

  // Parse and return the typed JSON
  return (await response.json()) as T;
}
