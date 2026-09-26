import { useEffect, useState } from 'react';
import { currencyApi } from './api/client';
import type { Currency } from './api/types';
import { Converter } from './components/Converter';
import { CurrencyTable } from './components/CurrencyTable';

export default function App() {
  const [currencies, setCurrencies] = useState<Currency[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    currencyApi
      .list()
      .then(setCurrencies)
      .catch((e: Error) => setError(e.message));
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
            <Converter currencies={currencies} />
          </section>
          <section>
            <h2>Currencies</h2>
            <CurrencyTable currencies={currencies} />
          </section>
        </main>
      )}
    </div>
  );
}
