# Digital ID Management System

A console-based backend system for managing digital identities in a federated ecosystem.

## Architecture

Clean/Hexagonal Architecture with 4 layers:
- **Domain Layer**: Core business logic and entities
- **Application Layer**: Use cases and workflows
- **Infrastructure Layer**: Technical implementations
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
