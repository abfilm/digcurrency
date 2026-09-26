import { vi } from 'vitest';
import { ApiError, conversionApi, currencyApi } from './client';

function mockFetch(status: number, body?: unknown) {
  return vi.spyOn(globalThis, 'fetch').mockResolvedValue(
    new Response(body === undefined ? null : JSON.stringify(body), {
      status,
      headers: { 'Content-Type': 'application/json' },
    }),
  );
}

describe('api client', () => {
  it('builds the conversion query string', async () => {
    const fetchSpy = mockFetch(200, { from: 'BTC', to: 'EUR', amount: 1, rate: 2, result: 2 });

    await conversionApi.convert('BTC', 'EUR', 1);

    expect(fetchSpy).toHaveBeenCalledWith('/api/v1/conversions?from=BTC&to=EUR&amount=1', expect.anything());
  });

  it('throws ApiError carrying the problem detail', async () => {
    mockFetch(404, { title: 'Resource not found', status: 404, detail: 'Currency XXX not found' });

    const error = await currencyApi.get('XXX').catch((e: unknown) => e);

    expect(error).toBeInstanceOf(ApiError);
    expect((error as ApiError).status).toBe(404);
    expect((error as ApiError).message).toBe('Currency XXX not found');
  });

  it('returns undefined for 204 responses', async () => {
    mockFetch(204);

    await expect(currencyApi.remove('BTC')).resolves.toBeUndefined();
  });
});
