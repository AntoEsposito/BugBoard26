# BugBoard26
Repo per il progetto di Ingegneria del Software

### Prerequisiti
- **Docker** installato e avviato
- **Java 25** (JDK)
- **Git**

### 1. Avvio

```bash
docker compose up -d --build
```

auth-service → porta **8080**, core-service → porta **8081**, database → porta interna 5432 (host: 5433)

---

### 2. Utenti di default

All'avvio il DataSeeder crea automaticamente due utenti se non esistono:

| Ruolo | Email | Password |
|---|---|---|
| Admin | `admin@bugboard26.com` | `admin` |
| Utente | `utente@bugboard26.com` | `utente` |

---

### 3. Verifica — Login

```bash
# Admin
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"email":"admin@bugboard26.com","password":"admin"}'

# Utente base
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"email":"utente@bugboard26.com","password":"utente"}'
```

La risposta contiene il token JWT da usare come `Authorization: Bearer <token>` nelle chiamate successive.
