# ASDD — Agent Spec-Driven Development

Framework de desarrollo asistido por IA que transforma requerimientos en código funcional mediante agentes especializados orquestados. Garantiza calidad y trazabilidad a través de especificaciones técnicas aprobadas antes de cualquier implementación.

```
Requerimiento → Spec → [Backend ∥ DB] → [Unit Tests] → QA → Docs
```

---

## Stack Tecnológico

| Componente | Tecnología |
|-----------|------------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.x |
| Build | Gradle (Groovy DSL) |
| Base de Datos | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Mensajería | RabbitMQ (Spring AMQP) |
| Tests | JUnit 5 + Mockito |
| Documentación API | SpringDoc OpenAPI (Swagger UI) |
| Autenticación | No aplica |

## Arquitectura

Microservicios backend con arquitectura en capas y comunicación asíncrona:

```
┌──────────────────┐         RabbitMQ         ┌──────────────────┐
│ employee-service │ ─────────────────────► │ payroll-service  │
│   (puerto 8081)  │                          │   (puerto 8082)  │
└──────────────────┘                          └──────────────────┘
```

### Estructura por microservicio

```
com.nomina.<servicio>/
├── controller/        ← REST Controllers
├── service/           ← Lógica de negocio
├── repository/        ← Spring Data JPA
├── entity/            ← Entidades JPA
├── dto/               ← Request/Response
├── exception/         ← Excepciones de dominio
├── config/            ← Configuraciones
└── infrastructure/    ← RabbitMQ (separado del dominio)
    └── messaging/
```

## Compatibilidad

| Herramienta | Configuración | Carpeta de agentes |
|-------------|---------------|-------------------|
| **GitHub Copilot** | `.github/copilot-instructions.md` | `.github/agents/` |

## Instalación

### GitHub Copilot

1. Instala la extensión **GitHub Copilot Chat** en VS Code
2. Activa el uso de instruction files en tu settings.json de VS Code:

```json
{
  "github.copilot.chat.codeGeneration.useInstructionFiles": true
}
```

3. Copia `.github/` a la raíz de tu proyecto

---

## Comandos de Desarrollo

```bash
# Compilar
./gradlew compileJava

# Ejecutar tests
./gradlew test

# Ejecutar microservicio
./gradlew bootRun

# Build completo
./gradlew build
```

## Variables de Entorno

```properties
# PostgreSQL (cada microservicio)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/employee_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# RabbitMQ
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
```

---

## Flujo de trabajo

```bash
# 1. Escribe el requerimiento
echo "HU-01: Registro de empleado" > .github/requirements/registro-empleado.md

# 2. Genera la spec
/generate-spec registro-empleado

# 3. Abre .github/specs/hu-01-registro-empleado.spec.md, revisa y cambia:
#    status: DRAFT  →  status: APPROVED

# 4. Orquesta la implementación
/asdd-orchestrate registro-empleado

# → Backend implementado
# → Tests generados
# → Análisis QA completado
```

> **Regla de Oro**: Ningún agente escribe código si la spec no tiene `status: APPROVED`.

---

## Skills disponibles

| Comando | Qué hace |
|---------|----------|
| `/asdd-orchestrate` | Orquesta el flujo ASDD completo |
| `/generate-spec` | Genera spec técnica en `.github/specs/` |
| `/implement-backend` | Implementa el backend según la spec aprobada |
| `/unit-testing` | Genera tests unitarios (JUnit 5 + Mockito) |
| `/gherkin-case-generator` | Genera escenarios Given-When-Then y datos de prueba |
| `/risk-identifier` | Clasifica riesgos de calidad (Alto / Medio / Bajo) |
| `/automation-flow-proposer` | Propone flujos a automatizar con análisis de ROI |
| `/performance-analyzer` | Define estrategia de performance testing con k6 |

---

## Agentes disponibles

| Agente | Fase | Responsabilidad |
|--------|------|-----------------|
| `orchestrator` | Entry point | Coordina el flujo completo |
| `spec-generator` | 1 | Genera especificaciones técnicas |
| `backend-developer` | 2 | Controllers, services, repositories |
| `database-agent` | 2 | Entidades JPA, migrations |
| `test-engineer-backend` | 3 | Tests unitarios JUnit 5 + Mockito |
| `qa-agent` | 4 | Estrategia QA, Gherkin, riesgos, performance |
| `documentation-agent` | 5 | README, API docs, ADRs |

---

## Documentación interna

- `.github/README.md` — Guía detallada para GitHub Copilot
- `.github/AGENTS.md` — Reglas de Oro y lineamientos de todos los agentes
- `.github/specs/README.md` — Convenciones y ciclo de vida de specs
