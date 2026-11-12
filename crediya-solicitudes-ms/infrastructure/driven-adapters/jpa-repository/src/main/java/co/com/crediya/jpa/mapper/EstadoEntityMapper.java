package co.com.crediya.jpa.mapper;

import co.com.crediya.jpa.entity.EstadoEntity;
import co.com.crediya.model.estado.Estado;

/**
 * Convierte entre la entidad de dominio (Estado) y la entidad de base de datos.
 */
public final class EstadoEntityMapper {

    private EstadoEntityMapper() {
    }

    public static Estado toDomain(EstadoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Estado.builder()
                .idEstado(entity.getIdEstado())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}
