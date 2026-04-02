---
id: SPEC-003
status: APPROVED
feature: confirmacion-resultado
created: 2026-03-25
updated: 2026-03-25
author: spec-generator
version: "1.0"
related-specs: [SPEC-001, SPEC-002]
---

# Spec: Confirmación del Resultado

> **Estado:** `APPROVED`
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción
Permite al administrador de recursos humanos revisar el resumen del cálculo de nómina antes de generar el documento PDF. El sistema muestra el desglose completo y requiere confirmación explícita antes de habilitar la descarga.

### Requerimiento de Negocio
Como administrador de recursos humanos, quiero ver el resultado del cálculo antes de generar el documento para confirmar que los datos son correctos antes de emitir el recibo.

### Historias de Usuario

#### HU-04: Confirmación del resultado

```
Como:        Administrador de recursos humanos
Quiero:      Ver el resultado del cálculo antes de generar el documento
Para:        Confirmar que los datos son correctos antes de emitir el recibo

Prioridad:   Alta
Estimación:  M
Dependencias: HU-03 (la nómina debe estar calculada)
Microservicio: payroll-service
```

#### Criterios de Aceptación — HU-04

**Happy Path — Resumen**
```gherkin
CRITERIO-4.1: Resumen mostrando el desglose del cálculo
  Dado que:  la nómina del empleado fue calculada
  Cuando:    el administrador accede al resumen del cálculo
  Entonces:  el sistema debe mostrar el nombre, tipo de contrato, salario bruto, deducciones, bonificación y salario neto del empleado
```

**Happy Path — Confirmación**
```gherkin
CRITERIO-4.2: Confirmación del resumen habilita la generación del PDF
  Dado que:  la nómina del empleado fue calculada
  Y:         el administrador está revisando el resumen
  Cuando:    el administrador confirma que los datos son correctos
  Entonces:  el sistema debe habilitar la descarga del PDF
```

**Error Path — Sin confirmación**
```gherkin
CRITERIO-4.3: No se puede generar el PDF sin confirmar el resumen
  Dado que:  la nómina del empleado fue calculada
  Y:         el administrador no confirma los datos
  Cuando:    el administrador intenta generar el PDF
  Entonces:  el sistema debe informar que debe confirmar los datos antes de seguir (HTTP 400)
```

### Reglas de Negocio
1. Solo se puede confirmar una nómina que ha sido calculada previamente.
2. La confirmación cambia el campo `confirmed` de `false` a `true`.
3. El PDF solo se puede generar/descargar si `confirmed = true`.
4. El resumen expone: nombre del empleado, tipo de contrato, salario bruto, porcentaje y monto de deducción, porcentaje y monto de bonificación, salario neto.
5. No hay autenticación: cualquier petición válida puede confirmar y descargar.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas
| Entidad | Almacén | Cambios | Microservicio | Descripción |
|---------|---------|---------|---------------|-------------|
| `Payroll` | tabla `payrolls` | modificada (campo `confirmed`) | payroll-service | Se usa el campo `confirmed` ya definido en SPEC-002 |

> No se crean entidades nuevas. Se reutiliza la entidad `Payroll` de SPEC-002.

### API Endpoints

#### GET /api/v1/payroll/{payrollId}/summary
- **Microservicio**: payroll-service
- **Descripción**: Obtiene el resumen/desglose completo de la nómina calculada
- **Response 200**:
  ```json
  {
    "id": 1,
    "employeeName": "Juan Pérez",
    "contractType": "FULL_TIME",
    "grossSalary": 2500.00,
    "deductionPercentage": 9.45,
    "deductionAmount": 236.25,
    "bonusPercentage": 8.33,
    "bonusAmount": 208.25,
    "netSalary": 2472.00,
    "confirmed": false
  }
  ```
- **Response 404**: nómina no encontrada

#### PUT /api/v1/payroll/{payrollId}/confirm
- **Microservicio**: payroll-service
- **Descripción**: Confirma que los datos de la nómina son correctos
- **Response 200**:
  ```json
  {
    "id": 1,
    "confirmed": true,
    "message": "Nómina confirmada. PDF disponible para descarga."
  }
  ```
- **Response 404**: nómina no encontrada
- **Response 400**: nómina ya fue confirmada previamente

#### GET /api/v1/payroll/{payrollId}/pdf
- **Microservicio**: payroll-service
- **Descripción**: Genera y descarga el recibo de nómina en PDF
- **Response 200**: archivo PDF (`Content-Type: application/pdf`)
- **Response 400**: nómina no confirmada — "Debe confirmar los datos antes de generar el PDF"
- **Response 404**: nómina no encontrada

### Notas de Implementación
- El endpoint `/summary` reutiliza los datos ya persistidos de la entidad `Payroll`.
- El endpoint `/confirm` simplemente actualiza `confirmed = true` en la base de datos.
- Para la generación de PDF se recomienda usar la librería **iText** o **Apache PDFBox** si es en base a HTML, o similar.
- El PDF debe contener: encabezado con datos de la empresa (configurables), nombre del empleado, tipo de contrato, desglose de cálculo y firma/fecha.

---

## 3. LISTA DE TAREAS

### Backend (payroll-service)

#### Implementación
- [ ] Crear DTO `PayrollSummaryResponse` con campos de desglose
- [ ] Crear DTO `PayrollConfirmResponse` con mensaje y estado
- [ ] Agregar endpoint GET `/api/v1/payroll/{payrollId}/summary` en `PayrollController`
- [ ] Agregar endpoint PUT `/api/v1/payroll/{payrollId}/confirm` en `PayrollController`
- [ ] Agregar endpoint GET `/api/v1/payroll/{payrollId}/pdf` en `PayrollController`
- [ ] Implementar lógica de confirmación en `PayrollService`
- [ ] Implementar generación de PDF en `PayrollPdfService` (capa service o infrastructure)
- [ ] Crear excepciones `PayrollNotConfirmedException`, `PayrollAlreadyConfirmedException`
- [ ] Agregar dependencia de iText/PDFBox en `pom.xml`

#### Tests Unitarios
- [ ] `should_returnSummary_when_payrollExists` — resumen exitoso
- [ ] `should_return404_when_payrollNotFound` — nómina inexistente
- [ ] `should_confirmPayroll_when_notConfirmedYet` — confirmación exitosa
- [ ] `should_throwException_when_alreadyConfirmed` — doble confirmación
- [ ] `should_generatePdf_when_payrollConfirmed` — PDF exitoso
- [ ] `should_throwException_when_pdfRequestedWithoutConfirmation` — PDF sin confirmar

### QA (Omitido)
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
