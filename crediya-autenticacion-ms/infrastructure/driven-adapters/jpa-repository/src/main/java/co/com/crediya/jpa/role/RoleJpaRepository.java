package co.com.crediya.jpa.role;

import co.com.crediya.jpa.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA de la tabla "rol".
 */
public interface RoleJpaRepository extends JpaRepository<RoleEntity, Integer> {
}
