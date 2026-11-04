# StockExchangeSystem

<img width="4096" height="1125" alt="image" src="https://github.com/user-attachments/assets/a560ff98-c67e-4f78-a222-c5d1187b2abb" />

## Features
- Stock price and market index events
- Observer-based event notifications
- Portfolio management using Composite
- Financial calculations using Visitor
- Risk analysis and JSON export
- Concurrent market simulation using `ExecutorService`

## Design Patterns

| Pattern | Implementation |
|---|---|
| Observer | Market events and listeners |
| Composite | Portfolio → Account → Assets |
| Visitor | Value, risk, tax, filtering, JSON export |

## Project Structure

- `observer` – Observer interfaces and event publisher.
- `events` – Market event classes.
- `feeds` – Stock and index data sources.
- `model` – Portfolio and financial assets.
- `visitor` – Financial calculations and JSON export.
- `listeners` – Market event listeners.
- `service` – Market simulation.
