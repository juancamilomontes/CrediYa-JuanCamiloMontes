package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.RegisterUserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.model.user.User;

/**
 * Convierte entre los DTO de la API y la entidad de dominio User.
 */
public final class UserDtoMapper {

    private UserDtoMapper() {
    }

    public static User toDomain(RegisterUserRequest request) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .email(request.email())
                .documentoIdentidad(request.documentoIdentidad())
                .telefono(request.telefono())
                .idRol(request.idRol())
                .salarioBase(request.salarioBase())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getIdUsuario(),
                user.getNombre(),
                user.getApellido(),
                user.getEmail(),
                user.getDocumentoIdentidad(),
                user.getTelefono(),
                user.getIdRol(),
                user.getSalarioBase()
        );
    }
}
