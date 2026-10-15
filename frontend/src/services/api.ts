export const API_BASE_URL = "http://localhost:8080/api";

type HttpMethod = "GET" | "POST" | "PUT" | "DELETE";

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, statusText: string) {
    super(`Error ${status}: ${statusText}`);
    this.name = "ApiError";
    this.status = status;
  }
}

export async function apiRequest<T>(
  method: HttpMethod,
  endpoint: string,
  body?: unknown,
  signal?: AbortSignal,
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;

  const options: RequestInit = {
    method,
    signal,
    headers: {
      "Content-Type": "application/json",
    },
  };

  // The body is only added if it exists
  if (body) {
    options.body = JSON.stringify(body);
  }

  const response = await fetch(url, options);

  // If the response is not OK (codes 200-299), throw an error
  if (!response.ok) {
    throw new ApiError(response.status, response.statusText);
  }

  // Parse and return the typed JSON
  return (await response.json()) as T;
}
