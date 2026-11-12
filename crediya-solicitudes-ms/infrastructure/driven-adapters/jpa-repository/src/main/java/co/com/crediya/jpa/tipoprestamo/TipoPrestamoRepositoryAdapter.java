package co.com.crediya.jpa.tipoprestamo;

import co.com.crediya.jpa.mapper.TipoPrestamoEntityMapper;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Driven adapter: implementa el gateway TipoPrestamoRepository del dominio usando JPA.
 */
@Repository
@RequiredArgsConstructor
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {

    private final TipoPrestamoJpaRepository tipoPrestamoJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoPrestamo> findById(Integer idTipoPrestamo) {
        if (idTipoPrestamo == null) {
            return Optional.empty();
        }
        return tipoPrestamoJpaRepository.findById(idTipoPrestamo)
                .map(TipoPrestamoEntityMapper::toDomain);
    }
}
