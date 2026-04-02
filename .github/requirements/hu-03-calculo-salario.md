# HU-03 — Cálculo de salario neto

**Como** administrador de recursos humanos
**Quiero** calcular automáticamente el salario neto del empleado según su tipo de contrato
**Para** procesar la nómina sin intervención manual y sin riesgo de errores.

## Criterios de aceptación

### Aplicación correcta de bonificacion y deducciones para tipo de contrato tiempo completo
- **Given** que existe un empleado registrado con un tipo de contrato de tiempo completo y un salario bruto definido
- **When** se procesa la nomina del empleado
- **Then** el sistema debe calcular el salario neto aplicando 9.45% de deducción 
- **And** 8.33% de bonificación sobre el salario bruto

### Aplicación correcta de bonificacion y deducciones para tipo de contrato medio tiempo
- **Given** que existe un empleado registrado con un tipo de contrato de medio tiempo y un salario bruto definido
- **When** se procesa la nomina del empleado
- **Then** el sistema debe calcular el salario neto aplicando 9.45% de deducción 
- **And** 8.33% de bonificación sobre el salario bruto

### Aplicación correcta de bonificacion y deducciones para tipo de contrato servicios profesionales
- **Given** que existe un empleado registrado con contrato de servicios profesionales y un salario bruto definido
- **When** se procesa la nomina del empleado
- **Then** el sistema debe calcular el salario neto aplicando 8.00% de deducción 
- **And** 0% de bonificación sobre el salario bruto

### No permitir calculo de nomina si el empleado no esta registrado
- **Given** no existe ningún empleado registrado en el sistema
- **When** el administrador intenta procesar una nomina
- **Then** el sistema no permite realizar el calculo
- **And** informa que debe registrar un empleado antes de continuar
