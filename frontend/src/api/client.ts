// Typed API client. All calls go same-origin (/api) so the HttpOnly session
// cookie is sent automatically. CSRF: read XSRF-TOKEN cookie, send X-XSRF-TOKEN.
export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: { code: string; message: string } | null;
  timestamp: string;
}

function getCsrfToken(): string | null {
  const m = document.cookie.match(/(?:^|; )XSRF-TOKEN=([^;]*)/);
  return m ? decodeURIComponent(m[1]) : null;
}

export async function api<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (init.headers) Object.assign(headers, init.headers);
  const csrf = getCsrfToken();
  if (csrf && init.method && init.method !== 'GET') headers['X-XSRF-TOKEN'] = csrf;
  const res = await fetch(path, { ...init, headers, credentials: 'include' });
  if (res.status === 204) return null as T;
  const body = (await res.json().catch(() => null)) as ApiResponse<T> | null;
  if (!res.ok || !body?.success) {
    throw new Error(body?.error?.message ?? `Request failed (${res.status})`);
  }
  return body.data as T;
}

export interface Warehouse { id: string; code: string; name: string; city: string; state: string; country: string; timezone: string; active: boolean; }
export interface DashboardOverview { totalWarehouses: number; totalProducts: number; totalInventoryUnits: number; totalInventoryRecords: number; dataSource: string; }
export interface User { id: string; email: string; fullName: string; role: string; }

export const fetchWarehouses = () => api<Warehouse[]>('/api/warehouses');
export const fetchOverview = () => api<DashboardOverview>('/api/dashboard/overview');
export const fetchMe = () => api<User>('/api/auth/me');
export const login = (email: string, password: string) =>
  api<User>('/api/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) });
export const logout = () => api<null>('/api/auth/logout', { method: 'POST' });
