# BugBoard26
Repo per il progetto di Ingegneria del Software

dopo un intero pomeriggio il database parte e auth service si collega, istruzioni per controllare se parte tutto:

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

### 2. Compila e avvia l'auth-service

```powershell
cd backend\auth-service
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

### 3. Verifica che funziona

- Il server deve partire sulla **porta 8081** senza errori di connessione al DB
- Nei log devi vedere che Hibernate si connette (nessun errore `Connection refused`)
- Testando `http://localhost:8081` devi ricevere un **401 Unauthorized** (Spring Security attivo)

```powershell
# Da un altro terminale:
Invoke-WebRequest -Uri http://localhost:8081 -UseBasicParsing
# Deve dare errore 401 → significa che il server è up e Security è attivo
```


> **Se qualcosa non va**, controlla che la porta 5433 non sia già occupata e che Docker sia avviato.
