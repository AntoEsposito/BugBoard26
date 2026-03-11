# BugBoard26

Piattaforma web per il tracciamento di issue software — progetto universitario di Ingegneria del Software.

## Prerequisiti

- **Docker** e **Docker Compose** installati e avviati

## Avvio

```bash
git clone <repo-url>
cd BugBoard26
docker compose up -d --build
```

L'applicazione sarà disponibile su `http://localhost:4200`.

## Utenti di default

All'avvio il `DataSeeder` crea automaticamente due utenti se non esistono:

| Ruolo | Email | Password |
|---|---|---|
| Admin | `admin@bugboard26.com` | `admin` |
| Utente | `utente@bugboard26.com` | `utente` |
