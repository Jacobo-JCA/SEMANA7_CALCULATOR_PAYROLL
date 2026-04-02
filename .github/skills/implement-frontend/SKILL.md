---
name: implement-frontend
description: Implementa el frontend para el feature especificado, siguiendo la spec aprobada.
argument-hint: "<nombre-feature>"
---

# Implement Frontend

## Prerequisitos
1. Leer spec: `.github/specs/<feature>.spec.md` — sección 2 (modelos, vistas, validaciones)
2. Leer stack: `.github/instructions/frontend.instructions.md`

## Orden de implementación

services → hooks/state → components → pages/views → registrar ruta


| Capa | Responsabilidad |
|------|-----------------|
| **Services** | Encapsular llamadas HTTP al backend (`employee-service`, `payroll-service`) |
| **Hooks / State** | Manejo de estado y efectos, exponer acciones para los componentes |
| **Components** | UI reutilizable, recibir props y emitir eventos |
| **Pages / Views** | Composición y layout, mapear exactamente los campos que retorna el backend |
| **Routes** | Registrar la nueva vista en el router del proyecto |

## Patrón de Consumo de Datos (obligatorio)
- Llamadas HTTP solo desde `services/`.
- Hooks consumen los services y exponen estado/acciones.
- Components reciben props y eventos, nunca llaman APIs.
- Pages ensamblan componentes y registran rutas.

Ver patrones específicos del stack en `.github/instructions/frontend.instructions.md`.

## Manejo de Validaciones
- Reflejar reglas del backend en formularios (ejemplo: `@NotBlank`, `@Positive`, `@Pattern`).
- Mostrar mensajes de error consistentes en la UI.
- Usar librerías aprobadas para validación (ej. Zod, Yup, React Hook Form).

## Reglas
- Consumir **auth state** solo desde el hook/store de auth.
- URLs siempre desde variables de entorno (`process.env.REACT_APP_API_URL`).
- Usar únicamente el sistema de estilos aprobado (Tailwind/Chakra/Material).
- Mapear exactamente los campos que retorna el backend, omitir lo que no exista.
- Formularios deben respetar las validaciones de dominio (ejemplo: salario positivo, nombre solo letras).

## Restricciones
- Solo trabajar en el directorio `frontend/src`.
- No generar tests (responsabilidad de `test-engineer-frontend`).
- No duplicar lógica de negocio que ya existe en hooks/state.
- No llamar APIs directamente desde componentes o páginas.
