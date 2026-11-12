package co.com.crediya.model.estado.gateways;

import co.com.crediya.model.estado.Estado;

import java.util.Optional;

/**
 * Puerto de salida: consulta de los estados de las solicitudes.
 */
public interface EstadoRepository {

    Optional<Estado> findByNombre(String nombre);
}
