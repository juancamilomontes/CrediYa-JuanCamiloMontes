package co.com.crediya.jpa.mapper;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.user.User;

/**
 * Convierte entre la entidad de dominio (User) y la entidad de base de datos (UserEntity).
 */
public final class UserEntityMapper {

    private UserEntityMapper() {
    }

    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserEntity.builder()
                .idUsuario(user.getIdUsuario())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .email(user.getEmail())
                .documentoIdentidad(user.getDocumentoIdentidad())
                .telefono(user.getTelefono())
                .idRol(user.getIdRol())
                .salarioBase(user.getSalarioBase())
                .build();
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .idUsuario(entity.getIdUsuario())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .email(entity.getEmail())
                .documentoIdentidad(entity.getDocumentoIdentidad())
                .telefono(entity.getTelefono())
                .idRol(entity.getIdRol())
                .salarioBase(entity.getSalarioBase())
                .build();
    }
}
