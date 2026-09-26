import type { Currency } from '../api/types';

interface Props {
  currencies: Currency[];
}

const usd = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 6 });

export function CurrencyTable({ currencies }: Props) {
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
          <tr key={c.code}>
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
