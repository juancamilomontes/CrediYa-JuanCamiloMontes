package co.com.crediya.jpa.estado;

import co.com.crediya.jpa.entity.EstadoEntity;
import co.com.crediya.model.estado.Estado;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EstadoRepositoryAdapterTest {

    private final EstadoJpaRepository jpaRepository = mock(EstadoJpaRepository.class);
    private final EstadoRepositoryAdapter adapter = new EstadoRepositoryAdapter(jpaRepository);

    @Test
    void shouldReturnEstadoWhenExists() {
        when(jpaRepository.findByNombre("PENDIENTE_REVISION")).thenReturn(Optional.of(EstadoEntity.builder()
                .idEstado(1)
                .nombre("PENDIENTE_REVISION")
                .descripcion("Pendiente de revisión")
                .build()));

        Optional<Estado> result = adapter.findByNombre("PENDIENTE_REVISION");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIdEstado());
    }

    @Test
    void shouldReturnEmptyWhenNotExists() {
        when(jpaRepository.findByNombre("NO_EXISTE")).thenReturn(Optional.empty());

        assertTrue(adapter.findByNombre("NO_EXISTE").isEmpty());
    }
}
