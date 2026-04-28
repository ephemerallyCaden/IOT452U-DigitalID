# Food Service Digital ID Management System

A console-based backend system for managing digital worker identities and food safety certifications across a federated ecosystem of organisations.

## Architecture

Layered Architecture with Ports and Adapters - 4 Layers:
- **Domain Layer**: Core business logic and entities
- **Application Layer**: Use cases, workflows, and port interfaces
- **Infrastructure Layer**: Technical implementations (adapters)
- **Presentation Layer**: Console interface

## Project Structure

```
src/
├── main/java/com/digitalid/
│   ├── domain/              # Core business logic
│   ├── application/         # Use cases
│   ├── infrastructure/      # Technical implementations
│   └── presentation/        # Console interface
└── test/java/com/digitalid/ # Tests
```

## Build

```bash
mvn clean compile
mvn test
```
