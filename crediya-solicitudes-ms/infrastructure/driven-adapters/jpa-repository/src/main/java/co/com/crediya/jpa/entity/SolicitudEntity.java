package co.com.crediya.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Representación de la tabla "solicitud" del diagrama.
 * Es distinta de la entidad de dominio Solicitud para no acoplar el negocio a JPA.
 */
@Entity
@Table(name = "solicitud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudEntity {

    /** Nombres de las restricciones tal como están en la base de datos. */
    public static final String ESTADO_FOREIGN_KEY = "fk_solicitud_estado";
    public static final String TIPO_PRESTAMO_FOREIGN_KEY = "fk_solicitud_tipo_prestamo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "plazo", nullable = false)
    private Integer plazo;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "id_estado", nullable = false)
    private Integer idEstado;

    @Column(name = "id_tipo_prestamo", nullable = false)
    private Integer idTipoPrestamo;
}
