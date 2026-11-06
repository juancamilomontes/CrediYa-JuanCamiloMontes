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
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .fechaNacimiento(request.fechaNacimiento())
                .direccion(request.direccion())
                .telefono(request.telefono())
                .correoElectronico(request.correoElectronico())
                .salarioBase(request.salarioBase())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNombres(),
                user.getApellidos(),
                user.getFechaNacimiento(),
                user.getDireccion(),
                user.getTelefono(),
                user.getCorreoElectronico(),
                user.getSalarioBase()
        );
    }
}
