# HU-02 — Corrección de datos

**Como** administrador de recursos humanos
**Quiero** corregir los datos de un empleado antes de confirmar el cálculo
**Para** asegurarme de que la información es correcta antes de obtener el resultado

## Criterios de aceptación

### Corrección exitosa del nombre del empleado
- **Given** el empleado esta registrado con los datos necesarios (nombre, tipo de contrato y sueldo bruto)
- **And** la nómina no fue confirmada
- **When** se cambia el nombre del empleado
- **Then** los datos del empleado quedan actualizados

### Corrección exitosa del salario bruto del empleado
- **Given** el empleado esta registrado
- **And** la nómina del empleado no fue confirmada
- **When** se actualiza el salario bruto con un valor positivo valido
- **Then** los datos quedan actualizados con el nuevo valor

### Corrección errónea por nombre inválido
- **Given** el empleado esta registrado
- **And** la nómina del empleado no fue confirmada
- **When** se intenta cambiar el nombre del empleado por uno que contiene caracteres especiales
- **Then** los datos del empleado no quedan actualizados
- **And** el sistema informa que el nombre no puede tener caracteres especiales

### Corrección fallida por salario bruto inválido
- **Given** existe un empleado registrado con sus datos
- **And** la nómina del empleado no fue confirmada
- **When** se intenta actualizar el salario bruto con un valor negativo
- **Then** los datos del empleado permanecen sin cambios
- **And** el sistema informa que el salario bruto tiene que ser positivo

### Bloqueo de corrección por nómina ya calculada
- **Given** el empleado esta registrado
- **And** la nómina del empleado fue calculada
- **When** el administrador intenta modificar datos del empleado
- **Then** el sistema no lo permite
- **And** informa que los datos no pueden modificarse porque la nómina ya fue calculada
