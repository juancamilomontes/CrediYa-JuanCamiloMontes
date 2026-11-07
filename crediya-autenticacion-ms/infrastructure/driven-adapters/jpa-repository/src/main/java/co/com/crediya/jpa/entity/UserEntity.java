package co.com.crediya.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representación del usuario en la base de datos (tabla "usuarios").
 * Es distinta de la entidad de dominio User para no acoplar el negocio a JPA.
 */
@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(
        name = UserEntity.EMAIL_UNIQUE_CONSTRAINT, columnNames = "correo_electronico"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    public static final String EMAIL_UNIQUE_CONSTRAINT = "uk_usuarios_correo_electronico";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 200)
    private String direccion;

    @Column(length = 30)
    private String telefono;

    @Column(name = "correo_electronico", nullable = false, length = 150)
    private String correoElectronico;

    @Column(name = "salario_base", nullable = false, precision = 15, scale = 2)
    private BigDecimal salarioBase;
}
