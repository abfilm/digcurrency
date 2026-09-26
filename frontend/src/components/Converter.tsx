import { useState, type FormEvent } from 'react';
import { conversionApi } from '../api/client';
import type { ConversionResult, Currency } from '../api/types';

interface Props {
  currencies: Currency[];
  from: string;
  onFromChange: (code: string) => void;
}

export function Converter({ currencies, from, onFromChange }: Props) {
  const [to, setTo] = useState(currencies[1]?.code ?? currencies[0]?.code ?? '');
  const [amount, setAmount] = useState('1');
  const [result, setResult] = useState<ConversionResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    try {
      setResult(await conversionApi.convert(from, to, Number(amount)));
    } catch (e) {
      setResult(null);
      setError(e instanceof Error ? e.message : 'Conversion failed');
    }
  }

  return (
    <form className="converter" onSubmit={handleSubmit} aria-label="Converter">
      <label>
        Amount
        <input type="number" min="0" step="any" value={amount} onChange={(e) => setAmount(e.target.value)} />
      </label>
      <label>
        From
        <select value={from} onChange={(e) => onFromChange(e.target.value)}>
          {currencies.map((c) => (
            <option key={c.code} value={c.code}>{c.code}</option>
          ))}
        </select>
      </label>
      <label>
        To
        <select value={to} onChange={(e) => setTo(e.target.value)}>
          {currencies.map((c) => (
            <option key={c.code} value={c.code}>{c.code}</option>
          ))}
        </select>
      </label>
      <button type="submit">Convert</button>

      {result && (
        <p className="result" role="status">
          {result.amount} {result.from} = <strong>{result.result}</strong> {result.to}
        </p>
      )}
      {error && <p className="error" role="alert">{error}</p>}
    </form>
  );
}
