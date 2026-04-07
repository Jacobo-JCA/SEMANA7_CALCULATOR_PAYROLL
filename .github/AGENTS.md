# AGENTS.md — Payroll Calculator

Project-specific guidance. For ASDD framework, see `.github/README.md`.

## Services & Ports

| Service | Port | Database | Notes |
|---------|------|----------|-------|
| `frontend` | 5173 | - | React + Vite |
| `api-gateway` | 8080 | auth-db:5432 | Routes to backend services |
| `employee-service` | 8081 | employee-db:5432 (mapped 5433) | Publishes to RabbitMQ |
| `payroll-service` | 8082 | payroll-db:5432 (mapped 5434) | Consumes from RabbitMQ |

## Commands

```bash
# Docker (infrastructure)
docker-compose up -d

# Backend services (Maven wrapper)
cd employee-service && ./mvnw spring-boot:run
cd payroll-service && ./mvnw spring-boot:run

# Frontend
cd frontend && npm install && npm run dev

# Tests (per service)
cd <service> && ./mvnw test
```

## Critical Rules

1. **No implementation without approved spec** — check `.github/specs/<feature>.spec.md` (status: APPROVED)
2. **Backend is layered**: Controller → Service → Repository. RabbitMQ lives in `infrastructure/`, never in service layer
3. **Constructor injection only** — never use `new` for Spring beans
4. **No auth** — do not add authentication middleware
5. **Never commit secrets** — `.env` and credentials are gitignored

## Architecture Notes

- **Inter-service communication**: Async via RabbitMQ (NOT HTTP between services)
- **Database**: Separate PostgreSQL per service (do not share)
- **Frontend**: TypeScript + React 19 + TailwindCSS + Vite
- **Backend**: Java 21 + Spring Boot 3.x + Maven
