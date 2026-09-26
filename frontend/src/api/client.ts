import type { ConversionResult, Currency, CurrencyRequest, CurrencyType, ProblemDetail } from './types';

const BASE_URL = '/api/v1';

export class ApiError extends Error {
  constructor(
    readonly status: number,
    readonly problem: ProblemDetail,
  ) {
    super(problem.detail ?? problem.title ?? `Request failed with status ${status}`);
    this.name = 'ApiError';
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${BASE_URL}${path}`, {
    ...init,
    headers: { 'Content-Type': 'application/json', ...init?.headers },
  });

  if (!response.ok) {
    const problem: ProblemDetail = await response.json().catch(() => ({}));
    throw new ApiError(response.status, problem);
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return response.json() as Promise<T>;
}

export const currencyApi = {
  list: (type?: CurrencyType) =>
    request<Currency[]>(`/currencies${type ? `?type=${type}` : ''}`),
  get: (code: string) => request<Currency>(`/currencies/${encodeURIComponent(code)}`),
  create: (body: CurrencyRequest) =>
    request<Currency>('/currencies', { method: 'POST', body: JSON.stringify(body) }),
  update: (code: string, body: CurrencyRequest) =>
    request<Currency>(`/currencies/${encodeURIComponent(code)}`, {
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  remove: (code: string) =>
    request<void>(`/currencies/${encodeURIComponent(code)}`, { method: 'DELETE' }),
};

export const conversionApi = {
  convert: (from: string, to: string, amount: number) => {
    const params = new URLSearchParams({ from, to, amount: String(amount) });
    return request<ConversionResult>(`/conversions?${params}`);
  },
};
