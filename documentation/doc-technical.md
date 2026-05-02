# Technical Design Document

## Table of Contents

1. [Architecture Patterns](#architecture-patterns)
2. [Tool System Design](#tool-system-design)
3. [Domain Model](#domain-model)
4. [Database Design](#database-design)
5. [Implementation Structure](#implementation-structure)
6. [Design Decisions](#design-decisions)
7. [Security & Authorisation](#security--authorisation)
8. [Data Flow Patterns](#data-flow-patterns)
9. [Repository & Use Case Implementation](#repository--use-case-implementation)
10. [Validation & Error Handling](#validation--error-handling)
11. [Testing](#testing)

---

## Architecture Patterns

### Layered Architecture with Ports and Adapters

The system uses four layers. The main idea is that dependencies always point inward. Outer layers know about inner layers, never the reverse similar to a hexagonal architectural structure with a core and adapters to third parties.

**Layer 1: Presentation**: console menus, input collection, result display. Translates what the user types into application-layer requests.

**Layer 2: Application**: use cases live here. Also defines port interfaces (inbound: what the app offers, outbound: what it needs from infrastructure). The tool registry that enforces access control sits here too.

**Layer 3: Domain**: business logic, entities, value objects. Doesn't know about databases or consoles. Shouldn't import anything from outer layers.

**Layer 4: Infrastructure**: JSON file persistence that fulfils the outbound port contracts. File I/O, serialisation, etc.

The ports-and-adapters part means the application layer never directly depends on the storage mechanism as it talks through interfaces. Infrastructure provides the concrete adapter, the same as in hexagonal architecture. This makes testing straightforward since I can mock the repositories without touching the real data file.

### Repository Pattern

Data access is abstracted through interfaces in `application/port/out/`, with JSON file implementations in `infrastructure/adapter/persistence/`. A central DependencyInjection class wires the concrete implementations at startup.

The reason for this is mostly testability. domain and application tests don't need the real file. It also means if I ever wanted to swap JSON for a proper database later, the change would be isolated to infrastructure and mitigate shotgun surgery.

### Dependency Injection

Constructor injection throughout. One DependencyInjection class at startup creates everything and passes dependencies down.

---

## Tool System Design

### Concept

Each organisation gets a specific set of tools (use cases) based on what they're authorised to do. Rather than a traditional role-permissions system, I'm using enum tool types, which makes it incredibly simple to configure functionality for each org. The organisation's profile lists which tools it can access, and the registry enforces this.

### Tool Categories

#### CORE Tools (All Organisations)

| Tool Name | Purpose |
|-----------|---------|
| VIEW_WORKER_ID | View basic worker information |
| VERIFY_BASIC | Check if worker ID is currently valid |

#### IDENTITY MANAGEMENT Tools (Central Authority Only)

| Tool Name | Purpose |
|-----------|---------|
| CREATE_WORKER_ID | Register new food service worker |
| UPDATE_WORKER_ID | Modify permitted worker attributes |
| CHANGE_STATUS | Activate, suspend, or revoke worker ID |
| DELETE_WORKER_ID | Permanently remove worker from system |

#### CERTIFICATION MANAGEMENT Tools (Central Authority Only)

| Tool Name | Purpose |
|-----------|---------|
| ADD_CERTIFICATION | Record new food safety certification |
| RENEW_CERTIFICATION | Update expiring certifications |
| UPDATE_CERTIFICATION_STATUS | Suspend, expire, or reactivate certifications |

#### ENHANCED VERIFICATION Tools (Specialised Organisations)

| Tool Name | Purpose | Available To |
|-----------|---------|--------------|
| VERIFY_WITH_CERT_HISTORY | Full certification history | Fine Dining Restaurants |
| VERIFY_WITH_CONDITIONS | Specific conditions (driver status, background, etc.) | Delivery Services |
| VERIFY_WITH_PERMITS | Mobile vendor permits and licences | Street Vendor Associations |
| VERIFY_WITH_ATTRIBUTES | Specific worker attributes (allergen training, HACCP) | Fine Dining Restaurants |

#### REPORTING & ANALYTICS Tools (Central Authority)

| Tool Name | Purpose |
|-----------|---------|
| VIEW_AUDIT_LOG | View system audit trail, filterable by worker/org/date |
| GENERATE_COMPLIANCE_REPORT | Compliance reports by region |
| CHECK_EXPIRING_CERTS | Find certifications expiring within timeframe |
| GENERATE_REGIONAL_REPORT | Regional compliance statistics |
| VIEW_ORGANISATION_ACTIVITY | Track verification requests by organisation |

#### SEARCH & QUERY Tools (Central Authority)

| Tool Name | Purpose |
|-----------|---------|
| SEARCH_WORKERS | Search by name, email, or worker ID with filters |
| SEARCH_BY_CERTIFICATION | Find workers with specific certification type |
| SEARCH_BY_EXPIRATION | Find workers with expiring certifications |

#### BATCH OPERATIONS Tools (Central Authority)

| Tool Name | Purpose |
|-----------|---------|
| BULK_STATUS_UPDATE | Update status for multiple workers |
| BULK_CERTIFICATION_CHECK | Check certification validity for multiple workers |
| EXPORT_WORKER_DATA | Export worker data for backup or reporting |

#### NOTIFICATION Tools (Central Authority)

| Tool Name | Purpose |
|-----------|---------|
| SEND_RENEWAL_REMINDER | Notify workers of expiring certifications |
| SEND_STATUS_NOTIFICATION | Notify worker of status changes |

### Tool Registry

The UseCaseRegistry is basically a factory with access control baked in. When an organisation requests a tool:

1. Registry checks if the organisation's profile includes that tool
2. If yes it returns the use case instance
3. If no it throws UnauthorisedAccessException

The presentation layer only shows menu options for tools the org actually has, so in practice the exception path is a safety net rather than something users would normally hit.

### Organisation Tool Matrix

| Organisation Type | Tool Count | Tools Available |
|------------------|------------|-----------------|
| Central Authority | 22+ | Everything |
| Fine Dining | 4 | Core + certification history + attributes |
| Delivery Service | 3 | Core + conditional verification |
| Street Vendors | 3 | Core + permit verification |
| FoodPay Financial | 2 | Core only |
| Fast Food Chain | 2 | Core only |
| Coffee Shop | 2 | Core only |

---

## Domain Model

### Entities

#### Worker

Represents a food service professional. Main fields:

- Unique identifier with region prefix (e.g., `WK-US-2024-001`)
- Full legal name, date of birth (immutable after creation)
- Email, operating region (mutable by central authority)
- Current status (ACTIVE / SUSPENDED / REVOKED)
- Work authorisation docs
- Collection of certifications
- Timestamps for creation and last modification

The worker entity can check its own eligibility e.g. whether it has valid certs for a given region, or whether an update is allowed based on current status, etc.

#### Certification

A food safety certification or permit attached to a worker.

Fields: unique ID, worker ID, certification type (enum), issuing authority, cert number, issue date, expiration date, status.

It knows if it's expired, how many days until expiration, whether renewal is needed. Issue date must be before expiration date. Expired certs can't be marked valid again.

#### Work Authorisation

Every region has different work authorisation requirements: US uses I9 Certification, green card, UK requires valid passport etc. I am simplifying this by having one WokrAuthorisation class. This tracks right-to-work verification across all regions. Fields: ID, worker ID, region, verification date, status, documents presented, expiry date (null for indefinite, e.g. citizens), verified by.



Different regions have different timing requirements. UK requires verification *before* employment starts, US allows 3 business days after hire. The entity handles this through a `meetsRegionalTimingRequirement(hireDate)` method that switches on region. Expiry date being null means indefinite right to work (citizens, settled status, etc.).

### Value Objects

**OrganisationContext**: carries org type, ID, profile, and request timestamp through the system. Passed to use cases so they know who's asking.

**VerificationResult**: outcome of a verification. Can be basic (just valid/invalid), or enriched with cert lists, condition checks, or permit info depending on which tool was used.

### Enumerations

**CertificationType**: 20+ values across 12 regions. Each carries its typical validity duration, home region, display name, and issuing authority. This is probably the most complex enum in the system.

**Region**: 12 values (US, UK, Germany, France, Italy, Spain, EU general, Singapore, Japan, Hong Kong, South Korea, China). Each has country code, locale code, currency symbol.

**WorkerStatus**: just ACTIVE, SUSPENDED, REVOKED. Controls what operations are allowed.

**OrganisationType**: the 7 org types. Maps to profiles which determine tool access.

---

## Data Persistence

### Approach: JSON File Storage

The system persists all data to a single `digitalid.json` file. On startup, the file is loaded into memory as domain objects. On any write operation, the updated state is written back to the file.

I originally planned to use SQLite, but after thinking about it more, it added complexity that wasn't justified for this project's scale.

### Data Structure

The JSON file contains:
- **workers**: array of worker objects with their certifications and work authorisation nested
- **auditLog**: array of audit entries

### Why JSON over SQLite

- No additional dependencies (Gson or Jackson for JSON vs sqlite-jdbc)
- Human-readable: can open the file and inspect data directly
- Simpler implementation: no schema or connection management
- Data persists between restarts (unlike pure in-memory)
- Appropriate for the project's scale

SQLite would make sense if this were a production system with concurrent users and large datasets. For a console app with one user and a few hundred records, JSON is simpler and achieves the same goal.

### Why not pure in-memory?

Data needs to persist between restarts, otherwise the audit trail is worthless and you'd have to re-create workers every time you run the system.

---

## Implementation Structure

### Package Organisation

```
com.digitalid.domain
├── model/          - Entities and value objects
├── service/        - Domain services (business logic)
└── exception/      - Domain-specific exceptions

com.digitalid.application
├── port/in/        - Inbound port interfaces
├── port/out/       - Outbound port interfaces
├── registry/       - Tool registry for access control
├── usecase/        - Use case implementations
└── request/        - Request objects for use case inputs

com.digitalid.infrastructure
├── adapter/persistence/  - JSON file repository implementations
└── config/               - DB connection + dependency injection

com.digitalid.presentation
└── console/        - Console UIs for each organisation type
```

### Naming Conventions

Standard Java conventions. Classes in PascalCase, methods in camelCase, enum values in UPPER_SNAKE_CASE, packages lowercase. Nothing unusual here.

---

## Design Decisions

### Architecture

I went with a layered architecture enhanced with ports and adapters. There's only one adapter type (console in, JSON file out), so the full hexagonal architecture seemed slightly excessive. But I still wanted the testability that ports give you, so the hybrid approach made sense.

### Tool-Based Access Control

Instead of a traditional role/permission matrix, each organisation has a list of tool enums in its profile. The registry checks this list before handing out use case instances. I was inspired by my work with agentic orchestration, where a toolset is given to sub-agents, leading to an effective separation of concerns.

I find this more readable than scattered permission checks, as you look at an org's profile and immediately see what it can do.

The downside is it's slightly less granular than a full permission system (you can't do "this org can UPDATE_WORKER_ID but only for workers in their region" without additional logic in the use case itself). For this project's scope, that's not required so I deemed this an effective approach.

### JLine for Terminal Interaction

I chose JLine over a plain `Scanner` for the console UI. The main reason is usability: arrow key navigation through menus is a much better experience than typing numbers, especially when there are 20+ options on screen. It makes the demo feel like an actual tool rather than a homework assignment.

JLine puts the terminal into raw mode, which means I read individual keypresses instead of waiting for the user to hit Enter. This lets me render a highlighted menu that updates as the user navigates. The trade-off is that text input gets more involved since I have to manually handle backspace, echo characters, and detect Enter myself. I wrapped all of that in a `TerminalMenu` class so the rest of the presentation layer doesn't need to know about raw mode or escape sequences.

The alternative was to use Lanterna (a full TUI framework with windows and borders), but that felt heavy for what I needed. JLine gives me just enough control without pulling in a whole UI toolkit.

### JSON Persistence

Covered above in Data Persistence. Simple, human-readable, no extra dependencies beyond Gson, appropriate for the scale of this project.

### Multi-Region Support

Food service is genuinely global, and I wanted to show the system can handle complexity beyond a single jurisdiction. 12 regions with different certification types, validity periods, and naming conventions. Could've kept it US-only to save time, but the regional handling is where a lot of the interesting business logic lives.

---

## Security & Authorisation

### Access Control

Three layers of enforcement:

1. **Presentation**: menus only show tools the org is allowed to use.
2. **Application (Registry)**: even if someone bypasses the UI, the registry checks the org profile before returning a use case. 
3. **Domain**: use cases can perform additional validation (e.g., checking a worker belongs to the org's region), so business logic is enforced.

### Audit Trail

Every action gets logged. The audit log is append-only from the application's perspective. It's there for compliance (health inspections need to see who verified what) and for debugging.

---

## Data Flow Patterns

### Creating a Worker (example)

1. Console collects worker details, builds a CreateWorkerRequest
2. Console asks registry for CREATE_WORKER_ID tool
3. Registry checks org profile, returns CreateWorkerIdUseCase
4. Use case validates the request
5. Domain service enforces business rules, creates Worker entity
6. Repository persists to JSON file
7. Audit log records the action
8. Use case returns the created worker
9. Console displays confirmation

### Verifying a Worker

1. Console collects worker ID
2. Gets appropriate verification use case from registry
3. Use case retrieves worker from repository
4. Domain logic determines result based on verification type
5. Result returned and displayed

---

## Repository & Use Case Implementation

### Repository Interfaces

Defined in the application layer (`port/out/`). Standard operations:

```
save(entity)
findById(id)
findAll()
findByRegion(region)  // etc.
delete(id)
```

JSON implementations load the full dataset into memory on startup and write back to the file on every mutation. Gson handles serialisation/deserialisation.

### Use Cases

Each use case is one class, one business operation. Implements a UseCase interface, takes a request object and org context, returns a result. Responsibilities: validate input, check authorisation (via registry), invoke domain logic, call repositories, log to audit trail, return result.

I considered having use cases do less (just coordination) with all logic in domain services. In practice, some operations are simple enough that a separate domain service would just be an unnecessary indirection. I'll probably keep thinner use cases for complex operations and let simpler ones handle their own logic. Might revisit this if it gets messy.

---

## Validation & Error Handling

### Validation

Validation happens at every level to check things like correct data types, expiration, worker verification etc.

### Exception Hierarchy

I'm keeping this fairly simple:

- **Domain:** InvalidOperationException (business rule violations), WorkerNotFoundException, CertificationExpiredException
- **Application:** UnauthorisedAccessException, ValidationException
- **Infrastructure:** DatabaseException, ConnectionException

Exceptions propagate upward, so infrastructure throws, application catches and wraps if needed, presentation catches and shows a human-readable message.

---

## Testing

### Structure

Tests mirror the source packages. Domain tests don't need any mocking. Application tests mock the repositories (that's the whole point of the port interfaces). Infrastructure tests use a separate test JSON file. Integration tests wire everything together with a real data file.

I'm planning to use test fixture builders for creating realistic test data, and use a fresh temp file between test runs so they don't depend on each other.

Haven't decided yet whether presentation tests are worth the effort as testing console I/O can be very subjective. I may just cover these manually, as presentation tends to be fairly visible.

---