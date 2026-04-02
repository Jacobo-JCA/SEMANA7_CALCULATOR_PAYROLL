---
name: unit-testing
description: Genera tests unitarios para backend Java/Spring Boot. Lee la spec y el código implementado. Requiere spec APPROVED e implementación completa.
argument-hint: "<nombre-feature>"
---

# Unit Testing

## Definition of Done — verificar al completar

- [ ] Cobertura ≥ 80% en lógica de negocio (quality gate bloqueante)
- [ ] Tests aislados — sin conexión a DB real ni RabbitMQ (siempre mocks)
- [ ] Escenario feliz + errores de negocio + validaciones de entrada cubiertos
- [ ] Los cambios no rompen contratos existentes del módulo

## Prerequisito — Lee en paralelo

```
.github/specs/<feature>.spec.md        (criterios de aceptación)
código implementado en src/main/java/
.github/instructions/tests.instructions.md   (JUnit 5 + Mockito)
```

## Output → `src/test/java/com/nomina/<servicio>/`

| Archivo | Cubre |
|---------|-------|
| `controller/<Feature>ControllerTest.java` | Endpoints: 200/201, 400, 404, 422 (con @WebMvcTest) |
| `service/<Feature>ServiceTest.java` | Lógica: happy path + errores de negocio (con @Mock + @InjectMocks) |

## Patrones core

```java
// Service test — AAA con Mockito
@ExtendWith(MockitoExtension.class)
class FeatureServiceTest {

    @Mock
    private FeatureRepository repository;

    @InjectMocks
    private FeatureService service;

    @Test
    void should_createFeature_when_validRequest() {
        // GIVEN
        when(repository.save(any())).thenReturn(new Feature(1L, "test"));

        // WHEN
        FeatureResponse result = service.create(new FeatureRequest("test"));

        // THEN
        assertThat(result.getName()).isEqualTo("test");
        verify(repository).save(any());
    }
}
```

```java
// Controller test — MockMvc
@WebMvcTest(FeatureController.class)
class FeatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeatureService service;

    @Test
    void should_return201_when_validRequest() throws Exception {
        when(service.create(any())).thenReturn(new FeatureResponse(1L, "test"));

        mockMvc.perform(post("/api/v1/features")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"test\"}"))
            .andExpect(status().isCreated());
    }
}
```

## Restricciones

- Solo `src/test/java/`. No modificar código fuente.
- Nunca conectar a DB real ni RabbitMQ — siempre mocks.
- Cobertura mínima ≥ 80% en lógica de negocio.
