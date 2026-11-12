package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Cuerpo JSON que recibe POST /api/v1/solicitud.
 * Los nombres coinciden con las columnas de la tabla "solicitud" del diagrama.
 */
@Schema(description = "Datos para registrar una solicitud de préstamo")
public record RegisterSolicitudRequest(
        @Schema(example = "5000000", requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Monto solicitado, mayor que 0 y dentro del rango del tipo de préstamo")
        @JsonProperty("monto") BigDecimal monto,

        @Schema(example = "24", requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Plazo en meses, mayor que 0")
        @JsonProperty("plazo") Integer plazo,

        @Schema(example = "cliente@mail.com", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 150)
        @JsonProperty("email") String email,

        @Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Identificador de un tipo de préstamo existente")
        @JsonProperty("id_tipo_prestamo") Integer idTipoPrestamo
) {
}
