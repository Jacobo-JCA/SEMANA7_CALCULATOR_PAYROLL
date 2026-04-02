---
id: SPEC-###
status: DRAFT
feature: nombre-del-feature
created: YYYY-MM-DD
updated: YYYY-MM-DD
author: spec-generator
version: "1.0"
related-specs: []
---

# Spec: [Nombre de la Funcionalidad]

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción
Resumen de la funcionalidad en 2-3 oraciones. Qué hace, para quién y qué problema resuelve.

### Requerimiento de Negocio
El requerimiento original tal como fue proporcionado por el usuario (o copiado de `.github/requirements/<feature>.md`).

### Historias de Usuario

#### HU-01: [Título descriptivo corto]

```
Como:        [rol del usuario — ej. Administrador de RRHH]
Quiero:      [acción o funcionalidad concreta]
Para:        [valor o beneficio esperado por el negocio]

Prioridad:   Alta / Media / Baja
Estimación:  XS / S / M / L / XL
Dependencias: HU-X, HU-Y o Ninguna
Microservicio: employee-service / payroll-service / ambos
```

#### Criterios de Aceptación — HU-01

**Happy Path**
```gherkin
CRITERIO-1.1: [nombre del escenario exitoso]
  Dado que:  [contexto inicial válido]
  Cuando:    [acción del usuario]
  Entonces:  [resultado esperado verificable]
```

**Error Path**
```gherkin
CRITERIO-1.2: [nombre del escenario de error]
  Dado que:  [contexto inicial]
  Cuando:    [acción inválida o datos incorrectos]
  Entonces:  [manejo del error esperado con código HTTP y mensaje]
```

**Edge Case** *(si aplica)*
```gherkin
CRITERIO-1.3: [nombre del caso borde]
  Dado que:  [contexto de borde]
  Cuando:    [acción en el límite]
  Entonces:  [resultado esperado en el límite]
```

### Reglas de Negocio
1. Regla de validación (ej. "el campo X es obligatorio y no puede superar 100 caracteres")
2. Regla de integridad (ej. "el nombre debe ser único en la tabla")

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas
| Entidad | Almacén | Cambios | Microservicio | Descripción |
|---------|---------|---------|---------------|-------------|
| `FeatureEntity` | tabla `features` | nueva / modificada | employee-service / payroll-service | descripción del recurso |

#### Campos del modelo (JPA Entity)
| Campo | Tipo Java | Columna DB | Obligatorio | Validación | Descripción |
|-------|-----------|------------|-------------|------------|-------------|
| `id` | Long | `id` (PK, auto) | sí | auto-generado | Identificador único |
| `name` | String | `name` | sí | `@NotBlank`, max 100 | Nombre del recurso |
| `createdAt` | LocalDateTime | `created_at` | sí | auto-generado | Timestamp creación |
| `updatedAt` | LocalDateTime | `updated_at` | sí | auto-generado | Timestamp actualización |

#### Índices / Constraints
- Listar índices necesarios con su justificación de uso (búsqueda frecuente, unicidad, etc.)

### API Endpoints

#### POST /api/v1/[features]
- **Microservicio**: employee-service / payroll-service
- **Descripción**: Crea un nuevo recurso
- **Request Body**:
  ```json
  { "name": "string" }
  ```
- **Response 201**:
  ```json
  { "id": 1, "name": "string", "createdAt": "iso8601", "updatedAt": "iso8601" }
  ```
- **Response 400**: campo obligatorio faltante o inválido (Bean Validation)
- **Response 409**: ya existe un recurso con ese nombre

#### GET /api/v1/[features]
- **Microservicio**: employee-service / payroll-service
- **Descripción**: Lista todos los recursos
- **Response 200**:
  ```json
  [{ "id": 1, "name": "string", ... }]
  ```

#### GET /api/v1/[features]/{id}
- **Microservicio**: employee-service / payroll-service
- **Descripción**: Obtiene un recurso por id
- **Response 200**: recurso completo
- **Response 404**: no encontrado

### Comunicación Asíncrona (RabbitMQ)

> Solo completar si el feature involucra comunicación entre microservicios.

#### Eventos publicados
| Evento | Exchange | Routing Key | Productor | Payload |
|--------|----------|-------------|-----------|---------|
| `EmployeeReadyEvent` | `employee.exchange` | `employee.ready` | employee-service | `{ "employeeId": Long, "name": String, ... }` |

#### Eventos consumidos
| Evento | Queue | Consumidor | Acción |
|--------|-------|------------|--------|
| `EmployeeReadyEvent` | `payroll.calculate.queue` | payroll-service | Iniciar cálculo de nómina |

### Arquitectura y Dependencias
- Paquetes nuevos requeridos: ninguno / listar si aplica
- Servicios externos: RabbitMQ (broker de mensajería)
- Impacto en configuración: registrar beans, exchanges, queues si aplica

### Notas de Implementación
> Observaciones técnicas, decisiones de diseño o advertencias para los agentes de desarrollo.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación
- [ ] Crear entity JPA `[Feature]` con anotaciones `@Entity`, `@Table`
- [ ] Crear DTOs `[Feature]Request` y `[Feature]Response`
- [ ] Implementar `[Feature]Repository` (interface extends JpaRepository)
- [ ] Implementar `[Feature]Service` — lógica de negocio
- [ ] Implementar `[Feature]Controller` — endpoints REST
- [ ] Configurar excepciones de dominio + `GlobalExceptionHandler`
- [ ] Configurar RabbitMQ (exchange, queue, bindings) si aplica
- [ ] Implementar Producer/Consumer de eventos si aplica

#### Tests Unitarios
- [ ] `should_create[Feature]_when_validRequest` — happy path creación
- [ ] `should_throwException_when_invalidData` — error validación
- [ ] `should_throwException_when_notFound` — error not found
- [ ] `should_return[Feature]_when_exists` — happy path consulta

### QA
- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-1.1, 1.2, 1.3
- [ ] Ejecutar skill `/risk-identifier` → clasificación ASD de riesgos
- [ ] Revisar cobertura de tests contra criterios de aceptación
- [ ] Validar que todas las reglas de negocio están cubiertas
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
