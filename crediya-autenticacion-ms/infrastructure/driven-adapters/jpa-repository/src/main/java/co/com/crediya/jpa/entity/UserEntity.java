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
 * Representación de la tabla "usuario" del diagrama.
 * Es distinta de la entidad de dominio User para no acoplar el negocio a JPA.
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    /** Nombres de las restricciones tal como están en la base de datos. */
    public static final String EMAIL_UNIQUE_CONSTRAINT = "usuario_email_key";
    public static final String DOCUMENTO_UNIQUE_CONSTRAINT = "usuario_documento_identidad_key";
    public static final String ROL_FOREIGN_KEY = "fk_usuario_rol";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "documento_identidad", nullable = false, length = 20)
    private String documentoIdentidad;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "id_rol", nullable = false)
    private Integer idRol;

    @Column(name = "salario_base", precision = 12, scale = 2)
    private BigDecimal salarioBase;
}
