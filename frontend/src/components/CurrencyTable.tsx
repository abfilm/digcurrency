import type { Currency } from '../api/types';

interface Props {
  currencies: Currency[];
  /** Code of the row to highlight, e.g. the converter's "From" currency. */
  selectedCode?: string;
  /** Called with the currency code when a row is clicked or activated with Enter/Space. */
  onSelect?: (code: string) => void;
}

const usd = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 6 });

export function CurrencyTable({ currencies, selectedCode, onSelect }: Props) {
  if (currencies.length === 0) {
    return <p className="muted">No currencies yet.</p>;
  }

  return (
    <table className="currency-table">
      <thead>
        <tr>
          <th>Code</th>
          <th>Name</th>
          <th>Type</th>
          <th className="num">Value in USD</th>
        </tr>
      </thead>
      <tbody>
        {currencies.map((c) => (
          <tr
            key={c.code}
            className={[onSelect && 'selectable', c.code === selectedCode && 'selected'].filter(Boolean).join(' ') || undefined}
            aria-selected={onSelect ? c.code === selectedCode : undefined}
            tabIndex={onSelect ? 0 : undefined}
            title={onSelect ? `Convert from ${c.code}` : undefined}
            onClick={onSelect && (() => onSelect(c.code))}
            onKeyDown={
              onSelect &&
              ((e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault();
                  onSelect(c.code);
                }
              })
            }
          >
            <td className="code">{c.code}</td>
            <td>{c.name}</td>
            <td>
              <span className={`badge badge-${c.type.toLowerCase()}`}>{c.type}</span>
            </td>
            <td className="num">{usd.format(c.usdRate)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
