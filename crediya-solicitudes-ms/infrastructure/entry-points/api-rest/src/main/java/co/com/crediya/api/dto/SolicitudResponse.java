package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Respuesta JSON con la solicitud registrada.
 */
public record SolicitudResponse(
        @JsonProperty("id_solicitud") Integer idSolicitud,
        @JsonProperty("monto") BigDecimal monto,
        @JsonProperty("plazo") Integer plazo,
        @JsonProperty("email") String email,
        @JsonProperty("id_estado") Integer idEstado,
        @JsonProperty("id_tipo_prestamo") Integer idTipoPrestamo
) {
}
