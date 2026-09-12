# MotoRoute

MotoRoute è un'applicazione web full stack per creare, consultare, modificare e gestire itinerari motociclistici.

Il progetto viene sviluppato per milestone e user story verticali, mantenendo separati backend e frontend ma facendo
evolvere ogni funzionalità end-to-end: database, dominio, API REST, documentazione OpenAPI, interfaccia React e test
automatici.

## Stato del progetto

### Release

**Versione:** `v0.1.0`

La release `v0.1.0` rappresenta il completamento della **Milestone M1 — CRUD itinerari**.

### Milestone completate

- **M0 — Fondamenta del progetto**
- **M1 — CRUD itinerari**

### User story completate

- **US-1.1 — Creazione itinerario**
- **US-1.2 — Elenco itinerari**
- **US-1.3 — Dettaglio itinerario**
- **US-1.4 — Modifica itinerario**
- **US-1.5 — Eliminazione itinerario**

### Funzionalità disponibili

- configurazione separata di backend e frontend;
- database PostgreSQL eseguito tramite Docker Compose;
- gestione versionata dello schema tramite Flyway;
- validazione dello schema tramite Hibernate;
- test di integrazione con PostgreSQL reale tramite Testcontainers;
- gestione centralizzata e standardizzata degli errori API;
- documentazione OpenAPI e Swagger UI;
- creazione di un itinerario;
- elenco paginato e ordinabile degli itinerari;
- dettaglio di un singolo itinerario;
- modifica di un itinerario esistente;
- eliminazione di un itinerario con conferma esplicita;
- validazione backend e frontend;
- gestione frontend degli stati di caricamento, errore, not found, elenco vuoto e submit;
- navigazione tra elenco, creazione, dettaglio e modifica;
- test backend e frontend distribuiti sui livelli appropriati.

### Prossima milestone

La prossima attività prevista è la **M2 — Waypoint e composizione dell'itinerario**, a partire dalla **US-2.1 —
Creazione waypoint**.

---

## Stack tecnologico

### Backend

- Java 25
- Spring Boot 4
- Spring MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Maven
- JUnit 5
- Mockito
- AssertJ
- MockMvc
- Testcontainers
- Springdoc OpenAPI

### Frontend

- JavaScript
- React
- JSX
- Vite
- React Router
- Fetch API
- Vitest
- Testing Library
- `user-event`
- Mock Service Worker

### Infrastruttura e workflow

- Docker
- Docker Compose
- Git
- GitHub
- branch `main`, `develop` e branch di lavoro dedicati

---

## Architettura

Flusso principale lato backend:

```text
Browser / React
      |
      | HTTP JSON
      v
RouteController
      |
      v
RouteService
      |
      +----> RouteMapper
      |
      v
RouteRepository
      |
      v
PostgreSQL
```

Principi principali:

- il controller gestisce il confine HTTP e delega la logica applicativa;
- il service orchestra i casi d'uso ed è il confine transazionale;
- il dominio contiene invarianti e operazioni proprie della route;
- i DTO separano il contratto API dalle entity JPA;
- il mapper converte dominio e DTO;
- il repository gestisce la persistenza;
- Flyway è l'unico proprietario delle modifiche allo schema;
- il frontend separa client API, Page, componenti presentazionali, form e funzioni pure.

---

## Struttura del progetto

```text
MotoRoute/
├── backend/
├── frontend/
├── compose.yaml
├── .env.example
├── .gitignore
└── README.md
```

### Struttura backend principale

```text
backend/src/main/java/it/motoroute/
├── common/
│   └── api/
│       ├── ApiError.java
│       └── GlobalExceptionHandler.java
│
└── route/
    ├── api/
    │   ├── RouteController.java
    │   ├── CreateRouteRequest.java
    │   ├── UpdateRouteRequest.java
    │   ├── RouteResponse.java
    │   ├── RouteSummaryResponse.java
    │   └── RoutePageResponse.java
    │
    ├── application/
    │   ├── RouteService.java
    │   └── RouteMapper.java
    │
    ├── domain/
    │   ├── Route.java
    │   └── Difficulty.java
    │
    └── infrastructure/
        └── RouteRepository.java
```

`RouteNotFoundException` viene riutilizzata nei casi d'uso dettaglio, modifica ed eliminazione per produrre un
comportamento `404 Not Found` coerente.

### Struttura frontend principale

```text
frontend/src/
├── App.jsx
├── main.jsx
├── index.css
│
├── features/
│   └── routes/
│       ├── api/
│       │   ├── ApiRequestError.js
│       │   └── apiResponse.js
│       │
│       ├── create/
│       │   ├── CreateRoutePage.jsx
│       │   └── createRouteApi.js
│       │
│       ├── list/
│       │   ├── RoutesPage.jsx
│       │   ├── RouteCard.jsx
│       │   ├── RoutesPagination.jsx
│       │   └── listRoutesApi.js
│       │
│       ├── details/
│       │   ├── getRouteApi.js
│       │   ├── RouteDetails.jsx
│       │   └── RouteDetailsPage.jsx
│       │
│       ├── edit/
│       │   ├── updateRouteApi.js
│       │   └── RouteEditPage.jsx
│       │
│       ├── delete/
│       │   └── deleteRouteApi.js
│       │
│       ├── form/
│       │   ├── RouteForm.jsx
│       │   └── routeValidation.js
│       │
│       └── utils/
│           └── routeFormatters.js
│
└── test/
    ├── server.js
    └── setup.js
```

I relativi file di test sono mantenuti vicino al codice che verificano.

---

## Requisiti

Per eseguire il progetto in locale sono necessari:

- Java 25;
- Docker;
- Docker Compose;
- Node.js;
- npm.

---

## Avvio locale

### 1. Avvio del database

Dalla directory principale del progetto:

```bash
docker compose up -d
```

Per verificare che PostgreSQL sia in esecuzione:

```bash
docker compose ps
```

### 2. Avvio del backend

Aprire un terminale ed eseguire:

```bash
cd backend
./mvnw spring-boot:run
```

Il backend sarà disponibile all'indirizzo:

```text
http://localhost:8080
```

Durante l'avvio, Flyway applica automaticamente le migration mancanti e Hibernate valida lo schema esistente.

### 3. Avvio del frontend

Aprire un secondo terminale ed eseguire:

```bash
cd frontend
npm install
npm run dev
```

Vite mostrerà nel terminale l'indirizzo locale del frontend.

---

## Pagine frontend

### Elenco degli itinerari

```text
/routes
```

La pagina gestisce:

- stato di caricamento;
- elenco degli itinerari;
- stato vuoto;
- errori restituiti dall'API;
- paginazione;
- ordinamento definito dal client API;
- navigazione verso creazione e dettaglio.

### Creazione di un itinerario

```text
/routes/new
```

Permette di inserire:

- nome;
- descrizione opzionale;
- luogo di partenza;
- luogo di arrivo;
- distanza in chilometri;
- difficoltà.

Dopo la creazione viene aperta la pagina di dettaglio dell'itinerario appena creato.

### Dettaglio itinerario

```text
/routes/:routeId
```

Mostra i dati completi dell'itinerario e permette di:

- tornare all'elenco;
- aprire la pagina di modifica;
- eliminare l'itinerario previa conferma.

Una route inesistente viene gestita con uno stato dedicato di not found.

### Modifica itinerario

```text
/routes/:routeId/edit
```

Il form viene precaricato con i dati esistenti. Dopo un aggiornamento riuscito, l'utente torna al dettaglio aggiornato.

---

## API REST

Base path:

```text
/api/routes
```

### Endpoint disponibili

| Metodo   | Endpoint                | Descrizione            | Successo         |
|----------|-------------------------|------------------------|------------------|
| `POST`   | `/api/routes`           | Crea un itinerario     | `201 Created`    |
| `GET`    | `/api/routes`           | Elenca gli itinerari   | `200 OK`         |
| `GET`    | `/api/routes/{routeId}` | Recupera il dettaglio  | `200 OK`         |
| `PUT`    | `/api/routes/{routeId}` | Aggiorna un itinerario | `200 OK`         |
| `DELETE` | `/api/routes/{routeId}` | Elimina un itinerario  | `204 No Content` |

Gli endpoint che ricevono `routeId` usano un UUID nel path. Un UUID non valido produce `400 Bad Request`; una route
valida ma inesistente produce `404 Not Found` per dettaglio, modifica ed eliminazione.

### Creazione di un itinerario

```http
POST /api/routes
```

Esempio di richiesta:

```json
{
  "name": "Passo dello Stelvio",
  "description": "Percorso panoramico",
  "startLocation": "Bormio",
  "endLocation": "Prato allo Stelvio",
  "distanceKm": 47.50,
  "difficulty": "HARD"
}
```

In caso di successo:

```http
201 Created
Location: /api/routes/{id}
```

Esempio di risposta:

```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "name": "Passo dello Stelvio",
  "description": "Percorso panoramico",
  "startLocation": "Bormio",
  "endLocation": "Prato allo Stelvio",
  "distanceKm": 47.50,
  "difficulty": "HARD",
  "createdAt": "2026-07-29T10:00:00Z",
  "updatedAt": "2026-07-29T10:00:00Z"
}
```

### Elenco degli itinerari

```http
GET /api/routes
```

La richiesta supporta paginazione e ordinamento:

```http
GET /api/routes?page=0&size=20&sort=createdAt,desc
```

Parametri principali:

| Parametro | Descrizione                            | Valore predefinito |
|-----------|----------------------------------------|-------------------:|
| `page`    | Numero della pagina, a partire da zero |                `0` |
| `size`    | Numero di elementi per pagina          |               `20` |
| `sort`    | Campo e direzione di ordinamento       |   `createdAt,desc` |

La dimensione della pagina deve essere compresa tra `1` e `100`.

Campi ordinabili:

- `createdAt`;
- `name`;
- `startLocation`;
- `endLocation`;
- `distanceKm`;
- `difficulty`.

Direzioni ammesse:

- `asc`;
- `desc`.

Esempi:

```http
GET /api/routes?sort=name,asc
```

```http
GET /api/routes?page=1&size=5&sort=distanceKm,desc
```

Esempio di risposta:

```json
{
  "content": [
    {
      "id": "11111111-1111-1111-1111-111111111111",
      "name": "Passo dello Stelvio",
      "startLocation": "Bormio",
      "endLocation": "Prato allo Stelvio",
      "distanceKm": 47.50,
      "difficulty": "HARD",
      "createdAt": "2026-07-29T10:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

Quando non sono presenti itinerari, l'API restituisce comunque `200 OK` con `content` vuoto.

### Dettaglio itinerario

```http
GET /api/routes/{routeId}
```

Restituisce il `RouteResponse` completo.

Risposte principali:

- `200 OK` — itinerario trovato;
- `400 Bad Request` — UUID non valido;
- `404 Not Found` — itinerario inesistente;
- `500 Internal Server Error` — errore inatteso.

### Modifica itinerario

```http
PUT /api/routes/{routeId}
```

Il body usa gli stessi campi mutabili della creazione e rappresenta un aggiornamento completo della route.

Esempio:

```json
{
  "name": "Passo dello Stelvio aggiornato",
  "description": "Percorso panoramico aggiornato",
  "startLocation": "Bormio",
  "endLocation": "Prato allo Stelvio",
  "distanceKm": 48.20,
  "difficulty": "HARD"
}
```

Risposte principali:

- `200 OK` — itinerario aggiornato;
- `400 Bad Request` — dati o UUID non validi;
- `404 Not Found` — itinerario inesistente;
- `500 Internal Server Error` — errore inatteso.

### Eliminazione itinerario

```http
DELETE /api/routes/{routeId}
```

In caso di successo restituisce:

```http
204 No Content
```

Non viene restituito alcun body.

Risposte principali:

- `204 No Content` — itinerario eliminato;
- `400 Bad Request` — UUID non valido;
- `404 Not Found` — itinerario inesistente;
- `500 Internal Server Error` — errore inatteso.

---

## Modello Route

Campi principali:

| Campo           | Tipo logico     | Note                           |
|-----------------|-----------------|--------------------------------|
| `id`            | UUID            | identificatore univoco         |
| `name`          | stringa         | obbligatorio, max 120          |
| `description`   | stringa         | opzionale, max 2000            |
| `startLocation` | stringa         | obbligatorio, max 120          |
| `endLocation`   | stringa         | obbligatorio, max 120          |
| `distanceKm`    | numero decimale | obbligatorio e positivo        |
| `difficulty`    | enum            | `EASY`, `MEDIUM`, `HARD`       |
| `createdAt`     | timestamp       | data di creazione              |
| `updatedAt`     | timestamp       | data dell'ultimo aggiornamento |

Il dominio espone operazioni dedicate alla creazione e all'aggiornamento e mantiene le proprie invarianti
indipendentemente dalla validazione HTTP.

---

## Gestione degli errori API

Gli errori HTTP con body JSON usano una struttura comune:

```json
{
  "timestamp": "2026-09-12T08:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "One or more fields are invalid",
  "path": "/api/routes",
  "fieldErrors": {
    "name": "must not be blank"
  }
}
```

Campi:

- `timestamp`;
- `status`;
- `error`;
- `message`;
- `path`;
- `fieldErrors`.

Gli errori di validazione possono valorizzare `fieldErrors`. Per errori non legati a singoli campi, la mappa resta
vuota.

La traduzione delle eccezioni applicative verso i codici HTTP è centralizzata nel `GlobalExceptionHandler`.

---

## Documentazione OpenAPI

Con il backend in esecuzione:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`

La documentazione copre:

- `POST /api/routes`;
- `GET /api/routes`;
- `GET /api/routes/{routeId}`;
- `PUT /api/routes/{routeId}`;
- `DELETE /api/routes/{routeId}`;
- request e response principali;
- parametri path/query;
- paginazione e ordinamento;
- codici HTTP principali;
- struttura standard degli errori.

Il documento OpenAPI generato viene verificato anche tramite test di integrazione dedicati.

---

## Test backend

### Test unitari e web

```bash
cd backend
./mvnw test
```

Questo comando esegue i test gestiti da Maven Surefire, tra cui:

- test di dominio;
- test unitari dei service;
- test web dei controller con MockMvc.

### Suite completa

```bash
cd backend
./mvnw clean verify
```

Questo comando esegue anche i test di integrazione con suffisso `IT`, gestiti tramite Maven Failsafe.

I test di integrazione usano PostgreSQL tramite Testcontainers e non dipendono dal database locale.

La strategia di M1 comprende test verticali sui flussi principali di creazione, dettaglio, modifica ed eliminazione,
mentre paginazione e ordinamento sono verificati nei livelli in cui portano maggior valore senza duplicare inutilmente
la copertura.

---

## Test frontend

### Esecuzione completa dei test

```bash
cd frontend
npm run test:run
```

### Modalità interattiva

```bash
cd frontend
npm test
```

### Controllo ESLint

```bash
cd frontend
npm run lint
```

### Verifica della build

```bash
cd frontend
npm run build
```

I test frontend usano:

- Vitest come test runner;
- Testing Library per il comportamento osservabile dei componenti;
- `userEvent` per simulare le interazioni utente;
- MSW per intercettare le richieste HTTP reali dei client API;
- `MemoryRouter` nei test che dipendono dal routing.

---

## Standard di sviluppo consolidati in M1

### Backend

- controller sottile;
- DTO separati dalle entity JPA;
- DTO distinti per casi d'uso differenti quando devono poter evolvere separatamente;
- dominio indipendente dal contratto HTTP;
- service come confine transazionale;
- `@Transactional(readOnly = true)` per le letture quando appropriato;
- dirty checking JPA per gli update delle entity managed;
- repository standard prima di query personalizzate;
- Flyway come unico proprietario dello schema;
- `ddl-auto: validate`, mai `update`;
- error handling centralizzato;
- OpenAPI aggiornata insieme al backend;
- test scelti in base al rischio reale, evitando duplicazioni inutili.

### Frontend

- `*Api.js` per HTTP, parsing ed errori;
- `*Page.jsx` per orchestrare router, rete e stati asincroni;
- componenti presentazionali basati su props;
- `RouteForm` condiviso tra create e update;
- validazione e normalizzazione in funzioni pure;
- `AbortController` per GET legate al lifecycle;
- `ApiRequestError` come errore HTTP condiviso;
- `handleJsonResponse` come gestione comune delle response;
- `Link` per navigazione e `button` per azioni;
- `role="status"` per loading e `role="alert"` per errori;
- test basati su comportamento utente e semantica accessibile;
- MSW preferito al mock diretto di `fetch`.

---

## Workflow Git

Branch principali:

- `main`: versioni stabili e rilasciabili;
- `develop`: integrazione delle feature completate;
- `feature/*`: sviluppo di singole user story o funzionalità;
- `fix/*`: correzioni;
- `hotfix/*`: correzioni urgenti su una release;
- `chore/*` / `docs/*`: attività tecniche o documentali isolate.

Esempi di branch usati in M1:

```text
feature/create-route
feature/list-routes
feature/route-details
feature/update-route
feature/delete-route
```

I commit devono essere piccoli e descrittivi. Esempi:

```text
feat(route): add route delete api frontend
feat(route): add route delete button in route details page
refactor(route): replace history entry after route update
docs(readme): update project status after M1
```

---

## Definition of Done

Prima del merge di una user story:

- criteri di accettazione soddisfatti;
- backend completato;
- frontend completato quando previsto;
- migration Flyway presente solo se necessaria;
- test unitari e web verdi;
- test di integrazione verdi;
- `./mvnw clean verify` completato con successo;
- OpenAPI aggiornata e verificata;
- Swagger UI controllata manualmente;
- `npm run test:run` verde;
- `npm run lint` verde;
- `npm run build` verde;
- flusso principale verificato manualmente dal browser;
- commit puliti;
- merge in `develop`.

---

## Prima release — `v0.1.0`

La prima release stabile di MotoRoute comprende l'intera M1 e quindi il CRUD completo degli itinerari:

```text
Create  -> POST   /api/routes
Read    -> GET    /api/routes
Read    -> GET    /api/routes/{routeId}
Update  -> PUT    /api/routes/{routeId}
Delete  -> DELETE /api/routes/{routeId}
```

Prima di creare il tag della release devono risultare verdi:

```bash
cd backend
./mvnw clean verify
```

```bash
cd frontend
npm run test:run
npm run lint
npm run build
```

Il flusso CRUD deve inoltre essere verificato manualmente dal browser.

Tag previsto:

```text
v0.1.0
```

---

## Prossimi sviluppi

La prossima milestone è:

```text
M2 — Waypoint e composizione dell'itinerario
```

La prima user story prevista è:

```text
US-2.1 — Creazione waypoint
```

La nuova feature introdurrà il modello dei waypoint e la relazione con gli itinerari. Le decisioni architetturali di M1
rimangono la baseline da mantenere anche nelle milestone successive.

Non vengono introdotti in anticipo elementi come soft delete, autorizzazione/ownership, PostGIS, query native o design
system completo se non richiesti da una user story concreta.
