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

### 2. Compila e avvia i servizi (un terminale per ciascuno)

**Auth Service:**

```powershell
cd backend\auth-service
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

**Core Service:**

```powershell
cd backend\core-service
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

### 3. Verifica che funziona

- Auth Service sulla porta **8081**, Core Service sulla porta **8082**
- Nei log devi vedere `HikariPool-1 - Start completed.` (connessione al DB riuscita)

```powershell
# Auth Service: deve dare errore 401 (Security attivo)
Invoke-WebRequest -Uri http://localhost:8081 -UseBasicParsing

# Core Service: deve dare errore 404 (nessun controller ancora)
Invoke-WebRequest -Uri http://localhost:8082 -UseBasicParsing
```

> **Se qualcosa non va**, controlla che la porta 5433 non sia già occupata e che Docker sia avviato.