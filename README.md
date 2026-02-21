# BugBoard26
Repo per il progetto di Ingegneria del Software

### Prerequisiti
- **Docker** installato e avviato
- **Java 25** (JDK)
- **Git**

### 1. AUTH SERVICE CONTAINERIZZATO

```Docker compose up -d per far partire database e auth service
```

### 2. Verifica che funziona

```powershell invia una richiesta http per simulare un login
curl -X POST http://localhost:8081/api/auth/login  -H "Content-Type: application/json"  -d '{"email":"admin@bugboard26.com","password":"admin"}'    
```

dovresti ricevere il token jwt con tutte le info del caso
