package co.com.crediya.model.auth.gateways;

import co.com.crediya.model.user.User;

/**
 * Puerto de salida: generación del token de sesión (JWT).
 * La implementación vive en infrastructure; el dominio no conoce JWT.
 */
public interface TokenProvider {

    String generateToken(User user, String rolNombre);
}
