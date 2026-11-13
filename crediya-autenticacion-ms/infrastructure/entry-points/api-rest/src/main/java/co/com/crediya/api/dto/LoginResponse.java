package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta del login: el token JWT y los datos mínimos del usuario.
 */
public record LoginResponse(
        @JsonProperty("token") String token,
        @JsonProperty("tipo") String tipo,
        @JsonProperty("id_usuario") Integer idUsuario,
        @JsonProperty("email") String email,
        @JsonProperty("id_rol") Integer idRol
) {
}
