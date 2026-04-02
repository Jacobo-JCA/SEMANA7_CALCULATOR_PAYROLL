# Specs — Fuente de Verdad del Proyecto ASDD

Este directorio contiene las especificaciones técnicas de cada funcionalidad. Son la fuente de verdad para todos los agentes de desarrollo.

## Ciclo de Vida

```
DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED
```

| Estado | Quién | Condición |
|--------|-------|-----------e|
| `DRAFT` | spec-generator | Spec generada, pendiente de revisión humana |
| `APPROVED` | Usuario / Tech Lead | Revisada y aprobada — verde para implementar |
| `IN_PROGRESS` | orchestrator | Implementación en curso |
| `IMPLEMENTED` | orchestrator | Código + tests + QA completos |
| `DEPRECATED` | Usuario | Descartada o reemplazada por otra spec |

> **Regla:** Sin `status: APPROVED` en el frontmatter → ningún agente implementa código.

## Convención de Nombres

```
.github/specs/<nombre-feature-en-kebab-case>.spec.md
```

## Índice de Specs

| ID | Feature | Archivo | Estado | Microservicio | Fecha |
|----|---------|---------|--------|---------------|-------|
| SPEC-001 | Registro de Empleado | `hu-01-registro-empleado.spec.md` | DRAFT | employee-service | 2026-03-25 |
| SPEC-002 | Cálculo de Salario Neto | `hu-03-calculo-salario.spec.md` | DRAFT | payroll-service | 2026-03-25 |
| SPEC-003 | Confirmación del Resultado | `hu-04-confirmacion-resultado.spec.md` | DRAFT | payroll-service | 2026-03-25 |

> Actualizar esta tabla cada vez que se crea o cambia el estado de una spec.

## Requerimientos pendientes de spec

Los siguientes requerimientos están en `.github/requirements/` listos para convertirse en spec:

| Requerimiento | Archivo | Acción |
|---------------|---------|--------|
| *(sin requerimientos pendientes)* | — | — |

## Cómo crear una spec nueva

**Opción 1 — Desde un requerimiento existente:**
```
/generate-spec <nombre-feature>
```

**Opción 2 — Desde cero:**
```
/generate-spec
> Descripción del feature: ...
```

**Opción 3 — Orquestación completa (spec → implementación → tests → QA):**
```
/asdd-orchestrate
> Feature: nombre del feature
```

## Frontmatter requerido en toda spec

```yaml
---
id: SPEC-001
status: DRAFT
feature: nombre-del-feature
created: YYYY-MM-DD
updated: YYYY-MM-DD
author: spec-generator
version: "1.0"
related-specs: []
---
```

## Template

Ver `.github/skills/generate-spec/spec-template.md`
