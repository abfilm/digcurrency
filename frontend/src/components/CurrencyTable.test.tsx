import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';
import { CurrencyTable } from './CurrencyTable';
import type { Currency } from '../api/types';

const btc: Currency = {
  code: 'BTC',
  name: 'Bitcoin',
  type: 'CRYPTO',
  usdRate: 65000,
  updatedAt: '2026-01-01T00:00:00Z',
};

const eth: Currency = { ...btc, code: 'ETH', name: 'Ether', usdRate: 3200 };

describe('CurrencyTable', () => {
  it('renders a row per currency', () => {
    render(<CurrencyTable currencies={[btc]} />);

    expect(screen.getByText('BTC')).toBeInTheDocument();
    expect(screen.getByText('Bitcoin')).toBeInTheDocument();
    expect(screen.getByText('$65,000.00')).toBeInTheDocument();
    expect(screen.getByRole('columnheader', { name: 'Updated' })).toBeInTheDocument();
    expect(screen.getByText('Bitcoin').closest('tr')!.querySelector('time')).toHaveAttribute(
      'dateTime',
      '2026-01-01T00:00:00Z',
    );
  });

  it('shows an empty state', () => {
    render(<CurrencyTable currencies={[]} />);

    expect(screen.getByText('No currencies yet.')).toBeInTheDocument();
  });

  it('selects a currency when its row is clicked', async () => {
    const onSelect = vi.fn();
    render(<CurrencyTable currencies={[btc, eth]} onSelect={onSelect} />);

    await userEvent.click(screen.getByText('Ether'));

    expect(onSelect).toHaveBeenCalledWith('ETH');
  });

  it('selects a currency with the keyboard', async () => {
    const onSelect = vi.fn();
    render(<CurrencyTable currencies={[btc, eth]} onSelect={onSelect} />);

    screen.getByText('Bitcoin').closest('tr')!.focus();
    await userEvent.keyboard('{Enter}');

    expect(onSelect).toHaveBeenCalledWith('BTC');
  });

  it('marks the selected row', () => {
    render(<CurrencyTable currencies={[btc, eth]} selectedCode="ETH" onSelect={vi.fn()} />);

    expect(screen.getByText('Ether').closest('tr')).toHaveAttribute('aria-selected', 'true');
    expect(screen.getByText('Bitcoin').closest('tr')).toHaveAttribute('aria-selected', 'false');
  });
});
