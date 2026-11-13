package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Respuesta JSON con el usuario registrado.
 */
public record UserResponse(
        @JsonProperty("id_usuario") Integer idUsuario,
        @JsonProperty("nombre") String nombre,
        @JsonProperty("apellido") String apellido,
        @JsonProperty("email") String email,
        @JsonProperty("documento_identidad") String documentoIdentidad,
        @JsonProperty("telefono") String telefono,
        @JsonProperty("id_rol") Integer idRol,
        @JsonProperty("salario_base") BigDecimal salarioBase
) {
}
