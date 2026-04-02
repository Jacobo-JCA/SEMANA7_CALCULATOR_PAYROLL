---
description: 'Ejecuta el Database Agent para diseñar esquemas de datos, generar entidades JPA, migrations con Flyway/Liquibase y seeders a partir de la spec aprobada.'
agent: Database Agent
---

Ejecuta el Database Agent para diseñar y gestionar el modelo de persistencia del feature.

**Feature**: ${input:featureName:nombre del feature en kebab-case}

**Instrucciones para @Database Agent:**

1. Lee `.github/instructions/backend.instructions.md` — confirma el motor de BD aprobado (PostgreSQL)
2. Lee `.github/docs/lineamientos/dev-guidelines.md`
3. Lee la **Sección 2 — DISEÑO — Modelos de Datos** de `.github/specs/${input:featureName}.spec.md`
4. Escanea entidades JPA y repositorios existentes en `src/main/java/com/nomina/<servicio>/entity/` y `repository/`
5. Ejecuta el flujo completo:
   - Diseña o actualiza el esquema de datos (entidades, campos, índices)
   - Genera entidad JPA: `entity/<Feature>.java`
   - Genera migración SQL si aplica
   - Genera data.sql con datos de prueba sintéticos (si aplica)
   - Registra ADR si hay decisiones de diseño relevantes
6. Presenta reporte consolidado de cambios al modelo de datos

**Prerequisito:** Debe existir `.github/specs/${input:featureName}.spec.md` con estado APPROVED y Sección 2 completa. Si no, ejecutar `/generate-spec` primero.

**Nota:** Ejecutar ANTES o en paralelo con el Backend Developer para que los contratos de persistencia estén definidos antes de implementar los repositorios.
