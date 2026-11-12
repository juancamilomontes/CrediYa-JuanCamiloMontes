package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.solicitud.Solicitud;

/**
 * Puerto (gateway) de salida: lo que el dominio necesita de la persistencia de solicitudes.
 * La implementación vive en infrastructure (driven adapter JPA).
 */
public interface SolicitudRepository {

    Solicitud save(Solicitud solicitud);
}
