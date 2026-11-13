package co.com.crediya.jpa.role;

import co.com.crediya.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Driven adapter: implementa el gateway RoleRepository del dominio usando JPA.
 */
@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer idRol) {
        return idRol != null && roleJpaRepository.existsById(idRol);
    }
}
