package co.com.crediya.jpa.mapper;

import co.com.crediya.jpa.entity.SolicitudEntity;
import co.com.crediya.model.solicitud.Solicitud;

/**
 * Convierte entre la entidad de dominio (Solicitud) y la entidad de base de datos (SolicitudEntity).
 */
public final class SolicitudEntityMapper {

    private SolicitudEntityMapper() {
    }

    public static SolicitudEntity toEntity(Solicitud solicitud) {
        if (solicitud == null) {
            return null;
        }
        return SolicitudEntity.builder()
                .idSolicitud(solicitud.getIdSolicitud())
                .monto(solicitud.getMonto())
                .plazo(solicitud.getPlazo())
                .email(solicitud.getEmail())
                .idEstado(solicitud.getIdEstado())
                .idTipoPrestamo(solicitud.getIdTipoPrestamo())
                .build();
    }

    public static Solicitud toDomain(SolicitudEntity entity) {
        if (entity == null) {
            return null;
        }
        return Solicitud.builder()
                .idSolicitud(entity.getIdSolicitud())
                .monto(entity.getMonto())
                .plazo(entity.getPlazo())
                .email(entity.getEmail())
                .idEstado(entity.getIdEstado())
                .idTipoPrestamo(entity.getIdTipoPrestamo())
                .build();
    }
}
