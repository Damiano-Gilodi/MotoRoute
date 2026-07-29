# MotoRoute

MotoRoute è un'applicazione web full stack per creare, consultare, gestire e condividere itinerari motociclistici.

## Stato del progetto

Il progetto è attualmente in fase di sviluppo.

### Milestone completate

- **M0 — Fondamenta del progetto**

### User story completate

- **US-1.1 — Creazione itinerario**
- **US-1.2 — Elenco itinerari**

### Funzionalità disponibili

- configurazione separata di backend e frontend;
- database PostgreSQL eseguito tramite Docker Compose;
- gestione dello schema tramite migration Flyway;
- validazione dello schema tramite Hibernate;
- test di integrazione con PostgreSQL e Testcontainers;
- gestione standardizzata degli errori API;
- documentazione OpenAPI e Swagger UI;
- creazione di un itinerario;
- visualizzazione paginata degli itinerari;
- ordinamento degli itinerari;
- pagina frontend per la creazione di un itinerario;
- pagina frontend per l'elenco degli itinerari;
- gestione degli stati di caricamento, errore ed elenco vuoto;
- navigazione tra le pagine dell'elenco.

La prossima attività prevista è:

- **US-1.3 — Dettaglio itinerario**

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
- Vitest
- Testing Library
- Mock Service Worker

### Infrastruttura

- Docker
- Docker Compose
- Git
- GitHub

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

## Requisiti

Per eseguire il progetto in locale sono necessari:

- Java 25;
- Docker;
- Docker Compose;
- Node.js;
- npm.

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

Durante l'avvio, Flyway applica automaticamente le migration mancanti.

### 3. Avvio del frontend

Aprire un secondo terminale ed eseguire:

```bash
cd frontend
npm install
npm run dev
```

Vite mostrerà nel terminale l'indirizzo locale del frontend.

## Pagine frontend

### Elenco degli itinerari

```text
/routes
```

La pagina mostra:

- stato di caricamento;
- elenco degli itinerari;
- stato vuoto;
- errori restituiti dall'API;
- navigazione tra le pagine.

### Creazione di un itinerario

```text
/routes/new
```

La pagina permette di inserire:

- nome;
- descrizione opzionale;
- luogo di partenza;
- luogo di arrivo;
- distanza in chilometri;
- difficoltà.

## Test backend

### Test unitari e web

```bash
cd backend
./mvnw test
```

Questo comando esegue i test gestiti da Maven Surefire, tra cui:

- test unitari dei service;
- test web dei controller;
- test con MockMvc.

### Suite completa

```bash
cd backend
./mvnw clean verify
```

Questo comando esegue anche i test di integrazione con suffisso `IT`, gestiti tramite Maven Failsafe.

I test di integrazione utilizzano PostgreSQL tramite Testcontainers e non dipendono dal database locale.

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

## API

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

In caso di creazione completata correttamente, l'API restituisce:

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

### Parametri

| Parametro | Descrizione                            | Valore predefinito |
|-----------|----------------------------------------|-------------------:|
| `page`    | Numero della pagina, a partire da zero |                `0` |
| `size`    | Numero di elementi per pagina          |               `20` |
| `sort`    | Campo e direzione di ordinamento       |   `createdAt,desc` |

La dimensione della pagina deve essere compresa tra `1` e `100`.

### Campi ordinabili

Sono attualmente supportati:

- `createdAt`;
- `name`;
- `startLocation`;
- `endLocation`;
- `distanceKm`;
- `difficulty`.

Le direzioni ammesse sono:

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

Quando non sono presenti itinerari, l'API restituisce comunque `200 OK`:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

## Gestione degli errori API

Gli errori HTTP utilizzano una struttura comune.

Esempio:

```json
{
  "timestamp": "2026-07-29T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "size must be between 1 and 100",
  "path": "/api/routes",
  "fieldErrors": {}
}
```

Gli errori di validazione relativi ai campi possono contenere dettagli nella proprietà `fieldErrors`.

Esempio:

```json
{
  "timestamp": "2026-07-29T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "One or more fields are invalid",
  "path": "/api/routes",
  "fieldErrors": {
    "name": "must not be blank"
  }
}
```

## Documentazione OpenAPI

Con il backend in esecuzione, la documentazione è disponibile ai seguenti indirizzi:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`

La documentazione include attualmente:

- `POST /api/routes`;
- `GET /api/routes`;
- modelli di richiesta e risposta;
- parametri di paginazione e ordinamento;
- codici HTTP principali;
- struttura standard degli errori.

## Workflow Git

Il progetto utilizza i seguenti branch principali:

- `main`: versioni stabili e rilasciabili;
- `develop`: integrazione delle feature;
- `feature/*`: sviluppo delle singole user story.

Esempi:

```text
feature/create-route
feature/list-routes
feature/route-details
```

## Prossimi sviluppi

La prossima user story prevista è:

```text
US-1.3 — Dettaglio itinerario
```

L'endpoint previsto sarà:

```http
GET /api/routes/{routeId}
```

La documentazione completa del progetto, i diagrammi architetturali, gli screenshot e le informazioni sul deployment
verranno aggiunti nelle milestone successive.
