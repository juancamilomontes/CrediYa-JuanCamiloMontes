package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;

/**
 * Puerto (gateway) de salida: lo que el dominio necesita de la persistencia.
 * La implementación vive en infrastructure (driven adapter JPA).
 */
public interface UserRepository {

    User save(User user);

    boolean existsByCorreoElectronico(String correoElectronico);
}
