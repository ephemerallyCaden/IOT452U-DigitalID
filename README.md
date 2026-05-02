# Food Service Digital ID Management System

A console-based backend system for managing digital worker identities and food safety certifications across a federated ecosystem of organisations.

```
┌────────────────────────────────────────────────────┐
│  Food Service Digital ID - Select Organisation     │
└────────────────────────────────────────────────────┘

  ▶ Central Authority Service
    Financial Service
    Fast Food Service
    Fine Dining Service
    Delivery Service
    Coffee Shop Service
    Street Vendor Service

  ↑/↓ navigate · Enter select · Q quit
```

## GitHub Repository

https://github.com/ephemerallyCaden/IOT452U-DigitalID

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Build and Run
```bash
mvn clean compile exec:java -Dexec.mainClass="com.digitalid.Application"
```

### Run Tests
```bash
mvn test
```

## System Structure

```
src/main/java/com/digitalid/
├── domain/              # Core business logic, entities, enums
│   ├── model/           # Worker, Certification, Region, OrganisationType
│   ├── service/         # Validation and verification services
│   └── exception/       # Domain-specific exceptions
├── application/         # Use cases and port interfaces
│   ├── usecase/         # 26 use case implementations
│   ├── port/in/         # Inbound port (UseCase interface)
│   ├── port/out/        # Outbound ports (repository interfaces)
│   ├── registry/        # Tool-based access control
│   └── request/         # Request objects
├── infrastructure/      # JSON persistence and configuration
│   ├── adapter/persistence/  # Repository implementations
│   └── config/               # DI, data seeding, DB connection
└── presentation/        # Console UI per organisation type
    └── console/         # JLine-based terminal menus
```

## Architecture

Layered architecture with ports and adapters. Four layers with dependencies pointing inward:

- **Presentation** → Console menus with JLine arrow-key navigation
- **Application** → Use cases, access control registry, port interfaces
- **Domain** → Entities, business rules, validation (no external dependencies)
- **Infrastructure** → JSON file persistence via Gson

## Key Features

- 7 organisation types with role-based tool access
- 26 use cases covering identity management, certification, verification, reporting etc.
- 12 regions with region-specific certification requirements
- Full audit trail for all operations
- Arrow-key navigable terminal UI

