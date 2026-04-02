# HU-05 — Generación de PDF

**Como** administrador de recursos humanos
**Quiero** generar un documento PDF descargable con el desglose completo del salario neto
**Para** contar con un comprobante formal que pueda entregar al empleado o archivar.

## Criterios de aceptación

### Generación exitosa del PDF
- **Given** que la nomina del empleado fue calculada
- **And** el administrador confirmo los datos del resumen
- **When** el administrador genera el PDF
- **Then** el sistema debe generar un PDF descargable con los siguientes datos: nombre, tipo de contrato, salario bruto, deducción, bonificación y salario neto del empleado.

### Notificar al usuario cuando se descargue el pdf
- **Given** que la nomina del empleado fue calculada
- **And** el administrador confirmo los datos del resumen
- **When** el administrador descarga el PDF
- **Then** el sistema debe notificar descarga exitosa
