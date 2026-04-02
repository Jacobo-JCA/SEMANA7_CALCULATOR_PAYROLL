---
name: frontend-task
description: No aplica para este proyecto (backend-only). Placeholder para futuros proyectos con frontend.
argument-hint: "<nombre-feature>"
agent: Frontend Developer
tools:
  - edit/createFile
  - edit/editFiles
  - read/readFile
  - search/listDirectory
  - search
  - execute/runInTerminal
---

Implementa el frontend para el feature especificado, siguiendo la spec aprobada.

**Feature**: ${input:featureName:nombre del feature en kebab-case}

## Pasos obligatorios:

1. **Lee la spec** en `.github/specs/${input:featureName:nombre-feature}.spec.md` — si no existe, detente e informa al usuario.
2. **Revisa el código existente** en `frontend/src/` para entender patrones actuales de services, hooks y componentes.
3. **Implementa en orden**:
    - `services/` — Encapsular llamadas HTTP al backend (`employee-service`, `payroll-service`).
    - `hooks/` — Manejo de estado y efectos, exponer acciones para los componentes.
    - `components/` — UI reutilizable, recibir props y emitir eventos.
    - `pages/` — Composición y layout, mapear exactamente los campos que retorna el backend.
    - `routes/` — Registrar la nueva vista en el router.
4. **Verifica sintaxis y build** ejecutando: `npm run build` o `yarn build`.

## Restricciones:
- Consumir **auth state** solo desde el hook/store de auth — nunca duplicar.
- Usar **variables de entorno** para URLs (`process.env.REACT_APP_API_URL`) y nunca hardcodear.
- Usar únicamente el sistema de **estilos aprobado** (Tailwind/Chakra/Material).
- Reflejar las **validaciones del backend** en formularios (ejemplo: nombre solo letras y espacios, salario positivo).
- NO llamar APIs directamente desde componentes o páginas.
- NO duplicar lógica de negocio que ya existe en hooks/state.
- NO generar tests (responsabilidad del agente `test-engineer-frontend`).
