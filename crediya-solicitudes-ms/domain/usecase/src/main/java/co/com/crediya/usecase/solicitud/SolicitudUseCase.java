package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exceptions.InvalidSolicitudDataException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * HU2 - Registrar una solicitud de préstamo.
 * Aquí viven las reglas de negocio; no depende de Spring, HTTP ni base de datos.
 */
@Log
@RequiredArgsConstructor
public class SolicitudUseCase {

    /** Estado inicial con el que nace toda solicitud (ver tabla "estados"). */
    public static final String ESTADO_INICIAL = "PENDIENTE_REVISION";

    static final BigDecimal MONTO_MINIMO_ABSOLUTO = BigDecimal.ZERO;
    static final int MONTO_MAX_DECIMALES = 2;
    static final int MAX_EMAIL = 150;
    static final int PLAZO_MAXIMO_MESES = 600;

    static final String EMAIL_REQUERIDO = "El campo email es obligatorio";
    static final String EMAIL_INVALIDO = "El email no tiene un formato válido";
    static final String MONTO_REQUERIDO = "El campo monto es obligatorio";
    static final String MONTO_NO_POSITIVO = "El monto debe ser mayor que 0";
    static final String MONTO_DECIMALES = "El monto no puede tener más de 2 decimales";
    static final String PLAZO_REQUERIDO = "El campo plazo es obligatorio";
    static final String PLAZO_NO_POSITIVO = "El plazo debe ser mayor que 0";
    static final String PLAZO_FUERA_DE_RANGO = "El plazo debe estar entre 1 y 600 meses";
    static final String TIPO_PRESTAMO_REQUERIDO = "El campo id_tipo_prestamo es obligatorio";
    static final String TIPO_PRESTAMO_INEXISTENTE = "El tipo de préstamo indicado no existe";
    static final String MONTO_FUERA_DE_RANGO =
            "El monto debe estar entre %s y %s para el tipo de préstamo seleccionado";
    private static final String LONGITUD_MAXIMA = "El campo %s no puede tener más de %d caracteres";
    private static final String ESTADO_INICIAL_NO_CONFIGURADO =
            "No está configurado el estado inicial de las solicitudes: " + ESTADO_INICIAL;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;

    public Solicitud registrarSolicitud(Solicitud solicitud) {
        log.info("Iniciando registro de solicitud");
        Solicitud normalized = normalize(solicitud);
        validate(normalized);
        TipoPrestamo tipoPrestamo = validateTipoPrestamoExists(normalized.getIdTipoPrestamo());
        validateMontoEnRango(normalized.getMonto(), tipoPrestamo);

        Solicitud toSave = normalized.toBuilder()
                .idSolicitud(null)
                .idEstado(estadoInicialId())
                .build();

        Solicitud saved = solicitudRepository.save(toSave);
        log.info(() -> "Solicitud registrada con id " + saved.getIdSolicitud());
        return saved;
    }

    private Solicitud normalize(Solicitud solicitud) {
        if (solicitud == null) {
            throw new InvalidSolicitudDataException(List.of("Los datos de la solicitud son obligatorios"));
        }
        return solicitud.toBuilder()
                .idSolicitud(null)
                .email(normalizeEmail(solicitud.getEmail()))
                .build();
    }

    private void validate(Solicitud solicitud) {
        List<String> errors = new ArrayList<>();
        validateEmail(solicitud.getEmail(), errors);
        validateMaxLength("email", solicitud.getEmail(), MAX_EMAIL, errors);
        validateMonto(solicitud.getMonto(), errors);
        validatePlazo(solicitud.getPlazo(), errors);
        if (solicitud.getIdTipoPrestamo() == null) {
            errors.add(TIPO_PRESTAMO_REQUERIDO);
        }

        if (!errors.isEmpty()) {
            log.warning(() -> "Datos de solicitud inválidos: " + errors);
            throw new InvalidSolicitudDataException(errors);
        }
    }

    private TipoPrestamo validateTipoPrestamoExists(Integer idTipoPrestamo) {
        return tipoPrestamoRepository.findById(idTipoPrestamo)
                .orElseThrow(() -> {
                    log.warning(() -> "Tipo de préstamo inexistente: " + idTipoPrestamo);
                    return new InvalidSolicitudDataException(List.of(TIPO_PRESTAMO_INEXISTENTE));
                });
    }

    private void validateMontoEnRango(BigDecimal monto, TipoPrestamo tipoPrestamo) {
        BigDecimal min = tipoPrestamo.getMontoMinimo();
        BigDecimal max = tipoPrestamo.getMontoMaximo();
        boolean bajoMinimo = min != null && monto.compareTo(min) < 0;
        boolean sobreMaximo = max != null && monto.compareTo(max) > 0;
        if (bajoMinimo || sobreMaximo) {
            String mensaje = String.format(MONTO_FUERA_DE_RANGO, String.valueOf(min), String.valueOf(max));
            log.warning(() -> "Monto fuera de rango para el tipo de préstamo: " + monto);
            throw new InvalidSolicitudDataException(List.of(mensaje));
        }
    }

    private Integer estadoInicialId() {
        Estado estado = estadoRepository.findByNombre(ESTADO_INICIAL)
                .orElseThrow(() -> new IllegalStateException(ESTADO_INICIAL_NO_CONFIGURADO));
        return estado.getIdEstado();
    }

    private void validateEmail(String email, List<String> errors) {
        if (isBlank(email)) {
            errors.add(EMAIL_REQUERIDO);
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add(EMAIL_INVALIDO);
        }
    }

    private void validateMonto(BigDecimal monto, List<String> errors) {
        if (monto == null) {
            errors.add(MONTO_REQUERIDO);
            return;
        }
        if (monto.compareTo(MONTO_MINIMO_ABSOLUTO) <= 0) {
            errors.add(MONTO_NO_POSITIVO);
        }
        if (monto.stripTrailingZeros().scale() > MONTO_MAX_DECIMALES) {
            errors.add(MONTO_DECIMALES);
        }
    }

    private void validatePlazo(Integer plazo, List<String> errors) {
        if (plazo == null) {
            errors.add(PLAZO_REQUERIDO);
            return;
        }
        if (plazo <= 0) {
            errors.add(PLAZO_NO_POSITIVO);
        } else if (plazo > PLAZO_MAXIMO_MESES) {
            errors.add(PLAZO_FUERA_DE_RANGO);
        }
    }

    private void validateMaxLength(String field, String value, int max, List<String> errors) {
        if (value != null && value.length() > max) {
            errors.add(String.format(LONGITUD_MAXIMA, field, max));
        }
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
