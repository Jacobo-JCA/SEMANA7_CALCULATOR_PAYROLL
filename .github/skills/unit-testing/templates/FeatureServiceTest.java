/**
 * Plantilla para tests de Service (JUnit 5 + Mockito).
 * Copiar y adaptar al feature específico.
 */
package com.nomina.SERVICIO.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureServiceTest {

    @Mock
    private FeatureRepository repository;

    @InjectMocks
    private FeatureService service;

    @Test
    void should_createFeature_when_validRequest() {
        // GIVEN
        FeatureRequest request = new FeatureRequest(/* datos válidos */);
        Feature entity = new Feature(/* entidad esperada */);
        when(repository.save(any(Feature.class))).thenReturn(entity);

        // WHEN
        FeatureResponse result = service.create(request);

        // THEN
        assertThat(result).isNotNull();
        verify(repository).save(any(Feature.class));
    }

    @Test
    void should_throwException_when_invalidData() {
        // GIVEN
        FeatureRequest request = new FeatureRequest(/* datos inválidos */);

        // WHEN & THEN
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("campo requerido");
    }

    @Test
    void should_returnFeature_when_exists() {
        // GIVEN
        Long id = 1L;
        Feature entity = new Feature(/* entidad existente */);
        when(repository.findById(id)).thenReturn(java.util.Optional.of(entity));

        // WHEN
        FeatureResponse result = service.findById(id);

        // THEN
        assertThat(result).isNotNull();
    }

    @Test
    void should_throwException_when_notFound() {
        // GIVEN
        Long id = 999L;
        when(repository.findById(id)).thenReturn(java.util.Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> service.findById(id))
            .isInstanceOf(FeatureNotFoundException.class);
    }
}
