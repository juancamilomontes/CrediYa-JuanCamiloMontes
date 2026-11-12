package co.com.crediya.model.solicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad de dominio: una solicitud de préstamo (tabla "solicitud" del diagrama).
 * No conoce nada de base de datos ni de HTTP: solo el negocio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    private Integer idSolicitud;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private Integer idEstado;
    private Integer idTipoPrestamo;
}
