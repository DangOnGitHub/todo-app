import type { components } from './schema';

type CamelCase<S extends string> = S extends `${infer Head}_${infer Tail}`
  ? `${Head}${Capitalize<CamelCase<Tail>>}`
  : S;

type CamelCaseKeys<T> = T extends (infer U)[]
  ? CamelCaseKeys<U>[]
  : T extends object
    ? { [K in keyof T as CamelCase<string & K>]: CamelCaseKeys<T[K]> }
    : T;

export type ApiSchema<T extends keyof components['schemas']> = CamelCaseKeys<
  components['schemas'][T]
>;

function toCamelCase(s: string): string {
  return s.replace(/_([a-z])/g, (_, c: string) => c.toUpperCase());
}

function camelCaseKeys<T>(value: unknown): T {
  if (Array.isArray(value)) return value.map(camelCaseKeys) as T;
  if (value !== null && typeof value === 'object') {
    return Object.fromEntries(
      Object.entries(value).map(([k, v]) => [toCamelCase(k), camelCaseKeys(v)])
    ) as T;
  }
  return value as T;
}

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('token');
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const response = await fetch(`${BASE_URL}${path}`, { ...init, headers });
  if (!response.ok) throw camelCaseKeys(await response.json());
  if (response.status === 204) return undefined as T;
  return camelCaseKeys(await response.json());
}

export const api = {
  post: <T>(path: string, body: unknown) =>
    request<T>(path, { method: 'POST', body: JSON.stringify(body) }),
  get: <T>(path: string) => request<T>(path),
  patch: <T>(path: string, body: unknown) =>
    request<T>(path, { method: 'PATCH', body: JSON.stringify(body) }),
  delete: (path: string) => request<void>(path, { method: 'DELETE' }),
};
