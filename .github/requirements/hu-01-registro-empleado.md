# HU-01 — Registro de empleado

**Como** administrador de recursos humanos
**Quiero** registrar los datos básicos del empleado
**Para** contar con la información necesaria antes de iniciar el cálculo de nómina.

## Criterios de aceptación

### Registro exitoso del empleado
- **Given** no existe el empleado en el sistema
- **When** se registra un empleado con sus datos necesarios (nombre, tipo de contrato y salario bruto)
- **Then** el empleado queda guardado en el sistema
- **And** queda habilitado para confirmar su nómina

### Registro fallido por falta de datos necesarios
- **Given** no existe el empleado en el sistema
- **When** se intenta registrar un empleado sin ingresar datos necesarios
- **Then** el sistema no permite guardar el empleado
- **And** notifica cuales son los datos necesarios

### Registro fallido por salario bruto inválido
- **Given** el empleado no existe en el sistema
- **When** se intenta registrar al empleado con un salario bruto negativo o en cero
- **Then** el sistema no permite guardar el empleado
- **And** notifica que el salario bruto debe ser positivo

### Registro fallido por caracteres en nombre
- **Given** el empleado no consta en el sistema
- **When** se intenta registrar al empleado con un nombre que contiene caracteres especiales
- **Then** el sistema no permite guardarlo
- **And** notifica que el nombre no debe contener caracteres especiales
