package co.com.crediya.jpa.tipoprestamo;

import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA de la tabla "tipo_prestamo".
 */
public interface TipoPrestamoJpaRepository extends JpaRepository<TipoPrestamoEntity, Integer> {
}
