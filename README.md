# MotoRoute

MotoRoute è un'applicazione web full stack per creare, gestire e condividere itinerari motociclistici.

## Stato del progetto

Il progetto è in fase di sviluppo.

Attualmente sono disponibili:

* configurazione del backend Spring Boot;
* database PostgreSQL gestito tramite Flyway;
* test di integrazione con PostgreSQL e Testcontainers;
* gestione standardizzata degli errori API;
* creazione di un itinerario tramite `POST /api/routes`;
* documentazione OpenAPI e Swagger UI.

La prossima attività prevista è lo sviluppo del frontend per la creazione degli itinerari.

## Stack tecnologico

### Backend

* Java 25
* Spring Boot 4
* Spring MVC
* Spring Data JPA
* PostgreSQL
* Flyway
* Maven
* Testcontainers
* Springdoc OpenAPI

### Frontend

* React
* JavaScript
* JSX
* Vite

### Infrastruttura

* Docker
* Docker Compose
* GitHub
* GitFlow

## Avvio locale

### Database

Avviare PostgreSQL dalla directory principale del progetto:

```bash
docker compose up -d
```

### Backend

Avviare il backend:

```bash
cd backend
./mvnw spring-boot:run
```

Il backend sarà disponibile all'indirizzo:

```text
http://localhost:8080
```

## Test backend

Eseguire i test unitari e i test del layer web:

```bash
cd backend
./mvnw test
```

Eseguire la suite completa, inclusi i test di integrazione:

```bash
cd backend
./mvnw verify
```

## API

### Creazione di un itinerario

```http
POST /api/routes
```

In caso di creazione completata correttamente, l'API restituisce:

```http
201 Created
Location: /api/routes/{id}
```

## Documentazione OpenAPI

Con il backend in esecuzione, la documentazione è disponibile ai seguenti indirizzi:

* Swagger UI: `http://localhost:8080/swagger-ui.html`
* OpenAPI JSON: `http://localhost:8080/v3/api-docs`
* OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`

