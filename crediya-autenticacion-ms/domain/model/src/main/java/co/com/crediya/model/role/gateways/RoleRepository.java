package co.com.crediya.model.role.gateways;

/**
 * Puerto (gateway) de salida para consultar roles.
 */
public interface RoleRepository {

    boolean existsById(Integer idRol);
}
