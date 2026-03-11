# BugBoard26

Piattaforma web per il tracciamento di issue software — progetto universitario di Ingegneria del Software.

---

## Architettura

| Container | Tecnologia | Porta | Responsabilità |
|---|---|---|---|
| **frontend** | Angular 21 + Node 22 Alpine | 4200 | SPA, proxy verso i backend |
| **auth-service** | Spring Boot 4 / Java 25 | 8080 | Login, registrazione, JWT |
| **core-service** | Spring Boot 4 / Java 25 | 8081 | Progetti, issue, commenti, immagini |
| **database** | PostgreSQL 15 Alpine | 5433 (host) | Persistenza dati |

```
Browser → HTTP → Angular (proxy) → Spring Boot → PostgreSQL
```

---

## Prerequisiti

- **Docker** e **Docker Compose** installati e avviati
- **Java 25** (JDK) — solo per sviluppo locale senza Docker
- **Git**

---

## Avvio

```bash
git clone <repo-url>
cd BugBoard26
docker compose up -d --build
```

L'applicazione sarà disponibile su `http://localhost:4200`.

---

## Utenti di default

All'avvio il `DataSeeder` crea automaticamente due utenti se non esistono:

| Ruolo | Email | Password |
|---|---|---|
| Admin | `admin@bugboard26.com` | `admin` |
| Utente | `utente@bugboard26.com` | `utente` |

Le credenziali sono configurabili tramite variabili nel file `.env`.

---

## API Endpoints

### Auth Service (`/api/auth`) — porta 8080

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| POST | `/api/auth/login` | Pubblico | Login, restituisce token JWT |
| POST | `/api/auth/utenti` | Solo ADMIN | Creazione nuovo utente |

### Core Service (`/api`) — porta 8081

**Progetti**

| Metodo | Endpoint | Descrizione |
|---|---|---|
| GET | `/api/progetti` | Lista progetti dell'utente autenticato |
| GET | `/api/progetti/{id}/membri` | Membri di un progetto |
| POST | `/api/progetti/{id}/membri` | Aggiunta membri |
| DELETE | `/api/progetti/{id}/membri` | Rimozione membri |

**Issue**

| Metodo | Endpoint | Descrizione |
|---|---|---|
| GET | `/api/issue?idProgetto={id}` | Lista issue di un progetto |
| POST | `/api/issue` | Creazione issue (multipart: dati + immagine) |
| GET | `/api/issue/{id}` | Dettaglio issue |
| PUT | `/api/issue/{id}` | Modifica issue (multipart: dati + immagine) |

**Commenti**

| Metodo | Endpoint | Descrizione |
|---|---|---|
| POST | `/api/issue/{idIssue}/commenti` | Aggiunta commento a un'issue |

**Immagini**

| Path | Descrizione |
|---|---|
| GET `/api/uploads/**` | Serve immagini caricate (no autenticazione) |

---

## Autenticazione

Tutte le chiamate (eccetto login e `/api/uploads/**`) richiedono l'header:

```
Authorization: Bearer <token>
```

Il token JWT viene restituito dall'endpoint di login e ha una validità di 24 ore.

### Verifica rapida

```bash
# Login admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@bugboard26.com","password":"admin"}'
```

---

## Stack tecnologico

| Layer | Tecnologia |
|---|---|
| Frontend | Angular 21, TypeScript 5.9, RxJS 7.8 |
| Backend | Spring Boot 4.0, Java 25, Maven 3.9 |
| Database | PostgreSQL 15 |
| Auth | JWT (jjwt 0.12.6) |
| Container | Docker, Docker Compose, Alpine Linux |
| Testing | Vitest 4.0, jsdom 27.1 |
