package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cuerpo JSON que recibe POST /api/v1/usuarios.
 * Los nombres en snake_case son los de la HU1.
 */
@Schema(description = "Datos para registrar un usuario")
public record RegisterUserRequest(
        @Schema(example = "Juan Camilo", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
        @JsonProperty("nombres") String nombres,

        @Schema(example = "Montes", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
        @JsonProperty("apellidos") String apellidos,

        @Schema(example = "2000-01-15", description = "Formato yyyy-MM-dd, no puede ser futura")
        @JsonProperty("fecha_nacimiento") LocalDate fechaNacimiento,

        @Schema(example = "Calle 10 # 20-30", maxLength = 200)
        @JsonProperty("direccion") String direccion,

        @Schema(example = "3001234567", maxLength = 30)
        @JsonProperty("telefono") String telefono,

        @Schema(example = "juan@mail.com", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 150)
        @JsonProperty("correo_electronico") String correoElectronico,

        @Schema(example = "3000000", requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Entre 0 y 15000000, máximo 2 decimales")
        @JsonProperty("salario_base") BigDecimal salarioBase
) {
}
