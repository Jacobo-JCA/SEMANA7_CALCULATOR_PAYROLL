---
id: SPEC-004
status: DRAFT
feature: correccion-datos
created: 2026-03-25
updated: 2026-03-25
author: spec-generator
version: "1.0"
related-specs: [SPEC-001, SPEC-002]
---

# Spec: Corrección de Datos

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción
Permite al administrador de recursos humanos corregir los datos de un empleado (nombre y salario bruto) siempre y cuando el cálculo de su nómina no se haya llevado a cabo aún por el `payroll-service`.

### Requerimiento de Negocio
Como administrador de recursos humanos, quiero corregir los datos de un empleado antes de confirmar el cálculo para asegurarme de que la información es correcta antes de obtener el resultado.

### Historias de Usuario

#### HU-02: Corrección de datos

```text
Como:        Administrador de recursos humanos
Quiero:      Corregir los datos de un empleado antes de confirmar el cálculo
Para:        Asegurarme de que la información es correcta antes de obtener el resultado

Prioridad:   Alta
Estimación:  M
Dependencias: HU-01, HU-03
Microservicio: employee-service (con consulta a payroll-service)
```

### Criterios de Aceptación — HU-02

**Happy Path — Corrección exitosa del nombre**
```gherkin
CRITERIO-2.1: Corrección exitosa del nombre del empleado
  Dado que:  el empleado esta registrado con los datos necesarios (nombre, tipo de contrato y sueldo bruto)
  Y:         la nómina no fue confirmada (calculada)
  Cuando:    se cambia el nombre del empleado
  Entonces:  los datos del empleado quedan actualizados
```

**Happy Path — Corrección exitosa del salario**
```gherkin
CRITERIO-2.2: Corrección exitosa del salario bruto del empleado
  Dado que:  el empleado esta registrado
  Y:         la nómina del empleado no fue confirmada (calculada)
  Cuando:    se actualiza el salario bruto con un valor positivo valido
  Entonces:  los datos quedan actualizados con el nuevo valor
```

**Error Path — Nombre inválido**
```gherkin
CRITERIO-2.3: Corrección errónea por nombre inválido
  Dado que:  el empleado esta registrado
  Y:         la nómina del empleado no fue confirmada (calculada)
  Cuando:    se intenta cambiar el nombre del empleado por uno que contiene caracteres especiales
  Entonces:  los datos del empleado no quedan actualizados
  Y:         el sistema informa que el nombre no puede tener caracteres especiales
```

**Error Path — Salario inválido**
```gherkin
CRITERIO-2.4: Corrección fallida por salario bruto inválido
  Dado que:  existe un empleado registrado con sus datos
  Y:         la nómina del empleado no fue confirmada (calculada)
  Cuando:    se intenta actualizar el salario bruto con un valor negativo
  Entonces:  los datos del empleado permanecen sin cambios
  Y:         el sistema informa que el salario bruto tiene que ser positivo
```

**Error Path — Nómina ya calculada**
```gherkin
CRITERIO-2.5: Bloqueo de corrección por nómina ya calculada
  Dado que:  el empleado esta registrado
  Y:         la nómina del empleado fue calculada
  Cuando:    el administrador intenta modificar datos del empleado
  Entonces:  el sistema no lo permite
  Y:         informa que los datos no pueden modificarse porque la nómina ya fue calculada
```

### Reglas de Negocio
1. Solo se pueden corregir el nombre y el salario bruto. El contrato en esta historia asume que es implícito o no se menciona como bloqueado, pero aplicaremos la misma validación base que en creación.
2. Validaciones de esquema: nombre sin caracteres especiales, salario bruto siempre mayor a 0.
3. El `employee-service` debe rechazar la modificación si ya existe un cálculo de nómina asociado en el `payroll-service`. (Para esto, se debe verificar mediante el ID del empleado).

---

## 2. DISEÑO

### Modelos de Datos

Se reutiliza la entidad `Employee` existente en `employee-service`.

### API Endpoints

#### PUT /api/v1/employees/{id}
- **Microservicio**: employee-service
- **Descripción**: Actualiza los datos de un empleado si la nómina no ha sido calculada.
- **Request Body** (`EmployeeRequest`):
  ```json
  {
    "name": "Juan Perez Modificado",
    "contractType": "FULL_TIME",
    "grossSalary": 3000.00
  }
  ```
- **Response 200**:
  ```json
  {
    "id": 1,
    "name": "Juan Perez Modificado",
    "contractType": "FULL_TIME",
    "grossSalary": 3000.00,
    "updatedAt": "..."
  }
  ```
- **Response 400**:
  - Validaciones fallidas (salario negativo, nombre con símbolos).
  - Empleado bloqueado: "Los datos no pueden modificarse porque la nómina ya fue calculada"
- **Response 404**:
  - Empleado no encontrado.

### Comunicación entre microservicios

El `employee-service` debe poder consultar si existe nómina para un empleado en el `payroll-service`. 
Se propone una de dos opciones (diseño asincrónico preferido o sincrónico para bloqueo):
- **Sincrónico:** Un nuevo `PayrollClient` en `employee-service` que haga un GET a `http://localhost:8082/api/v1/payroll/employee/{id}`. Si retorna algo, se bloquea la edición.
- **Evento:** Para alinearse con las instrucciones, el bloqueo es inmediato, se requerirá consulta sincrónica (`RestTemplate` o `WebClient`) de `employee-service` a `payroll-service`.

---

## 3. LISTA DE TAREAS

### Backend (employee-service y payroll-service)

#### Implementación
- [ ] Implementar un nuevo endpoint abierto en `payroll-service`: `GET /api/v1/payroll/employee/{employeeId}` (si no existe, retorna 404 o lista vacía).
- [ ] En `employee-service`, crear `PayrollClient` en la capa de `infrastructure/client` usando `RestTemplate` para verificar si existe nómina.
- [ ] En `employee-service`, agregar endpoint `PUT /api/v1/employees/{id}` en `EmployeeController`.
- [ ] En `employee-service`, extender lógica en `EmployeeService.update(id, request)` validando con `PayrollClient`.
- [ ] Agregar validaciones en DTO (reutilizando `EmployeeRequest`).
- [ ] Agregar exception `EmployeeAlreadyCalculatedException` y configurar en `GlobalExceptionHandler` con error 400.

#### Tests Unitarios
- [ ] `should_updateEmployee_when_validDataAndNoPayroll`
- [ ] `should_throwException_when_payrollAlreadyCalculated`
- [ ] `should_throwException_when_salarioNegativo` (revisar que reusen las validaciones de POST)
- [ ] `should_throwException_when_nombreCaracteresEspeciales`

### QA
- [ ] Ejecutar skill `/gherkin-case-generator` → CRITERIO-2.1 a 2.5
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
