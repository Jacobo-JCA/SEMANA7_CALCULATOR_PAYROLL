---
name: backend-task
description: Implementa una funcionalidad en el backend Spring Boot basada en una spec ASDD aprobada.
argument-hint: "<nombre-feature> (debe existir .github/specs/<nombre-feature>.spec.md)"
agent: Backend Developer
tools:
  - edit/createFile
  - edit/editFiles
  - read/readFile
  - search/listDirectory
  - search
  - execute/runInTerminal
---

Implementa el backend para el feature especificado, siguiendo la spec aprobada.

**Feature**: ${input:featureName:nombre del feature en kebab-case}

## Pasos obligatorios:

1. **Lee la spec** en `.github/specs/${input:featureName:nombre-feature}.spec.md` — si no existe, detente e informa al usuario.
2. **Revisa el código existente** en `src/main/java/` del microservicio destino para entender patrones actuales.
3. **Implementa en orden**:
   - `entity/` — Entidad JPA con `@Entity`, `@Table`, campos de auditoría
   - `dto/` — Request/Response DTOs con Bean Validation
   - `repository/` — Interface `extends JpaRepository`
   - `service/` — Servicio con lógica de negocio
   - `controller/` — `@RestController` con endpoints REST
   - `infrastructure/messaging/` — Producer/Consumer RabbitMQ (si aplica)
   - `exception/` — Excepciones de dominio + `GlobalExceptionHandler`
4. **Verifica sintaxis** ejecutando: `./gradlew compileJava`

## Restricciones:
- Inyección de dependencias por constructor exclusivamente.
- Validaciones con Bean Validation (`@Valid`, `@NotBlank`, `@Positive`).
- BigDecimal para montos monetarios.
- Enum para tipos de contrato.
- NO poner lógica de negocio en controllers.
- NO importar infraestructura (RabbitMQ) en la capa de servicios.
