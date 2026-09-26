import { render, screen } from '@testing-library/react';
import { CurrencyTable } from './CurrencyTable';
import type { Currency } from '../api/types';

const btc: Currency = {
  code: 'BTC',
  name: 'Bitcoin',
  type: 'CRYPTO',
  usdRate: 65000,
  updatedAt: '2026-01-01T00:00:00Z',
};

describe('CurrencyTable', () => {
  it('renders a row per currency', () => {
    render(<CurrencyTable currencies={[btc]} />);

    expect(screen.getByText('BTC')).toBeInTheDocument();
    expect(screen.getByText('Bitcoin')).toBeInTheDocument();
    expect(screen.getByText('$65,000.00')).toBeInTheDocument();
  });

  it('shows an empty state', () => {
    render(<CurrencyTable currencies={[]} />);

    expect(screen.getByText('No currencies yet.')).toBeInTheDocument();
  });
});
