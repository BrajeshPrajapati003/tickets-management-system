# Ticket Management System

A Spring Boot REST API for managing events, ticket types, ticket purchases, QR codes, and ticket validation. The project uses PostgreSQL for application data and Keycloak as an OAuth 2.0 / JWT issuer.

## What it implements

- Organizer event management: create, list, view, update, and delete events. Event creation includes ticket types; updates can modify the event's ticket types.
- Public event discovery: list published events, search by event name or venue, and view published event details.
- Ticket purchasing: purchase a ticket for a ticket type, with an availability check.
- Ticket ownership views: list a user's tickets, retrieve a ticket, and download its QR code as a PNG.
- Ticket validation: validate by QR code ID or manually by ticket ID. Validation records whether a ticket has already been validated.
- User provisioning: an authenticated JWT is used to create/update the local user record used by organizer and ticket operations.

## Technology

- Java 21
- Spring Boot 4.1.1, Spring MVC, Spring Data JPA, Bean Validation
- Spring Security OAuth 2.0 Resource Server
- PostgreSQL; H2 runtime dependency and H2 console support are also present
- Keycloak (provided through Docker Compose)
- ZXing 3.5.4 for PNG QR code generation
- MapStruct 1.6.3 and Lombok
- Maven Wrapper

## How the application works

### Application components

1. A client sends requests to the Spring Boot API.
2. Keycloak issues JWTs. The API checks those tokens for protected requests.
3. The API reads and writes application data in PostgreSQL.
4. Adminer provides a browser interface for PostgreSQL.

### Ticket purchase and validation

1. A user browses published events and requests a ticket.
2. The API checks whether the selected ticket type has capacity.
   - If no tickets remain, the request fails as sold out.
   - If tickets remain, the API saves the ticket.
3. The API generates and stores a QR code for the ticket.
4. Staff validates the ticket using its QR code or ticket ID.
5. The API records the validation result.

## Requirements

- JDK 21
- Docker Desktop with Docker Compose
- A Keycloak realm configured to issue JWTs for this application

The repository does not include a Keycloak realm import, application client setup, or sample access token. The Compose service starts Keycloak in development mode, but does not configure the realm expected by the application.

## Start the supporting services

From the repository root:

```sh
docker compose up -d
```

Compose starts:

| Service | Local address | Purpose |
| --- | --- | --- |
| PostgreSQL | `localhost:5432` | Application database |
| Adminer | `http://localhost:18080` | Database browser |
| Keycloak | `http://localhost:19090` | Identity provider |

The Compose file uses these development defaults:

- PostgreSQL password: `changemeinprod!`
- Keycloak administrator: `admin` / `admin@pass`

They can be overridden with `DB_PASSWORD`, `KEYCLOAK_ADMIN`, and `KEYCLOAK_ADMIN_PASSWORD` in the environment used to run Docker Compose, or via Compose's standard `.env` file loading. These are development defaults; change them for any non-local use. Adminer connects to the database using the Compose service name `db`, port `5432`, and the PostgreSQL credentials.

Keycloak is configured with `start-dev` and its development file database. Its data is stored in the Compose volume `keycloak-data`.

## Configure and run the API

The base configuration activates the `dev` profile. The checked-in `application-dev.yml` supplies these defaults:

| Setting | Default |
| --- | --- |
| HTTP port | `8080` |
| Database URL | `jdbc:postgresql://localhost:5432/postgres` |
| Database username | `postgres` |
| Database password | `changemeinprod!` |
| JWT issuer URI | `http://localhost:19090/realms/event-ticket-platform` |

The PostgreSQL URL, username, password, and JWT issuer can be overridden with `SPRING_DB_URL`, `SPRING_DB_USERNAME`, `SPRING_DB_PASSWORD`, and `SPRING_JWT_ISSUER_URI`. The issuer URI must match the issuer claim in tokens issued by the configured realm. Configure the realm to include the application's required realm roles; the JWT converter recognizes realm roles whose names start with `ROLE_`.

The base `application.yaml` also declares port `9090` and requires the four environment variables above without defaults. Because it activates the `dev` profile, the dev profile's port and defaults apply unless configuration is overridden.

Run the application from the repository root:

```sh
# Windows
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

The API listens on port `8080` with the repository's default profile configuration. To connect from the host to the Compose PostgreSQL service, the defaults above match the Compose port and password.

## REST API

All routes use the `/api/v1` prefix. UUID values are path parameters. Responses that list records use Spring Data pagination through `Pageable` query parameters.

| Method | Path | Access rule | Behavior |
| --- | --- | --- | --- |
| `POST` | `/api/v1/events` | `ORGANIZER` role | Create an event and its ticket types |
| `GET` | `/api/v1/events` | Authenticated; organizer-scoped | List the current organizer's events |
| `GET` | `/api/v1/events/{eventId}` | Authenticated; organizer-scoped | Get an organizer's event |
| `PUT` | `/api/v1/events/{eventId}` | Authenticated; organizer-scoped | Update an event and its ticket types |
| `DELETE` | `/api/v1/events/{eventId}` | Authenticated; organizer-scoped | Delete an organizer's event |
| `GET` | `/api/v1/published-events` | Public | List published events; optional `q` searches name and venue |
| `GET` | `/api/v1/published-events/{eventId}` | Public | Get a published event |
| `POST` | `/api/v1/events/{eventId}/ticket-types/{ticketTypeId}/tickets` | Authenticated | Purchase a ticket; returns `204` on success |
| `GET` | `/api/v1/tickets` | Authenticated; current user | List the current user's tickets |
| `GET` | `/api/v1/tickets/{ticketId}` | Authenticated; current user | Get one of the current user's tickets |
| `GET` | `/api/v1/tickets/{ticketId}/qr-codes` | Authenticated; current user | Return the ticket QR code as `image/png` |
| `POST` | `/api/v1/ticket-validations` | `STAFF` role | Validate using a request body's method and ID |

For ticket validation, the request DTO contains a `method` and an `id`: `MANUAL` treats the ID as a ticket ID; other supported method values use it as a QR code ID. The response contains the validation result.

The application is stateless and expects bearer JWTs for protected routes. Public access is explicitly granted only to GET requests under `/api/v1/published-events/**`. Event operations are scoped to the user ID parsed from the JWT. The security converter reads `realm_access.roles` and grants only role names prefixed with `ROLE_`; the route rules check for `ORGANIZER` and `STAFF` roles. Tokens without those roles will not pass those checks.

## Data model

The JPA model includes:

- **User** — identity-provider UUID, name, email, and organizer, attendee, and staff relationships.
- **Event** — organizer, schedule, venue, ticket sales dates, status, and ticket types.
- **TicketType** — name, price, description, capacity, and associated event.
- **Ticket** — purchaser, ticket type, and purchase/cancellation status.
- **QrCode** — generated QR identifier, stored PNG data, status, and associated ticket.
- **TicketValidation** — ticket, validation method, result, and audit fields.

Event statuses are `DRAFT`, `PUBLISHED`, `CANCELLED`, and `COMPLETED`. Ticket statuses are `PURCHASED` and `CANCELLED`. The QR code contains a generated UUID, and its PNG image is stored in the database as Base64 data. Hibernate is configured with `ddl-auto: update`; there are no migration files in the repository.

## Project layout

```text
src/main/java/com/brajesh/tickets/
├── config/        # Security, JWT conversion, JPA auditing, QR writer
├── controllers/   # REST endpoints and exception handling
├── domain/        # Requests, response DTOs, and JPA entities
├── exception/     # Application exceptions
├── filters/       # JWT-backed local user provisioning
├── mappers/       # DTO/entity mappings
├── repository/    # Spring Data repositories
├── service/       # Service interfaces and implementations
└── util/          # JWT utilities
src/main/resources/
├── application.yaml
├── application-dev.yml
└── META-INF/orm.xml
src/test/           # Spring application context test
docker-compose.yml  # PostgreSQL, Adminer, and Keycloak
pom.xml             # Maven build and dependencies
```

## Build and tests

Build the project:

```sh
# Windows
.\mvnw.cmd clean package

# macOS / Linux
./mvnw clean package
```

Run the test suite:

```sh
# Windows
.\mvnw.cmd test

# macOS / Linux
./mvnw test
```

The checked-in test source contains a Spring application context-load test. The current test suite contains a Spring application context-load test.

## Current scope and caveats

- The repository contains the backend API and supporting services; it does not contain a web client.
- The Compose file does not provision a Keycloak realm, client, users, or role mappings. Those must be set up in Keycloak before protected API calls can be made.
- Ticket validation routes require the `STAFF` role, while the validator service records validation using the supplied ticket or QR code identifier.
- The development profile enables SQL output and Spring debug logging and uses Hibernate schema auto-update. Use deliberate database migrations and production-appropriate security/configuration before deploying.
- The Compose images use `latest`, so image versions can change when pulled.
- `keycloak.html` is a captured Keycloak administration page, not an application frontend.











