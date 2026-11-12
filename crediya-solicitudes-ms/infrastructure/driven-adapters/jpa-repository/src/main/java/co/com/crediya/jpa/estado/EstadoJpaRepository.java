package co.com.crediya.jpa.estado;

import co.com.crediya.jpa.entity.EstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA de la tabla "estados".
 */
public interface EstadoJpaRepository extends JpaRepository<EstadoEntity, Integer> {

    Optional<EstadoEntity> findByNombre(String nombre);
}
