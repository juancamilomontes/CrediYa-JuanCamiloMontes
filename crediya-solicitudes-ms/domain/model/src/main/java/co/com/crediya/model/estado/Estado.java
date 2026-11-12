package co.com.crediya.model.estado;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de dominio: un estado del ciclo de vida de una solicitud
 * (tabla "estados" del diagrama).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Estado {
    private Integer idEstado;
    private String nombre;
    private String descripcion;
}
