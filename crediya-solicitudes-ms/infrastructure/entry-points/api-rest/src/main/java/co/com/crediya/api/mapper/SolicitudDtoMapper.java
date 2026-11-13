package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.RegisterSolicitudRequest;
import co.com.crediya.api.dto.SolicitudResponse;
import co.com.crediya.model.solicitud.Solicitud;

/**
 * Convierte entre los DTO de la API y la entidad de dominio Solicitud.
 */
public final class SolicitudDtoMapper {

    private SolicitudDtoMapper() {
    }

    public static Solicitud toDomain(RegisterSolicitudRequest request) {
        if (request == null) {
            return null;
        }
        return Solicitud.builder()
                .monto(request.monto())
                .plazo(request.plazo())
                .email(request.email())
                .idTipoPrestamo(request.idTipoPrestamo())
                .build();
    }

    public static SolicitudResponse toResponse(Solicitud solicitud) {
        return new SolicitudResponse(
                solicitud.getIdSolicitud(),
                solicitud.getMonto(),
                solicitud.getPlazo(),
                solicitud.getEmail(),
                solicitud.getIdEstado(),
                solicitud.getIdTipoPrestamo()
        );
    }
}
