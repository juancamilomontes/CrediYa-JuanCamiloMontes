package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Cuerpo JSON que recibe POST /api/v1/login: correo y clave.
 */
@Schema(description = "Credenciales de inicio de sesión")
public record LoginRequest(
        @Schema(example = "admin@crediya.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("correo") String correo,

        @Schema(example = "Secreta123", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("clave") String clave
) {
}
