---
name: generate-tests
description: Genera pruebas unitarias para backend Java/Spring Boot (JUnit 5 + Mockito), basadas en la spec ASDD y el código implementado.
argument-hint: "<nombre-feature>"
agent: Test Engineer Backend
tools:
  - edit/createFile
  - edit/editFiles
  - read/readFile
  - search/listDirectory
  - search
  - execute/runInTerminal
---

Genera pruebas unitarias completas para el feature especificado.

**Feature**: ${input:featureName:nombre del feature en kebab-case}

## Pasos obligatorios:

1. **Lee la spec** en `.github/specs/${input:featureName:nombre-feature}.spec.md` — sección "Tests Unitarios".
2. **Delega a `Test Engineer Backend`**:
   - `src/test/java/com/nomina/<servicio>/service/<Feature>ServiceTest.java`
   - `src/test/java/com/nomina/<servicio>/controller/<Feature>ControllerTest.java`
3. **Verifica** que los tests corren: `./gradlew test`

## Cobertura obligatoria por test:
- ✅ Happy path (flujo exitoso)
- ❌ Error path (excepciones, datos inválidos)
- 🔲 Edge cases (campos vacíos, duplicados, valores límite)

## Restricciones:
- Cada test debe ser independiente (no compartir estado).
- Mockear SIEMPRE las dependencias externas (DB, RabbitMQ).
- Para servicios: usar `@ExtendWith(MockitoExtension.class)` + `@Mock` + `@InjectMocks`.
- Para controllers: usar `@WebMvcTest` + `MockMvc` + `@MockBean`.
