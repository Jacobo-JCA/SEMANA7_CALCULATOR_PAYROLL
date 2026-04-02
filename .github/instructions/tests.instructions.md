---
applyTo: "**/src/test/java/**/*.java"
---

> **Scope**: Se aplica a proyectos con tests en Java/Spring Boot. Mantener los principios de independencia, aislamiento, AAA y cobertura ≥ 80%.

# Instrucciones para Archivos de Pruebas Unitarias (Java / JUnit 5)

## Principios

- **Independencia**: cada test es 100% independiente — sin estado compartido entre tests.
- **Aislamiento**: mockear SIEMPRE dependencias externas (DB, RabbitMQ, APIs REST, sistema de archivos).
- **Claridad**: nombre del test debe describir la función bajo prueba y el escenario.
- **Cobertura**: cubrir happy path, error path y edge cases para cada unidad.

## Backend (JUnit 5 + Mockito)

### Estructura de archivos
```
src/test/java/com/nomina/<servicio>/
  service/<Feature>ServiceTest.java
  controller/<Feature>ControllerTest.java
```

### Convenciones
- Nombre: `should_[resultado]_when_[condición]` (ej: `should_createEmployee_when_validRequest`, `should_throwException_when_duplicateName`)
- Usar `@ExtendWith(MockitoExtension.class)` para inyectar mocks.
- Mockear repositorios en tests de servicios con `@Mock` + `@InjectMocks`.
- Para tests de controllers usar `@WebMvcTest` + `MockMvc` (solo unitarios, sin levantar contexto completo).

```java
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeService service;

    @Test
    void should_createEmployee_when_validRequest() {
        // GIVEN
        EmployeeRequest request = new EmployeeRequest("Juan", ContractType.FULL_TIME, new BigDecimal("2000"));
        Employee entity = new Employee(1L, "Juan", ContractType.FULL_TIME, new BigDecimal("2000"));
        when(repository.save(any(Employee.class))).thenReturn(entity);

        // WHEN
        EmployeeResponse result = service.create(request);

        // THEN
        assertThat(result.getName()).isEqualTo("Juan");
        verify(repository).save(any(Employee.class));
    }
}
```

```java
// Ejemplo mínimo de test de controller
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @Test
    void should_return201_when_validEmployee() throws Exception {
        // GIVEN
        EmployeeResponse response = new EmployeeResponse(1L, "Juan", ContractType.FULL_TIME, new BigDecimal("2000"));
        when(service.create(any())).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Juan\",\"contractType\":\"FULL_TIME\",\"grossSalary\":2000}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Juan"));
    }
}
```

## Nunca hacer

- Tests que dependen del orden de ejecución.
- Llamadas reales a PostgreSQL, RabbitMQ o APIs externas.
- `System.out.println` permanentes en tests.
- Lógica condicional dentro de un test (if/else).
- Usar `Thread.sleep` para sincronización temporal (cero tests "flaky").
- Levantar el contexto completo de Spring (`@SpringBootTest`) para tests unitarios.

---

> Para quality gates, pirámide de testing y nomenclatura, ver `.github/docs/lineamientos/dev-guidelines.md` §7 y `.github/docs/lineamientos/qa-guidelines.md`.

### Estructura AAA obligatoria
```java
// GIVEN — preparar datos y contexto (Arrange)
// WHEN  — ejecutar la acción bajo prueba (Act)
// THEN  — verificar el resultado esperado (Assert)
```

### Nomenclatura de tests
```
should_[resultado esperado]_when_[condición o acción]
```
Ejemplos:
- `should_createEmployee_when_validRequest`
- `should_throwException_when_salaryIsNegative`
- `should_returnEmpty_when_noEmployeesExist`
