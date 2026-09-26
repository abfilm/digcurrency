import { useEffect, useState } from 'react';
import { currencyApi } from './api/client';
import type { Currency } from './api/types';
import { Converter } from './components/Converter';
import { CurrencyTable } from './components/CurrencyTable';

const REFRESH_MS = 60_000;

export default function App() {
  const [currencies, setCurrencies] = useState<Currency[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [from, setFrom] = useState('');

  useEffect(() => {
    function load() {
      currencyApi
        .list()
        .then((list) => {
          setCurrencies(list);
          setError(null);
          setFrom((current) => current || (list[0]?.code ?? ''));
        })
        .catch((e: Error) => setError(e.message));
    }

    load();
    // The backend refreshes live rates periodically; pick them up without a page reload.
    const timer = setInterval(load, REFRESH_MS);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="app">
      <header>
        <h1>DigCurrency</h1>
        <p className="muted">Fiat, crypto and central-bank digital currencies in one place.</p>
      </header>

      {error && <p className="error" role="alert">Could not load currencies: {error}</p>}
      {!currencies && !error && <p className="muted">Loading…</p>}

      {currencies && (
        <main>
          <section>
            <h2>Convert</h2>
            <Converter currencies={currencies} from={from} onFromChange={setFrom} />
          </section>
          <section>
            <h2>Currencies</h2>
            <CurrencyTable currencies={currencies} selectedCode={from} onSelect={setFrom} />
          </section>
        </main>
      )}
    </div>
  );
}
