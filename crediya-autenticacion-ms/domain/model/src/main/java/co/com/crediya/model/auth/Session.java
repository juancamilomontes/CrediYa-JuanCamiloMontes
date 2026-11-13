package co.com.crediya.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Resultado de un login exitoso: el token y los datos mínimos del usuario autenticado.
 */
@Getter
@AllArgsConstructor
@Builder
public class Session {
    private final String token;
    private final Integer idUsuario;
    private final String email;
    private final Integer idRol;
}
