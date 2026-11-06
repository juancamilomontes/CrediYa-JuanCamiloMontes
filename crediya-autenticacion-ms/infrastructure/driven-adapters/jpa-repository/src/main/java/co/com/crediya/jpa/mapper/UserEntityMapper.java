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
                .id(user.getId())
                .nombres(user.getNombres())
                .apellidos(user.getApellidos())
                .fechaNacimiento(user.getFechaNacimiento())
                .direccion(user.getDireccion())
                .telefono(user.getTelefono())
                .correoElectronico(user.getCorreoElectronico())
                .salarioBase(user.getSalarioBase())
                .build();
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .fechaNacimiento(entity.getFechaNacimiento())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .correoElectronico(entity.getCorreoElectronico())
                .salarioBase(entity.getSalarioBase())
                .build();
    }
}
