---
applyTo: "**/src/main/java/**/*.java"
---

> **Scope**: Se aplica a proyectos backend con Java/Spring Boot. Si el proyecto usa un stack diferente, adaptar la sección de convenciones al stack real.

# Instrucciones para Archivos de Backend (Java 21 / Spring Boot 3)

## Stack Tecnológico

- **Lenguaje**: Java 21+
- **Framework**: Spring Boot 3.x
- **Build**: Gradle (Groovy DSL)
- **Base de Datos**: PostgreSQL con Spring Data JPA / Hibernate
- **Mensajería**: RabbitMQ con Spring AMQP
- **Autenticación**: No aplica (sin autenticación en este proyecto)
- **Documentación API**: SpringDoc OpenAPI (Swagger UI)

## Arquitectura del Proyecto — Microservicios

Este proyecto consta de dos microservicios independientes:

| Microservicio | Responsabilidad | Puerto |
|---------------|----------------|--------|
| `employee-service` | Registro y gestión de empleados | 8081 |
| `payroll-service` | Cálculo de nómina, confirmación y generación de PDF | 8082 |

### Comunicación entre servicios

```
employee-service  ──(RabbitMQ)──►  payroll-service
```

- **Asíncrona** vía RabbitMQ (Spring AMQP).
- `employee-service` publica eventos cuando un empleado queda habilitado para nómina.
- `payroll-service` consume eventos y procesa el cálculo.

## Arquitectura en Capas (por microservicio)

Siempre sigue la arquitectura en capas del proyecto:

```
Controller → Service → Repository → Database (PostgreSQL)
     ↕
  Infrastructure (RabbitMQ Producer / Consumer)
```

### Estructura de paquetes por microservicio

```
com.nomina.<servicio>/
├── controller/        ← REST Controllers. Sin lógica de negocio.
├── service/           ← Lógica de negocio pura.
├── repository/        ← Interfaces Spring Data JPA.
├── entity/            ← Entidades JPA (@Entity).
├── dto/               ← Request/Response DTOs.
├── exception/         ← Excepciones de dominio + GlobalExceptionHandler.
├── config/            ← Configuraciones (RabbitMQ, OpenAPI, etc).
└── infrastructure/    ← Productores y consumidores RabbitMQ (separados del dominio).
    ├── messaging/
    │   ├── producer/  ← Publicadores de eventos.
    │   └── consumer/  ← Listeners de colas.
    └── event/         ← DTOs de eventos (payload de mensajes).
```

## Ejemplo de estructura

```java
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.create(request));
    }
}

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public EmployeeResponse create(EmployeeRequest request) {
        // validaciones de negocio
        // persistir entidad
        // retornar DTO response
    }
}

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
```

### Ejemplo de infraestructura RabbitMQ (Producer)

```java
@Component
public class EmployeeEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public EmployeeEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEmployeeReady(EmployeeReadyEvent event) {
        rabbitTemplate.convertAndSend("employee.exchange", "employee.ready", event);
    }
}
```

### Ejemplo de infraestructura RabbitMQ (Consumer)

```java
@Component
public class PayrollEventConsumer {

    private final PayrollService payrollService;

    public PayrollEventConsumer(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @RabbitListener(queues = "payroll.calculate.queue")
    public void handleEmployeeReady(EmployeeReadyEvent event) {
        payrollService.calculatePayroll(event.getEmployeeId());
    }
}
```

## Convenciones de Código

- Java 21+
- Spring Boot 3.x
- REST API versionada (`/api/v1/`)
- Nombres en camelCase para variables y métodos
- Clases en PascalCase
- Paquetes en minúsculas con punto: `com.nomina.employee`
- DTOs siempre separados de Entities
- Validaciones con Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`, `@Positive`)
- Manejo global de excepciones con `@RestControllerAdvice`
- NUNCA manejar dependencias fuera del contexto de Spring
- SIEMPRE usar inyección de dependencias por constructor

## Nuevas Rutas / Controladores

Para agregar un endpoint:
1. Crear clase en `controller/`
2. Usar `@RestController`
3. Definir rutas con `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.
4. Delegar SIEMPRE al service
5. Usar `@Valid` en los `@RequestBody` para validación automática

> Ver `README.md` para la estructura de carpetas específica del proyecto.

## Nunca hacer

- Instanciar servicios o repositorios con `new`
- Poner lógica de negocio en controllers
- Acceder directamente a la base de datos fuera de repository
- Mezclar DTOs con Entities
- Importar clases de infraestructura (RabbitMQ) en la capa de servicios de negocio
- Hardcodear URLs de otros microservicios
- Usar llamadas HTTP síncronas entre microservicios (usar RabbitMQ)

---

> Para estándares de código limpio, SOLID, nombrado, API REST y observabilidad, ver `.github/docs/lineamientos/dev-guidelines.md`.
