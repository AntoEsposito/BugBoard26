# BugBoard26

Piattaforma web per il tracciamento di issue software. Progetto universitario di Ingegneria del Software, pensato come prototipo deployabile su una singola istanza AWS EC2 Free Tier.

## Panoramica

BugBoard26 consente a team di progetto di aprire, commentare e gestire issue (bug, feature, question, documentation) associate a uno o più progetti. L'applicazione è composta da una SPA Angular e due microservizi Spring Boot che condividono un unico database PostgreSQL.

## Stack tecnologico

- **Frontend**: Angular 21, servito da Nginx Alpine che funge anche da reverse proxy verso i backend
- **auth-service**: Spring Boot 4.0.5 su Java 25 — registrazione, login, emissione JWT
- **core-service**: Spring Boot 4.0.5 su Java 25 — progetti, issue, commenti, upload immagini
- **Database**: PostgreSQL 15 (alpine), esposto solo sulla rete interna Docker
- **Orchestrazione**: Docker Compose

## Architettura

```
  browser
     |
     v
+-----------+      +--------------+      +---------------+
| frontend  | ---> | auth-service | ---> |               |
| (Nginx +  |      | :8080        |      |  PostgreSQL   |
|  Angular) |      +--------------+      |               |
|  :80      |      | core-service | ---> |               |
+-----------+ ---> | :8081        |      +---------------+
                   +--------------+
```

Nginx espone la porta 80 e instrada `/api/auth/**` verso auth-service e `/api/**` verso core-service. L'autenticazione è stateless: il client include `Authorization: Bearer <JWT>` su ogni richiesta protetta. Le immagini caricate vengono scritte su un volume Docker condiviso; il database memorizza solo il path.

## Requisiti

- Docker 24+ e Docker Compose v2
- Per lo sviluppo locale: Node.js 20+, JDK 25, Maven 3.9+

## Configurazione

Creare un file `.env` nella root del progetto con le seguenti variabili:

```env
POSTGRES_DB=bugboard26
POSTGRES_USER=bugboard
POSTGRES_PASSWORD=change-me

SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/bugboard26

JWT_SECRET=sostituire-con-una-chiave-lunga-almeno-32-byte
JWT_EXPIRATION=86400000

ADMIN_EMAIL=admin@bugboard26.com
ADMIN_PASSWORD=admin
ADMIN_NOME=Admin
ADMIN_COGNOME=BugBoard

UTENTE_EMAIL=utente@bugboard26.com
UTENTE_PASSWORD=utente
UTENTE_NOME=Mario
UTENTE_COGNOME=Rossi
```

I valori di `ADMIN_*` e `UTENTE_*` vengono usati dal `DataSeeder` di auth-service per creare gli utenti iniziali al primo avvio.

## Avvio

```bash
docker compose up --build
```

Una volta pronti i container, l'applicazione è raggiungibile su [http://localhost](http://localhost).

Per arrestare e rimuovere i container mantenendo i volumi:

```bash
docker compose down
```

Per un reset completo (cancella database e immagini caricate):

```bash
docker compose down -v
```

## Utenti predefiniti

| Ruolo         | Email                      | Password |
|---------------|----------------------------|----------|
| ROLE_ADMIN    | admin@bugboard26.com       | admin    |
| ROLE_UTENTE   | utente@bugboard26.com      | utente   |

Al primo avvio vengono creati anche tre progetti demo: *BugBoard26*, *Walrider*, *Pegasus*.

## Porte esposte

| Servizio      | Porta host | Note                                   |
|---------------|------------|----------------------------------------|
| frontend      | 80         | Punto di ingresso pubblico             |
| auth-service  | 8080       | Utile per chiamate dirette in debug    |
| core-service  | 8081       | Utile per chiamate dirette in debug    |
| database      | 5433       | Mappato per accesso da host (dev only) |

In produzione le porte 8080, 8081 e 5433 possono essere rimosse dal `docker-compose.yml`: il frontend raggiunge i backend attraverso la rete interna Docker.

## Sviluppo locale

### Backend

```bash
cd backend/auth-service
./mvnw spring-boot:run

cd backend/core-service
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm ci --ignore-scripts
npm start
```

Il dev server Angular usa `proxy.conf.json` per inoltrare le chiamate API ai servizi in esecuzione locale.

## Testing

I test di unit dei servizi backend si eseguono con:

```bash
./mvnw test
```

La suite copre i casi d'uso principali di `IssueService` e `CommentoService` (black-box, partizionamento di equivalenza, WECT).

## Qualità del codice

Il progetto è integrato con SonarCloud. L'analisi è configurata in `sonar-project.properties` ed eseguita in CI.

## Struttura del repository

```
BugBoard26/
├── backend/
│   ├── auth-service/     Spring Boot — autenticazione e JWT
│   └── core-service/     Spring Boot — dominio applicativo
├── frontend/             Angular 21 + Nginx
├── docker-compose.yml
├── sonar-project.properties
└── README.md
```

## Licenza

Vedere il file [LICENSE](LICENSE).
