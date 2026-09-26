// Mirrors the backend DTOs in com.digcurrency.* — keep in sync with the API.

export type CurrencyType = 'FIAT' | 'CRYPTO' | 'CBDC';

export interface Currency {
  code: string;
  name: string;
  type: CurrencyType;
  usdRate: number;
  updatedAt: string;
}

export interface CurrencyRequest {
  code: string;
  name: string;
  type: CurrencyType;
  usdRate: number;
}

export interface ConversionResult {
  from: string;
  to: string;
  amount: number;
  rate: number;
  result: number;
}

/** RFC 9457 problem detail, as returned by the backend on errors. */
export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
}
