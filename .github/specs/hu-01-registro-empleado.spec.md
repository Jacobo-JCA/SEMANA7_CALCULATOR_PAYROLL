---
id: SPEC-001
status: IMPLEMENTED
feature: registro-empleado
created: 2026-03-25
updated: 2026-03-25
author: spec-generator
version: "1.1"
related-specs: []
---

# Spec: Registro de Empleado

> **Estado:** `IMPLEMENTED`
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción
Permite al administrador de recursos humanos registrar los datos básicos de un empleado (nombre, tipo de contrato y salario bruto) en el sistema. Al finalizar, el sistema publica un evento de dominio asíncrono para notificar la creación a otros microservicios.

### Requerimiento de Negocio
Como administrador de recursos humanos, quiero registrar los datos básicos del empleado para contar con la información necesaria antes de iniciar el cálculo de nómina.

### Historias de Usuario

#### HU-01: Registro de empleado

```text
Como:        Administrador de recursos humanos
Quiero:      Registrar los datos básicos del empleado
Para:        Contar con la información necesaria antes de iniciar el cálculo de nómina

Prioridad:   Alta
Estimación:  S
Dependencias: Ninguna
Microservicio: employee-service
```

#### Criterios de Aceptación — HU-01

**Happy Path**
```gherkin
CRITERIO-1.1: Registro exitoso del empleado
  Dado que:  no existe el empleado en el sistema
  Cuando:    se registra un empleado con sus datos necesarios (nombre, tipo de contrato y salario bruto)
  Entonces:  el empleado queda guardado en el sistema
  Y:         queda habilitado para confirmar su nómina
  Y:         el sistema publica el evento de creación en el broker de mensajes
```

**Error Path — Datos faltantes**
```gherkin
CRITERIO-1.2: Registro fallido por falta de datos necesarios
  Dado que:  no existe el empleado en el sistema
  Cuando:    se intenta registrar un empleado sin ingresar datos necesarios
  Entonces:  el sistema no permite guardar el empleado
  Y:         notifica cuáles son los datos necesarios (HTTP 400)
```

**Error Path — Salario inválido**
```gherkin
CRITERIO-1.3: Registro fallido por salario bruto inválido
  Dado que:  el empleado no existe en el sistema
  Cuando:    se intenta registrar al empleado con un salario bruto negativo o en cero
  Entonces:  el sistema no permite guardar el empleado
  Y:         notifica que el salario bruto debe ser positivo (HTTP 400)
```

**Error Path — Caracteres en nombre**
```gherkin
CRITERIO-1.4: Registro fallido por caracteres especiales en nombre
  Dado que:  el empleado no consta en el sistema
  Cuando:    se intenta registrar al empleado con un nombre que contiene caracteres especiales
  Entonces:  el sistema no permite guardarlo
  Y:         notifica que el nombre no debe contener caracteres especiales (HTTP 400)
```

### Reglas de Negocio
1. El nombre del empleado es obligatorio, máximo 100 caracteres, solo letras y espacios (sin caracteres especiales ni números).
2. El tipo de contrato es obligatorio y debe ser uno de: `FULL_TIME`, `PART_TIME`, `PROFESSIONAL_SERVICES`.
3. El salario bruto es obligatorio y debe ser un valor positivo (mayor que cero).
4. No hay autenticación: cualquier petición válida puede registrar un empleado.
5. Tras el guardado exitoso en base de datos, se debe emitir un evento a la cola de RabbitMQ para indicar la creación del empleado.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas
| Entidad | Almacén | Cambios | Microservicio | Descripción |
|---------|---------|---------|---------------|-------------|
| `Employee` | tabla `employees` | nueva | employee-service | Registro del empleado con datos básicos |

#### Campos del modelo (JPA Entity)
| Campo | Tipo Java | Columna DB | Obligatorio | Validación | Descripción |
|-------|-----------|------------|-------------|------------|-------------|
| `id` | Long | `id` (PK, auto) | sí | auto-generado | Identificador único |
| `name` | String | `name` | sí | `@NotBlank`, `@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")`, max 100 | Nombre completo |
| `contractType` | ContractType (enum) | `contract_type` | sí | `@NotNull` | Tipo de contrato |
| `grossSalary` | BigDecimal | `gross_salary` | sí | `@NotNull`, `@Positive` | Salario bruto mensual |
| `createdAt` | LocalDateTime | `created_at` | sí | `@CreatedDate` | Timestamp creación |
| `updatedAt` | LocalDateTime | `updated_at` | sí | `@LastModifiedDate` | Timestamp actualización |

#### Enum ContractType
```java
public enum ContractType {
    FULL_TIME,
    PART_TIME,
    PROFESSIONAL_SERVICES
}
```

### API Endpoints

#### POST /api/v1/employees
- **Microservicio**: employee-service
- **Descripción**: Registra un nuevo empleado
- **Request Body**:
  ```json
  {
    "name": "Juan Pérez",
    "contractType": "FULL_TIME",
    "grossSalary": 2500.00
  }
  ```
- **Response 201**:
  ```json
  {
    "id": 1,
    "name": "Juan Pérez",
    "contractType": "FULL_TIME",
    "grossSalary": 2500.00,
    "createdAt": "...T10:00:00",
    "updatedAt": "...T10:00:00"
  }
  ```

### Comunicación Asíncrona (RabbitMQ)

El `employee-service` publicará eventos de dominio para notificar el registro de un nuevo empleado, siguiendo un modelo de comunicación guiado por eventos (Event-Driven Architecture).

**Configuración de Infraestructura:**
- **Exchange:** `employee.exchange` (Topic o Direct)
- **Routing Key:** `employee.created.key`
- **Queue:** `employee.created.queue`

**Contrato del Evento (EmployeeCreatedEvent):**
```json
{
  "employeeId": 1,
  "name": "Juan Pérez",
  "contractType": "FULL_TIME",
  "grossSalary": 2500.00,
  "timestamp": "2026-03-25T10:00:00"
}
```

### Notas de Implementación
- Usar Bean Validation para todas las validaciones de entrada (`@Pattern`, `@Positive`, etc).
- `GlobalExceptionHandler` para estructurar los errores 400 y listar los `details`.
- En la capa de infraestructura, crear el componente `EmployeeEventProducer` que inyecte un `RabbitTemplate` para emitir el evento en formato JSON (requerirá configurar `Jackson2JsonMessageConverter`).

---

## 3. LISTA DE TAREAS

### Backend (employee-service)

#### Implementación Rest & JPA
- [x] Crear enum `ContractType` con valores
- [x] Crear entity JPA `Employee` con `@EntityListeners(AuditingEntityListener.class)`
- [x] Crear DTO `EmployeeRequest` con validaciones
- [x] Crear DTO `EmployeeResponse`
- [x] Implementar `EmployeeRepository`
- [x] Implementar `EmployeeService` (logic save)
- [x] Implementar `EmployeeController` (endpoints)
- [x] Configurar `GlobalExceptionHandler` y excepciones custom

#### Implementación RabbitMQ (Agregado)
- [x] Implementar `RabbitMQConfig` (Declarar Exchange, Queue, Binding y MessageConverter)
- [x] Crear DTO de evento `EmployeeCreatedEvent` record
- [x] Implementar `EmployeeEventProducer` (Infraestructura)
- [x] Inyectar el producer en `EmployeeService` y disparar evento al guardar (`create()`)

#### Tests Unitarios
- [x] Tests del Service para JPA save
- [x] Tests del Controller para bean validation
- [x] **NUEVO:** Actualizar `EmployeeServiceTest` para mockear `EmployeeEventProducer` y hacer un `verify` sobre el llamado.

### QA (Omitido por requerimiento del usuario)
- [ ] Ejecutar skill `/gherkin-case-generator`
- [x] Actualizar estado spec: `status: IMPLEMENTED` (pendiente parcheo de rabbitmq)
