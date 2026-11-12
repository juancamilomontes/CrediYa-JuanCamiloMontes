package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.SolicitudEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA de la tabla "solicitud".
 */
public interface JPARepository extends JpaRepository<SolicitudEntity, Integer> {
}
