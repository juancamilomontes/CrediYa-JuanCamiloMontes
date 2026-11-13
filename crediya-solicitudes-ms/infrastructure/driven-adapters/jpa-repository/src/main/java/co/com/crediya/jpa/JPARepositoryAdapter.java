package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.SolicitudEntity;
import co.com.crediya.jpa.mapper.SolicitudEntityMapper;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Driven adapter: implementa el gateway SolicitudRepository del dominio usando JPA (tabla "solicitud").
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JPARepositoryAdapter implements SolicitudRepository {

    private final JPARepository repository;

    @Override
    @Transactional
    public Solicitud save(Solicitud solicitud) {
        log.debug("Guardando solicitud en base de datos");
        SolicitudEntity saved = repository.saveAndFlush(SolicitudEntityMapper.toEntity(solicitud));
        log.debug("Solicitud guardada con id {}", saved.getIdSolicitud());
        return SolicitudEntityMapper.toDomain(saved);
    }
}
