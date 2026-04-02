---
id: SPEC-002
status: IMPLEMENTED
feature: calculo-salario
created: 2026-03-25
updated: 2026-03-25
author: spec-generator
version: "1.0"
related-specs: [SPEC-001]
---

# Spec: Cálculo de Salario Neto

> **Estado:** `IMPLEMENTED`
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción
Calcula automáticamente el salario neto del empleado según su tipo de contrato, aplicando las deducciones y bonificaciones correspondientes. El cálculo se realiza en el `payroll-service` consultando los datos del empleado desde el `employee-service`.

### Requerimiento de Negocio
Como administrador de recursos humanos, quiero calcular automáticamente el salario neto del empleado según su tipo de contrato para procesar la nómina sin intervención manual y sin riesgo de errores.

### Historias de Usuario

#### HU-03: Cálculo de salario neto

```
Como:        Administrador de recursos humanos
Quiero:      Calcular automáticamente el salario neto del empleado según su tipo de contrato
Para:        Procesar la nómina sin intervención manual y sin riesgo de errores

Prioridad:   Alta
Estimación:  M
Dependencias: HU-01 (empleado debe estar registrado)
Microservicio: payroll-service (con consulta a employee-service)
```

#### Criterios de Aceptación — HU-03

**Happy Path — Tiempo Completo**
```gherkin
CRITERIO-3.1: Aplicación correcta para contrato tiempo completo
  Dado que:  existe un empleado registrado con tipo de contrato de tiempo completo y un salario bruto definido
  Cuando:    se procesa la nómina del empleado
  Entonces:  el sistema debe calcular el salario neto aplicando 9.45% de deducción
  Y:         8.33% de bonificación sobre el salario bruto
```

**Happy Path — Medio Tiempo**
```gherkin
CRITERIO-3.2: Aplicación correcta para contrato medio tiempo
  Dado que:  existe un empleado registrado con tipo de contrato de medio tiempo y un salario bruto definido
  Cuando:    se procesa la nómina del empleado
  Entonces:  el sistema debe calcular el salario neto aplicando 9.45% de deducción
  Y:         8.33% de bonificación sobre el salario bruto
```

**Happy Path — Servicios Profesionales**
```gherkin
CRITERIO-3.3: Aplicación correcta para contrato servicios profesionales
  Dado que:  existe un empleado registrado con contrato de servicios profesionales y un salario bruto definido
  Cuando:    se procesa la nómina del empleado
  Entonces:  el sistema debe calcular el salario neto aplicando 8.00% de deducción
  Y:         0% de bonificación sobre el salario bruto
```

**Error Path**
```gherkin
CRITERIO-3.4: No permitir cálculo sin empleado registrado
  Dado que:  no existe el empleado con el ID proporcionado
  Cuando:    el administrador intenta procesar una nómina
  Entonces:  el sistema no permite realizar el cálculo
  Y:         informa que debe registrar un empleado antes de continuar (HTTP 404)
```

### Reglas de Negocio

#### Tabla de deducciones y bonificaciones por tipo de contrato
| Tipo de Contrato | Deducción (%) | Bonificación (%) |
|-----------------|---------------|------------------|
| `FULL_TIME` | 9.45% | 8.33% |
| `PART_TIME` | 9.45% | 8.33% |
| `PROFESSIONAL_SERVICES` | 8.00% | 0.00% |

#### Fórmula de cálculo
```
deduccionMonto   = salarioBruto × (deduccion / 100)
bonificacionMonto = salarioBruto × (bonificacion / 100)
salarioNeto      = salarioBruto - deduccionMonto + bonificacionMonto
```

1. El cálculo solo puede realizarse si el empleado existe (consultar employee-service).
2. Los porcentajes de deducción y bonificación dependen exclusivamente del tipo de contrato.
3. Todos los montos se calculan con `BigDecimal` y precisión de 2 decimales (`HALF_UP`).
4. El resultado del cálculo (nómina) se persiste en el `payroll-service`.
5. No hay autenticación.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas
| Entidad | Almacén | Cambios | Microservicio | Descripción |
|---------|---------|---------|---------------|-------------|
| `Payroll` | tabla `payrolls` | nueva | payroll-service | Registro del cálculo de nómina |

#### Campos del modelo (JPA Entity — payroll-service)
| Campo | Tipo Java | Columna DB | Obligatorio | Validación | Descripción |
|-------|-----------|------------|-------------|------------|-------------|
| `id` | Long | `id` (PK, auto) | sí | auto-generado | Identificador de la nómina |
| `employeeId` | Long | `employee_id` | sí | `@NotNull` | ID del empleado |
| `employeeName` | String | `employee_name` | sí | denormalizado | Nombre del empleado |
| `contractType` | ContractType | `contract_type` | sí | `@Enumerated` | Tipo de contrato |
| `grossSalary` | BigDecimal | `gross_salary` | sí | > 0 | Salario bruto del empleado |
| `deductionPercentage` | BigDecimal | `deduction_pct` | sí | calculado | Porcentaje de deducción |
| `deductionAmount` | BigDecimal | `deduction_amount` | sí | calculado | Monto de deducción |
| `bonusPercentage` | BigDecimal | `bonus_pct` | sí | calculado | Porcentaje de bonificación |
| `bonusAmount` | BigDecimal | `bonus_amount` | sí | calculado | Monto de bonificación |
| `netSalary` | BigDecimal | `net_salary` | sí | calculado | Salario neto |
| `confirmed` | Boolean | `confirmed` | sí | default false | Si fue confirmada |
| `createdAt` | LocalDateTime | `created_at` | sí | `@CreatedDate` | Timestamp |
| `updatedAt` | LocalDateTime | `updated_at` | sí | `@LastModifiedDate` | Timestamp |

### Comunicación entre microservicios
El `payroll-service` consulta al `employee-service` de forma **síncrona** (REST) para pre-calcular.

---

## 3. LISTA DE TAREAS

### Backend (payroll-service)

#### Implementación
- [x] Crear enum `ContractType` (compartido o duplicado desde employee-service)
- [x] Crear entity JPA `Payroll` con todos los campos del cálculo
- [x] Crear DTO `PayrollResponse`
- [x] Implementar `PayrollRepository` (interface extends JpaRepository)
- [x] Implementar `PayrollService` — lógica de cálculo con tabla de deducciones/bonificaciones
- [x] Implementar `PayrollController`
- [x] Crear `EmployeeClient` en `infrastructure/` para consultar employee-service vía REST
- [x] Crear DTO `EmployeeResponse` (copia del contrato)

#### Tests Unitarios
- [x] `should_calculatePayroll_when_fullTimeEmployee`
- [x] `should_calculatePayroll_when_partTimeEmployee`
- [x] `should_calculatePayroll_when_professionalServices`
- [x] `should_throwException_when_employeeNotFound`

### QA (Omitido)
- [x] Actualizar estado spec: `status: IMPLEMENTED`
