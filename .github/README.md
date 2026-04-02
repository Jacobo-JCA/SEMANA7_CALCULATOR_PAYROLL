# ASDD Framework — Guía de Uso (GitHub Copilot)

**ASDD** (Agent Spec Software Development) es un framework de desarrollo asistido por IA que organiza el trabajo de software en fases orquestadas por agentes especializados.

```
Requerimiento → Spec → [Backend ∥ DB] → [Unit Tests] → QA → Doc (opcional)
```

> Esta guía cubre el uso con **GitHub Copilot Chat** en VS Code.

---

## Requisitos

| Requisito | Detalle |
|---|---|
| VS Code | Cualquier versión reciente |
| GitHub Copilot Chat | Extensión instalada y activa |
| Setting habilitado | `github.copilot.chat.codeGeneration.useInstructionFiles: true` |

---

## Onboarding — nuevo proyecto

Al copiar `.github/` a un proyecto nuevo, completa estos archivos **en orden** antes de usar cualquier agente:

| # | Archivo | Qué escribir |
|---|---------|-------------|
| 1 | `README.md` (raíz del proyecto) | Stack, arquitectura, comandos (`build`, `test`, `run`), variables de entorno |
| 2 | `.github/instructions/backend.instructions.md` | Microservicios, paquetes, DB, messaging |
| 3 | `.github/copilot-instructions.md` → Diccionario de Dominio | Términos canónicos del negocio (glosario) |

Una vez completados, los agentes tienen todo el contexto para operar de forma autónoma.

**No modificar**: `agents/`, `skills/`, `docs/lineamientos/`, `AGENTS.md`

---

## El flujo ASDD paso a paso

### Paso 1 — Spec (obligatorio, siempre primero)

Genera la especificación técnica antes de escribir código:

```
@Spec Generator genera la spec para: [tu requerimiento]
```
```
/generate-spec <nombre-feature>
```

El agente valida el requerimiento y genera `specs/<feature>.spec.md` con estado `DRAFT`.
Revisa y aprueba la spec (cambia a `APPROVED`) antes de continuar.

---

### Paso 2 — Implementación (paralelo)

Con la spec `APPROVED`, lanza backend y base de datos en paralelo:

```
@Backend Developer implementa specs/<feature>.spec.md
@Database Agent diseña el modelo de datos para specs/<feature>.spec.md
```

O con el Orchestrator para coordinar todo automáticamente:
```
@Orchestrator ejecuta el flujo completo para: [tu requerimiento]
```

> **Database Agent** solo es necesario si hay cambios en el modelo de datos.

---

### Paso 3 — Unit Tests

Con la implementación completa, genera los tests unitarios:

```
@Test Engineer Backend genera tests para specs/<feature>.spec.md
```
```
/unit-testing <nombre-feature>
```

---

### Paso 4 — QA

Con tests completos, ejecuta la estrategia QA:

```
@QA Agent ejecuta QA para specs/<feature>.spec.md
```

El agente genera: casos Gherkin, matriz de riesgos y (si hay SLAs) plan de performance.

---

### Paso 5 — Documentación *(opcional)*

Al cerrar el feature:

```
@Documentation Agent documenta el feature specs/<feature>.spec.md
```

---

## Agentes disponibles (`@nombre` en Copilot Chat)

| Agente | Fase | Cuándo usarlo |
|---|---|---|
| `@Orchestrator` | Entry point | Coordinar el flujo completo |
| `@Spec Generator` | Fase 1 | Validar un requerimiento y generar su spec técnica |
| `@Backend Developer` | Fase 2 ∥ | Implementar el backend según la spec |
| `@Database Agent` | Fase 2 ∥ | Diseñar entidades JPA, migrations y seeders |
| `@Test Engineer Backend` | Fase 3 | Generar tests unitarios para el backend |
| `@QA Agent` | Fase 4 | Gherkin, riesgos y análisis de performance |
| `@Documentation Agent` | Fase 5 | README, API docs y ADRs |

---

## Skills disponibles (`/comando` en Copilot Chat)

| Comando | Agente | Qué hace |
|---|---|---|
| `/asdd-orchestrate` | Orchestrator | Orquesta el flujo completo o muestra estado actual |
| `/generate-spec` | Spec Generator | Genera spec técnica con validación INVEST |
| `/implement-backend` | Backend Developer | Implementa feature completo en el backend |
| `/unit-testing` | Test Engineer Backend | Genera suite de tests unitarios |
| `/gherkin-case-generator` | QA Agent | Flujos críticos + casos Given-When-Then + datos de prueba |
| `/risk-identifier` | QA Agent | Matriz de riesgos ASD (Alto/Medio/Bajo) |
| `/automation-flow-proposer` | QA Agent | Propone flujos a automatizar con estimación de ROI |
| `/performance-analyzer` | QA Agent | Planifica pruebas de carga y performance |

---

## Instructions automáticas (sin intervención manual)

Inyectadas automáticamente por Copilot cuando el archivo activo coincide:

| Archivo activo | Instructions aplicadas |
|---|---|
| `**/src/main/java/**/*.java` | `instructions/backend.instructions.md` |
| `**/src/test/java/**/*.java` | `instructions/tests.instructions.md` |

---

## Lineamientos de referencia

Cargados automáticamente por los agentes:

| Documento | Contenido |
|---|---|
| `.github/docs/lineamientos/dev-guidelines.md` | Clean Code, SOLID, API REST, Seguridad, Observabilidad |
| `.github/docs/lineamientos/qa-guidelines.md` | Estrategia QA, Gherkin, Riesgos, Automatización, Performance |
| `.github/docs/lineamientos/guidelines.md` | Referencia rápida de estándares: código, tests, API, Git |

---

## Estructura de carpetas

```
Project Root/
│
├── employee-service/                ← Microservicio de gestión de empleados
│   └── src/main/java/com/nomina/employee/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       ├── exception/
│       ├── config/
│       └── infrastructure/messaging/
│
├── payroll-service/                 ← Microservicio de cálculo de nómina
│   └── src/main/java/com/nomina/payroll/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       ├── exception/
│       ├── config/
│       └── infrastructure/messaging/
│
├── docs/output/                     ← artefactos generados por los agentes
│   ├── qa/                          ← Gherkin, riesgos, performance
│   ├── api/                         ← documentación de API
│   └── adr/                         ← Architecture Decision Records
│
└── .github/                         ← framework Copilot (auto-contenido)
    ├── README.md                    ← este archivo
    ├── AGENTS.md                    ← reglas críticas para todos los agentes
    ├── copilot-instructions.md      ← siempre activo en Copilot Chat
    │
    ├── agents/                      ← agentes (@nombre en Copilot Chat)
    ├── skills/                      ← skills (/comando en Copilot Chat)
    ├── docs/lineamientos/           ← guidelines del framework
    ├── prompts/                     ← prompts rápidos reutilizables
    ├── instructions/                ← aplicadas automáticamente por contexto
    ├── requirements/                ← requerimientos de negocio (input)
    └── specs/                       ← specs técnicas (fuente de verdad)
```

---

## Reglas de Oro

1. **No código sin spec aprobada** — siempre debe existir `specs/<feature>.spec.md` con estado `APPROVED`.
2. **No código no autorizado** — los agentes no generan ni modifican código sin instrucción explícita.
3. **No suposiciones** — si el requerimiento es ambiguo, el agente pregunta antes de actuar.
4. **Transparencia** — el agente explica qué va a hacer antes de hacerlo.
