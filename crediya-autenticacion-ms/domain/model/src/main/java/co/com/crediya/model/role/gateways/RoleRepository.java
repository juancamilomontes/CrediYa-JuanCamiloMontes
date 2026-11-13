package co.com.crediya.model.role.gateways;

import co.com.crediya.model.role.Role;

import java.util.Optional;

/**
 * Puerto (gateway) de salida para consultar roles.
 */
public interface RoleRepository {

    boolean existsById(Integer idRol);

    Optional<Role> findById(Integer idRol);
}
