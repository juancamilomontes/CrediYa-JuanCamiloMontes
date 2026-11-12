package co.com.crediya.model.tipoprestamo.gateways;

import co.com.crediya.model.tipoprestamo.TipoPrestamo;

import java.util.Optional;

/**
 * Puerto de salida: consulta de los tipos de préstamo existentes.
 */
public interface TipoPrestamoRepository {

    Optional<TipoPrestamo> findById(Integer idTipoPrestamo);
}
