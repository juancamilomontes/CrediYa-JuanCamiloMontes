package co.com.crediya.jpa.tipoprestamo;

import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TipoPrestamoRepositoryAdapterTest {

    private final TipoPrestamoJpaRepository jpaRepository = mock(TipoPrestamoJpaRepository.class);
    private final TipoPrestamoRepositoryAdapter adapter = new TipoPrestamoRepositoryAdapter(jpaRepository);

    @Test
    void shouldReturnTipoPrestamoWhenExists() {
        when(jpaRepository.findById(1)).thenReturn(Optional.of(TipoPrestamoEntity.builder()
                .idTipoPrestamo(1)
                .nombre("Libre inversión")
                .montoMinimo(new BigDecimal("500000"))
                .montoMaximo(new BigDecimal("50000000"))
                .tasaInteres(new BigDecimal("2.2"))
                .validacionAutomatica(true)
                .build()));

        Optional<TipoPrestamo> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Libre inversión", result.get().getNombre());
    }

    @Test
    void shouldReturnEmptyWhenNotExists() {
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());

        assertTrue(adapter.findById(99).isEmpty());
    }

    @Test
    void shouldReturnEmptyForNullId() {
        assertTrue(adapter.findById(null).isEmpty());
    }
}
