package co.com.crediya.model.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de dominio: un rol del sistema (tabla "rol" del diagrama).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Role {
    private Integer idRol;
    private String nombre;
    private String descripcion;
}
