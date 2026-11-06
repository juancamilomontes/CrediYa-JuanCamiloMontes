package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cuerpo JSON que recibe POST /api/v1/usuarios.
 * Los nombres en snake_case son los de la HU1.
 */
public record RegisterUserRequest(
        @JsonProperty("nombres") String nombres,
        @JsonProperty("apellidos") String apellidos,
        @JsonProperty("fecha_nacimiento") LocalDate fechaNacimiento,
        @JsonProperty("direccion") String direccion,
        @JsonProperty("telefono") String telefono,
        @JsonProperty("correo_electronico") String correoElectronico,
        @JsonProperty("salario_base") BigDecimal salarioBase
) {
}
