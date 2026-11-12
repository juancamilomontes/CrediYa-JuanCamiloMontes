package co.com.crediya.jpa.mapper;

import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;

/**
 * Convierte entre la entidad de dominio (TipoPrestamo) y la entidad de base de datos.
 */
public final class TipoPrestamoEntityMapper {

    private TipoPrestamoEntityMapper() {
    }

    public static TipoPrestamo toDomain(TipoPrestamoEntity entity) {
        if (entity == null) {
            return null;
        }
        return TipoPrestamo.builder()
                .idTipoPrestamo(entity.getIdTipoPrestamo())
                .nombre(entity.getNombre())
                .montoMinimo(entity.getMontoMinimo())
                .montoMaximo(entity.getMontoMaximo())
                .tasaInteres(entity.getTasaInteres())
                .validacionAutomatica(entity.getValidacionAutomatica())
                .build();
    }
}
