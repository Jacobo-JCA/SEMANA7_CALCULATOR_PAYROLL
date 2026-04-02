# HU-04 — Confirmación del resultado

**Como** administrador de recursos humanos
**Quiero** ver el resultado del cálculo antes de generar el documento
**Para** confirmar que los datos son correctos antes de emitir el recibo.

## Criterios de aceptación

### Resumen mostrando el desglose del calculo
- **Given** la nomina del empleado fue calculada
- **When** el administrador accede al resumen del calculo
- **Then** el sistema debe mostrar el nombre, tipo de contrato, salario bruto, deducciones, bonificacion y salario neto del empleado

### Confirmacion del resumen habilita la generacion del PDF
- **Given** que la nomina del empleado fue calculada
- **And** el administrador esta revisando el resumen
- **When** el administrador confirma que los datos son correctos
- **Then** el sistema debe habilitar la descargar del PDF

### No se puede generar el PDF sin confirmar el resumen
- **Given** la nomina del empleado fue calculada
- **And** el administrador no confirma los datos 
- **When** el administrador intenta generar el PDF
- **Then** el sistema debe informar que debe confirmar los datos antes de seguir
