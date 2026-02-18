# BugBoard26
Repo per il progetto di Ingegneria del Software

### Prerequisiti
- **Docker** installato e avviato
- **Java 25** (JDK)
- **Git**

### 1. Avvia il database PostgreSQL con Docker

```powershell
cd BugBoard26
docker compose up -d
```

Verifica che il container sia attivo:

```powershell
docker ps
# Deve apparire bugboard26-db sulla porta 5433
```

### 2. Compila e avvia

**Auth Service:**

```powershell
cd backend\auth-service
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

### 3. Verifica che funziona

- Auth Service sulla porta **8081**, devono uscire nel terminale i vari log dei servizi che partono

```powershell invia una richiesta http per simulare un login
curl -X POST http://localhost:8081/api/auth/login  -H "Content-Type: application/json"  -d '{"email":"admin@bugboard26.com","password":"admin"}'    
```

dovresti ricevere il token jwt con tutte le info del caso
