package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA de la tabla "usuario".
 */
public interface JPARepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByEmail(String email);

    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}
