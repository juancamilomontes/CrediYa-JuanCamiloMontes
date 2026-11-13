package co.com.crediya.jpa.estado;

import co.com.crediya.jpa.mapper.EstadoEntityMapper;
import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Driven adapter: implementa el gateway EstadoRepository del dominio usando JPA.
 */
@Repository
@RequiredArgsConstructor
public class EstadoRepositoryAdapter implements EstadoRepository {

    private final EstadoJpaRepository estadoJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Estado> findByNombre(String nombre) {
        return estadoJpaRepository.findByNombre(nombre)
                .map(EstadoEntityMapper::toDomain);
    }
}
