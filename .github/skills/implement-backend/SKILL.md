---
name: implement-backend
description: Implementa un feature completo en el backend Spring Boot. Requiere spec con status APPROVED en .github/specs/.
argument-hint: "<nombre-feature>"
---

# Implement Backend

## Prerequisitos
1. Leer spec: `.github/specs/<feature>.spec.md` — sección 2 (modelos, endpoints, eventos)
2. Leer stack: `.github/instructions/backend.instructions.md`

## Orden de implementación
```
entity → dto → repository → service → controller → infrastructure (RabbitMQ) → registrar beans
```

| Capa | Responsabilidad |
|------|-----------------|
| **Entity** | Entidad JPA con `@Entity`, `@Table`, campos de auditoría |
| **DTO** | Request/Response separados, con Bean Validation (`@Valid`, `@NotBlank`) |
| **Repository** | Interface `extends JpaRepository` — sin lógica de negocio |
| **Service** | Lógica de negocio pura — orquesta repositorios |
| **Controller** | `@RestController` — parsing HTTP + DI + delegar al service |
| **Infrastructure** | Productores/Consumidores RabbitMQ — separados del dominio |

## Patrón de DI (obligatorio)
- Inyección por constructor exclusivamente
- Spring autowire es implícito con un solo constructor
- El service recibe el repo por constructor; el controller recibe el service

Ver patrones específicos del stack en `.github/instructions/backend.instructions.md`.

## Manejo de Excepciones
- Crear excepciones de dominio (ej. `EmployeeNotFoundException`, `InvalidSalaryException`)
- Centralizar en `@RestControllerAdvice` con `GlobalExceptionHandler`
- Respuestas de error con estructura consistente (código, mensaje, timestamp)

## Reglas
- Validaciones de entrada con Bean Validation (`@Valid`, `@NotNull`, `@Positive`, `@Pattern`)
- Timestamps con `@CreatedDate` / `@LastModifiedDate` (Spring Data Auditing)
- BigDecimal para montos monetarios
- Enum para tipos de contrato

## Restricciones
- Solo directorio del microservicio correspondiente. No tocar otro microservicio.
- No generar tests (responsabilidad de `test-engineer-backend`).
