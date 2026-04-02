---
id: SPEC-005
status: DRAFT
feature: generacion-pdf
created: 2026-03-25
updated: 2026-03-25
author: spec-generator
version: "1.0"
related-specs: [SPEC-003, SPEC-004]
---

# Spec: Generación de PDF

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción
Permite al administrador de recursos humanos generar un documento PDF descargable con el desglose completo del salario neto de una nómina que haya sido previamente confirmada.

### Requerimiento de Negocio
Como administrador de recursos humanos, quiero generar un documento PDF descargable con el desglose completo del salario neto para contar con un comprobante formal que pueda entregar al empleado o archivar.

### Historias de Usuario

#### HU-05: Generación de PDF

```text
Como:        Administrador de recursos humanos
Quiero:      Generar un documento PDF descargable con el desglose completo del salario neto
Para:        Contar con un comprobante formal que pueda entregar al empleado o archivar.

Prioridad:   Alta
Estimación:  M
Dependencias: HU-03 (cálculo), HU-04 (confirmación)
Microservicio: payroll-service
```

### Criterios de Aceptación — HU-05

**Happy Path — Generación correcta**
```gherkin
CRITERIO-5.1: Generacion exitosa del PDF
  Dado que:  la nomina del empleado fue calculada
  Y:         el administrador confirmo los datos del resumen
  Cuando:    el administrador genera el PDF
  Entonces:  el sistema debe generar un PDF descargable con los siguientes datos: nombre, tipo de contrato, salario bruto, deduccion, bonificacion y salario neto del empleado.
```

**Happy Path — Notificación/Descarga**
```gherkin
CRITERIO-5.2: Notificar al usuario cuando se descargue el pdf
  Dado que:  la nomina del empleado fue calculada
  Y:         el administrador confirmo los datos del resumen
  Cuando:    el administrador descarga el PDF
  Entonces:  el sistema debe notificar descarga exitosa
```
*(Nota técnica: La descarga exitosa se notifica entregando correctamente los encabezados HTTP para la descarga del archivo 200 OK).*

### Reglas de Negocio
1. Solo se puede generar el PDF de una nómina que esté en estado `confirmed = true`.
2. El PDF debe incluir: nombre, tipo de contrato, salario bruto, deducción (con su monto), bonificación (con su monto) y el salario neto.

---

## 2. DISEÑO

### Modelos de Datos
No se crean nuevas entidades, se leen los datos existentes de `Payroll`.

### API Endpoints

#### GET /api/v1/payroll/{payrollId}/pdf
- **Microservicio**: payroll-service
- **Descripción**: Genera el archivo PDF si está confirmada.
- **Headers de Respuesta**:
  - `Content-Type: application/pdf`
  - `Content-Disposition: attachment; filename="nomina-{id}.pdf"`
- **Response 200**: Archivo binario.
- **Response 400**: `PayrollNotConfirmedException` — "Debe confirmar los datos antes de generar el PDF".
- **Response 404**: `PayrollNotFoundException`.

### Notas de Implementación
- Separar la generación del reporte en un servicio `PayrollPdfService`.
- Recomendar Apache PDFBox, iText o OpenPDF para Java 21+ Spring Boot 3.

---

## 3. LISTA DE TAREAS

### Backend (payroll-service)

#### Implementación
- [ ] Agregar dependencia para manipulación de PDFs (e.g. `com.github.librepdf:openpdf` o `org.apache.pdfbox:pdfbox`) en el `pom.xml`.
- [ ] Agregar endpoint `/api/v1/payroll/{payrollId}/pdf` en `PayrollController`.
- [ ] Implementar `PayrollPdfService.generatePdf(Long payrollId)` retornando un array de bytes (`byte[]`).
- [ ] Validar explícitamente `if (!payroll.getConfirmed()) throw new PayrollNotConfirmedException(...)`.
- [ ] Darle formato de recibo básico al PDF mediante tablas usando la librería elegida.

#### Tests Unitarios
- [ ] `should_generatePdf_when_payrollConfirmed`
- [ ] `should_throwException_when_pdfRequestedWithoutConfirmation`
- [ ] `should_throwException_when_payrollNotFoundForPdf`

### QA
- [ ] Ejecutar skill `/gherkin-case-generator` → CRITERIO-5.1, 5.2
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
