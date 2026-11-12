package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exceptions.InvalidSolicitudDataException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudUseCaseTest {

    private static final Integer ID_TIPO = 1;
    private static final Integer ID_ESTADO_INICIAL = 10;

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Mock
    private EstadoRepository estadoRepository;

    private SolicitudUseCase solicitudUseCase;

    @BeforeEach
    void setUp() {
        solicitudUseCase = new SolicitudUseCase(solicitudRepository, tipoPrestamoRepository, estadoRepository);
    }

    private Solicitud validSolicitud() {
        return Solicitud.builder()
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .email("cliente@mail.com")
                .idTipoPrestamo(ID_TIPO)
                .build();
    }

    private TipoPrestamo tipoPrestamo() {
        return TipoPrestamo.builder()
                .idTipoPrestamo(ID_TIPO)
                .nombre("Libre inversión")
                .montoMinimo(new BigDecimal("500000"))
                .montoMaximo(new BigDecimal("50000000"))
                .tasaInteres(new BigDecimal("2.2"))
                .validacionAutomatica(true)
                .build();
    }

    private void givenValidDependencies() {
        lenient().when(tipoPrestamoRepository.findById(ID_TIPO)).thenReturn(Optional.of(tipoPrestamo()));
        lenient().when(estadoRepository.findByNombre(SolicitudUseCase.ESTADO_INICIAL))
                .thenReturn(Optional.of(Estado.builder().idEstado(ID_ESTADO_INICIAL).nombre(SolicitudUseCase.ESTADO_INICIAL).build()));
        lenient().when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> ((Solicitud) invocation.getArgument(0)).toBuilder().idSolicitud(1).build());
    }

    @Test
    void shouldRegisterValidSolicitud() {
        givenValidDependencies();

        Solicitud saved = solicitudUseCase.registrarSolicitud(validSolicitud());

        assertEquals(1, saved.getIdSolicitud());
        assertEquals("cliente@mail.com", saved.getEmail());
        assertEquals(ID_ESTADO_INICIAL, saved.getIdEstado());
    }

    @Test
    void shouldSetInitialStateAndNormalizeEmailBeforeSaving() {
        givenValidDependencies();
        Solicitud input = validSolicitud().toBuilder()
                .idSolicitud(99)
                .email("  CLIENTE@Mail.com ")
                .build();

        solicitudUseCase.registrarSolicitud(input);

        ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepository).save(captor.capture());
        assertEquals("cliente@mail.com", captor.getValue().getEmail());
        assertEquals(ID_ESTADO_INICIAL, captor.getValue().getIdEstado());
        assertNull(captor.getValue().getIdSolicitud());
    }

    @Test
    void shouldRejectUnknownTipoPrestamo() {
        when(tipoPrestamoRepository.findById(any())).thenReturn(Optional.empty());

        Solicitud solicitud = validSolicitud();
        InvalidSolicitudDataException ex =
                assertThrows(InvalidSolicitudDataException.class, () -> solicitudUseCase.registrarSolicitud(solicitud));

        assertTrue(ex.getErrors().contains(SolicitudUseCase.TIPO_PRESTAMO_INEXISTENTE));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void shouldReportAllMissingRequiredFields() {
        Solicitud empty = Solicitud.builder().build();

        InvalidSolicitudDataException ex =
                assertThrows(InvalidSolicitudDataException.class, () -> solicitudUseCase.registrarSolicitud(empty));

        assertEquals(4, ex.getErrors().size());
        assertTrue(ex.getErrors().contains(SolicitudUseCase.EMAIL_REQUERIDO));
        assertTrue(ex.getErrors().contains(SolicitudUseCase.MONTO_REQUERIDO));
        assertTrue(ex.getErrors().contains(SolicitudUseCase.PLAZO_REQUERIDO));
        assertTrue(ex.getErrors().contains(SolicitudUseCase.TIPO_PRESTAMO_REQUERIDO));
        verify(solicitudRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"cliente", "cliente@", "cliente@mail", "@mail.com", "cli ente@mail.com"})
    void shouldRejectInvalidEmailFormat(String email) {
        Solicitud solicitud = validSolicitud().toBuilder().email(email).build();

        InvalidSolicitudDataException ex =
                assertThrows(InvalidSolicitudDataException.class, () -> solicitudUseCase.registrarSolicitud(solicitud));

        assertTrue(ex.getErrors().contains(SolicitudUseCase.EMAIL_INVALIDO));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-5000000"})
    void shouldRejectNonPositiveMonto(String monto) {
        Solicitud solicitud = validSolicitud().toBuilder().monto(new BigDecimal(monto)).build();

        InvalidSolicitudDataException ex =
                assertThrows(InvalidSolicitudDataException.class, () -> solicitudUseCase.registrarSolicitud(solicitud));

        assertTrue(ex.getErrors().contains(SolicitudUseCase.MONTO_NO_POSITIVO));
    }

    @Test
    void shouldRejectMontoWithMoreThanTwoDecimals() {
        Solicitud solicitud = validSolicitud().toBuilder().monto(new BigDecimal("5000000.123")).build();

        InvalidSolicitudDataException ex =
                assertThrows(InvalidSolicitudDataException.class, () -> solicitudUseCase.registrarSolicitud(solicitud));

        assertTrue(ex.getErrors().contains(SolicitudUseCase.MONTO_DECIMALES));
    }

    @ParameterizedTest
    @ValueSource(strings = {"100000", "60000000"})
    void shouldRejectMontoOutOfTipoRange(String monto) {
        when(tipoPrestamoRepository.findById(ID_TIPO)).thenReturn(Optional.of(tipoPrestamo()));
        Solicitud solicitud = validSolicitud().toBuilder().monto(new BigDecimal(monto)).build();

        InvalidSolicitudDataException ex =
                assertThrows(InvalidSolicitudDataException.class, () -> solicitudUseCase.registrarSolicitud(solicitud));

        assertTrue(ex.getErrors().get(0).startsWith("El monto debe estar entre"));
        verify(solicitudRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -3, 601})
    void shouldRejectInvalidPlazo(int plazo) {
        Solicitud solicitud = validSolicitud().toBuilder().plazo(plazo).build();

        assertThrows(InvalidSolicitudDataException.class, () -> solicitudUseCase.registrarSolicitud(solicitud));
    }

    @Test
    void shouldFailWhenInitialStateIsNotConfigured() {
        when(tipoPrestamoRepository.findById(ID_TIPO)).thenReturn(Optional.of(tipoPrestamo()));
        when(estadoRepository.findByNombre(SolicitudUseCase.ESTADO_INICIAL)).thenReturn(Optional.empty());

        Solicitud solicitud = validSolicitud();
        assertThrows(IllegalStateException.class, () -> solicitudUseCase.registrarSolicitud(solicitud));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void shouldRejectNullSolicitud() {
        assertThrows(InvalidSolicitudDataException.class, () -> solicitudUseCase.registrarSolicitud(null));
    }
}
