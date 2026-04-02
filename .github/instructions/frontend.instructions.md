---
applyTo: "frontend/src/**/*.{js,jsx,ts,tsx}"
---

# Instrucciones para Archivos de Frontend

Este proyecto ahora incluye una capa **frontend en React** que consume los microservicios backend:

- `employee-service`
- `payroll-service`

## Arquitectura del Frontend

Orden de implementación:

### Capa Services
- Encapsular llamadas HTTP al backend.
- Usar `fetch` o el HTTP client aprobado.
- URL siempre desde variables de entorno (`process.env.REACT_APP_API_URL`).
- Incluir `Authorization: Bearer <token>` en endpoints protegidos.
- No manejar estado ni lógica de negocio aquí.

### Hooks / State
- Manejar estado local y efectos.
- Definir acciones y exponer funciones para los componentes.
- Nunca llamar APIs directamente desde componentes.
- Reutilizar hooks existentes, no duplicar lógica.

### Components
- UI reutilizable, recibir props y emitir eventos.
- No manejar estado global ni llamar APIs.
- Seguir el sistema de estilos aprobado (Tailwind/Chakra/Material según lineamientos).

### Pages / Views
- Composición y layout.
- Registrar rutas en el router del proyecto.
- Mapear exactamente los campos que retorna el backend:
    - **Employee Registration (Step 1)**: `name`, `contractType`, `grossSalary`.
    - **Earnings (Step 2)**: horas trabajadas, bonos.
    - **Deductions (Step 3)**: impuestos, descuentos.
    - **Review (Step 4)**: resumen final con net salary y confirmación.
- Omitir campos que el backend no expone.

### Convenciones
- **Auth**: consumir solo desde el hook/store de auth.
- **Estilos**: usar únicamente el sistema aprobado.
- **Validaciones**: reflejar las reglas del backend en formularios (ejemplo: nombre solo letras y espacios, salario positivo).
- **Variables de entorno**: nunca hardcodear URLs ni tokens.

## Restricciones
- Trabajar solo en el directorio `frontend/src`.
- No generar tests (responsabilidad de `test-engineer-frontend`).
- No duplicar lógica de negocio que ya existe en hooks/state.
- Seguir exactamente los lineamientos de `.github/docs/lineamientos/dev-guidelines.md`.
