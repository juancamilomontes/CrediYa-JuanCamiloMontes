package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;

/**
 * Puerto (gateway) de salida: lo que el dominio necesita de la persistencia de usuarios.
 * La implementación vive en infrastructure (driven adapter JPA).
 */
public interface UserRepository {

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}
