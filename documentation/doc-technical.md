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

The system uses four layers. The main idea is that dependencies always point inward — outer layers know about inner layers, never the reverse.

**Layer 1: Presentation** — console menus, input collection, result display. Translates what the user types into application-layer requests.

**Layer 2: Application** — use cases live here. Also defines port interfaces (inbound: what the app offers, outbound: what it needs from infrastructure). The tool registry that enforces access control sits here too.

**Layer 3: Domain** — business logic, entities, value objects. Doesn't know about databases or consoles. Shouldn't import anything from outer layers.

**Layer 4: Infrastructure** — SQLite repository implementations that fulfil the outbound port contracts. Database connection management, etc.

The ports-and-adapters part means the application layer never directly depends on SQLite — it talks through interfaces. Infrastructure provides the concrete adapter. This makes testing straightforward since I can mock the repositories without touching a real database.

### Repository Pattern

Data access is abstracted through interfaces in `application/port/out/`, with SQLite implementations in `infrastructure/adapter/persistence/`. A central DependencyInjection class wires the concrete implementations at startup.

The reason for this is mostly testability — domain and application tests don't need a database. It also means if I ever wanted to swap SQLite for something else, the change would be isolated to infrastructure.

### Dependency Injection

Constructor injection throughout. One DependencyInjection class at startup creates everything and passes dependencies down. Nothing fancy — no framework, just manual wiring.

---

## Tool System Design

### Concept

Each organisation gets a specific set of tools (use cases) based on what they're authorised to do. Rather than a traditional role-permissions system, I'm using enumerated tool types — the organisation's profile literally lists which tools it can access, and the registry enforces this.

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
2. If yes — returns the use case instance
3. If no — throws UnauthorisedAccessException

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

The worker entity can check its own eligibility — whether it has valid certs for a given region, whether an update is allowed based on current status, etc.

Key invariants: identifier must be unique, can't go from revoked back to active, updates are forbidden when revoked.

#### Certification

A food safety certification or permit attached to a worker.

Fields: unique ID, worker ID, certification type (enum), issuing authority, cert number, issue date, expiration date, status.

It knows if it's expired, how many days until expiration, whether renewal is needed. Issue date must be before expiration date — expired certs can't be marked valid again.

#### I9 Documentation

US-specific. Tracks employment eligibility verification — verification date, status, documents presented, next reverification date for temporary authorisations.

The 3-business-day requirement for new hires is enforced here. I'm not 100% sure yet how strictly I want to enforce this in the domain vs just flagging it — probably strict enforcement with a domain exception if violated.

### Value Objects

**OrganisationContext** — carries org type, ID, profile, and request timestamp through the system. Passed to use cases so they know who's asking.

**VerificationResult** — outcome of a verification. Can be basic (just valid/invalid), or enriched with cert lists, condition checks, or permit info depending on which tool was used.

### Enumerations

**CertificationType** — 20+ values across 12 regions. Each carries its typical validity duration, home region, display name, and issuing authority. This is probably the most complex enum in the system.

**Region** — 12 values (US, UK, Germany, France, Italy, Spain, EU general, Singapore, Japan, Hong Kong, South Korea, China). Each has country code, locale code, currency symbol.

**WorkerStatus** — just ACTIVE, SUSPENDED, REVOKED. Controls what operations are allowed.

**OrganisationType** — the 7 org types. Maps to profiles which determine tool access.

---

## Database Design

### Tables

**Workers** — primary entity table. One-to-many with certifications, referenced by I9 docs and audit log.

**Certifications** — all food safety certs. Foreign key back to workers. Supports multiple certs per worker across different regions.

**I9 Documentation** — one-to-one with US workers. Not applicable for non-US workers. Links to a document records sub-table.

**Audit Log** — every system action. Deliberately has no foreign key constraints — if a worker gets deleted, their audit history should remain. Records what happened, to whom, by which org, and when.

### Indexes

I'll index the obvious hot paths:
- Workers by region and status (for compliance reports)
- Certifications by worker ID (retrieved together constantly)
- Certifications by expiration date (for the expiring-soon queries)
- Audit log by timestamp and entity ID

Might add more based on actual query patterns once the system is running.

### Why SQLite

It's an embedded file-based database — one `digitalid.db` file in the project root. No server installation, no configuration, assessors can just run the jar. It supports full ACID transactions and standard SQL, which is more than enough for the scale of this system. The main alternative would've been PostgreSQL, but making assessors install and configure a database server seemed unreasonable for a coursework project.

I did consider just using in-memory data structures (HashMaps, basically), but that loses data between restarts and doesn't demonstrate any database skills. SQLite is a real database without the operational overhead.

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
├── adapter/persistence/  - SQLite repository implementations
└── config/               - DB connection + dependency injection

com.digitalid.presentation
└── console/        - Console UIs for each organisation type
```

### Naming Conventions

Standard Java conventions. Classes in PascalCase, methods in camelCase, enum values in UPPER_SNAKE_CASE, packages lowercase. Nothing unusual here.

---

## Design Decisions

### Architecture

I went with a layered architecture enhanced with ports and adapters. Pure hexagonal felt like overkill for a console-only app — there's only one adapter type (console in, SQLite out), so the full hexagonal ceremony doesn't pay for itself. But I still wanted the testability that ports give you, so the hybrid approach made sense.

If this were a production system with a REST API, a message queue, and maybe a GraphQL layer, full hexagonal would be worth it. For now, layers + ports at the boundaries gives me what I need without overcomplicating things.

### Tool-Based Access Control

Instead of a traditional role/permission matrix, each organisation has a list of tool enums in its profile. The registry checks this list before handing out use case instances. I find this more readable than scattered permission checks — you look at an org's profile and immediately see what it can do. The enums also mean typos get caught at compile time rather than runtime.

The downside is it's slightly less granular than a full permission system (you can't do "this org can UPDATE_WORKER_ID but only for workers in their region" without additional logic in the use case itself). For this project's scope, that's fine.

### SQLite

Covered above in Database Design. Basically: real database, zero operational overhead, assessors don't need to install anything.

### Multi-Region Support

Food service is genuinely global, and I wanted to show the system can handle complexity beyond a single jurisdiction. 12 regions with different certification types, validity periods, and naming conventions. Could've kept it US-only to save time, but the regional handling is where a lot of the interesting business logic lives.

---

## Security & Authorisation

### Access Control

Three layers of enforcement:

1. **Presentation** — menus only show tools the org is allowed to use. Users don't see options they can't access.
2. **Application (Registry)** — even if someone bypasses the UI, the registry checks the org profile before returning a use case. Throws UnauthorisedAccessException if denied.
3. **Domain** — use cases can perform additional validation (e.g., checking a worker belongs to the org's region). Business rules enforced regardless of who's calling.

### Audit Trail

Every action gets logged — what was done, to which entity, by whom, when. The audit log is append-only from the application's perspective. It's there for compliance (health inspections need to see who verified what) and for debugging.

---

## Data Flow Patterns

### Creating a Worker (example)

1. Console collects worker details, builds a CreateWorkerRequest
2. Console asks registry for CREATE_WORKER_ID tool
3. Registry checks org profile, returns CreateWorkerIdUseCase
4. Use case validates the request
5. Domain service enforces business rules, creates Worker entity
6. Repository persists to SQLite
7. Audit log records the action
8. Use case returns the created worker
9. Console displays confirmation

### Verifying a Worker

1. Console collects worker ID
2. Gets appropriate verification use case from registry
3. Use case retrieves worker from repository
4. Domain logic determines result based on verification type
5. Result returned and displayed

Data always flows through layers in order — presentation to application to domain to infrastructure and back. No skipping.

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

SQLite implementations handle connection management, SQL construction, and mapping rows to domain objects. Prepared statements throughout to prevent SQL injection.

### Use Cases

Each use case is one class, one business operation. Implements a UseCase interface, takes a request object and org context, returns a result. Responsibilities: validate input, check authorisation (via registry), invoke domain logic, call repositories, log to audit trail, return result.

I considered having use cases do less (just coordination) with all logic in domain services. In practice, some operations are simple enough that a separate domain service would just be an unnecessary indirection. I'll probably keep thinner use cases for complex operations and let simpler ones handle their own logic. Might revisit this if it gets messy.

---

## Validation & Error Handling

### Validation

Happens at multiple levels but with different concerns:

- **Presentation:** format checking, parsing. "Is this a valid email string?" Gives user-friendly messages.
- **Application:** request completeness, preliminary business checks. "Is this region supported?"
- **Domain:** real invariant enforcement. "Can a revoked worker be updated?" — no. "Is issue date before expiration?" etc.

Some specific rules: worker name can't be empty, DOB must be in the past, email must be valid format. Certification issue dates must precede expiration dates. You can activate a suspended worker but not a revoked one. Status changes always require an audit log entry.

### Exception Hierarchy

I'm keeping this fairly simple:

- **Domain:** InvalidOperationException (business rule violations), WorkerNotFoundException, CertificationExpiredException
- **Application:** UnauthorisedAccessException, ValidationException
- **Infrastructure:** DatabaseException, ConnectionException

Exceptions propagate upward — infrastructure throws, application catches and wraps if needed, presentation catches and shows a human-readable message. No swallowing exceptions silently.

---

## Testing

### Structure

Tests mirror the source packages. Domain tests don't need any mocking — pure logic, fast execution. Application tests mock the repositories (that's the whole point of the port interfaces). Infrastructure tests use a separate test database file. Integration tests wire everything together with a real SQLite database.

I'm planning to use test fixture builders for creating realistic test data, and reset the database between test runs so they don't depend on each other.

Haven't decided yet whether presentation tests are worth the effort — testing console I/O is fiddly. Might just cover it through integration tests that exercise the full flow.

---

**Document Status:** Working draft — will update as implementation progresses
**Audience:** Myself + assessors
