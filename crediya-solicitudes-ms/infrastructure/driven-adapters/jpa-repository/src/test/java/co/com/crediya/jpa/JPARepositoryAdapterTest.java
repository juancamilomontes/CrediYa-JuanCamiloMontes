package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.SolicitudEntity;
import co.com.crediya.model.solicitud.Solicitud;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JPARepositoryAdapterTest {

    private final JPARepository repository = mock(JPARepository.class);
    private final JPARepositoryAdapter adapter = new JPARepositoryAdapter(repository);

    @Test
    void shouldSaveSolicitudAndReturnDomainWithId() {
        when(repository.saveAndFlush(any(SolicitudEntity.class))).thenReturn(SolicitudEntity.builder()
                .idSolicitud(15)
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .email("cliente@mail.com")
                .idEstado(1)
                .idTipoPrestamo(1)
                .build());

        Solicitud saved = adapter.save(Solicitud.builder()
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .email("cliente@mail.com")
                .idEstado(1)
                .idTipoPrestamo(1)
                .build());

        assertEquals(15, saved.getIdSolicitud());
        assertEquals("cliente@mail.com", saved.getEmail());
        assertEquals(1, saved.getIdEstado());
    }
}
