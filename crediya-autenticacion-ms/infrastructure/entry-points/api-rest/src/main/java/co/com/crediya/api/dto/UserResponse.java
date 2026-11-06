package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Respuesta JSON con el usuario registrado.
 */
public record UserResponse(
        @JsonProperty("id") String id,
        @JsonProperty("nombres") String nombres,
        @JsonProperty("apellidos") String apellidos,
        @JsonProperty("fecha_nacimiento") LocalDate fechaNacimiento,
        @JsonProperty("direccion") String direccion,
        @JsonProperty("telefono") String telefono,
        @JsonProperty("correo_electronico") String correoElectronico,
        @JsonProperty("salario_base") BigDecimal salarioBase
) {
}
