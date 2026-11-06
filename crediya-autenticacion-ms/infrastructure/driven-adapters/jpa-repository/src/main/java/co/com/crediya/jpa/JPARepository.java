package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA. JpaRepository ya incluye CrudRepository y QueryByExampleExecutor.
 */
public interface JPARepository extends JpaRepository<UserEntity, String> {

    boolean existsByCorreoElectronico(String correoElectronico);
}
