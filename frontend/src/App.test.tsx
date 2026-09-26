import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';
import App from './App';
import type { Currency } from './api/types';

const currencies: Currency[] = [
  { code: 'BTC', name: 'Bitcoin', type: 'CRYPTO', usdRate: 65000, updatedAt: '2026-01-01T00:00:00Z' },
  { code: 'ETH', name: 'Ether', type: 'CRYPTO', usdRate: 3200, updatedAt: '2026-01-01T00:00:00Z' },
  { code: 'EUR', name: 'Euro', type: 'FIAT', usdRate: 1.08, updatedAt: '2026-01-01T00:00:00Z' },
];

describe('App', () => {
  it('sets the converter "From" currency when a table row is clicked', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify(currencies), { status: 200, headers: { 'Content-Type': 'application/json' } }),
    );
    render(<App />);

    const fromSelect = await screen.findByLabelText('From');
    expect(fromSelect).toHaveValue('BTC');

    await userEvent.click(screen.getByText('Ether'));

    expect(fromSelect).toHaveValue('ETH');
    expect(screen.getByText('Ether').closest('tr')).toHaveAttribute('aria-selected', 'true');
  });
});
