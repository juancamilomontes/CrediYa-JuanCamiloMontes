package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Cuerpo JSON que recibe POST /api/v1/usuarios.
 * Los nombres coinciden con las columnas de la tabla "usuario" del diagrama.
 */
@Schema(description = "Datos para registrar un usuario")
public record RegisterUserRequest(
        @Schema(example = "Juan Camilo", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
        @JsonProperty("nombre") String nombre,

        @Schema(example = "Montes", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
        @JsonProperty("apellido") String apellido,

        @Schema(example = "juan@mail.com", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 150)
        @JsonProperty("email") String email,

        @Schema(example = "1020304050", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 20)
        @JsonProperty("documento_identidad") String documentoIdentidad,

        @Schema(example = "3001234567", maxLength = 20)
        @JsonProperty("telefono") String telefono,

        @Schema(example = "3", requiredMode = Schema.RequiredMode.REQUIRED,
                description = "1 ADMINISTRADOR, 2 ASESOR, 3 CLIENTE")
        @JsonProperty("id_rol") Integer idRol,

        @Schema(example = "3000000", requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Entre 0 y 15000000, máximo 2 decimales")
        @JsonProperty("salario_base") BigDecimal salarioBase
) {
}
