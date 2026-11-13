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
 * Representación de la tabla "tipo_prestamo" del diagrama.
 */
@Entity
@Table(name = "tipo_prestamo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoPrestamoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_prestamo")
    private Integer idTipoPrestamo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "monto_minimo", precision = 15, scale = 2)
    private BigDecimal montoMinimo;

    @Column(name = "monto_maximo", precision = 15, scale = 2)
    private BigDecimal montoMaximo;

    @Column(name = "tasa_interes", precision = 6, scale = 3)
    private BigDecimal tasaInteres;

    @Column(name = "validacion_automatica", nullable = false)
    private Boolean validacionAutomatica;
}
