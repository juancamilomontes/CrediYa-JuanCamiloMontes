package co.com.crediya.usecase.user;

import co.com.crediya.model.auth.gateways.PasswordHasher;
import co.com.crediya.model.common.TextNormalizer;
import co.com.crediya.model.exceptions.DocumentAlreadyRegisteredException;
import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.exceptions.InvalidUserDataException;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.List;

/**
 * HU1 - Registrar usuarios en el sistema.
 * Una sola responsabilidad: ORQUESTAR el registro (el "qué pasa y en qué orden").
 * - Las reglas de formato las delega a UserValidator.
 * - La limpieza de texto la delega a TextNormalizer.
 * - Aquí solo quedan las reglas que necesitan consultar el sistema (rol, duplicados) y el guardado.
 */
@Log
@RequiredArgsConstructor
public class UserUseCase {

    static final String ROL_INEXISTENTE = "El rol indicado no existe";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;

    public User registerUser(User user) {
        log.info("Iniciando registro de usuario");
        User normalized = normalize(user);
        UserValidator.validate(normalized);
        ensureRoleExists(normalized.getIdRol());
        ensureNotRegistered(normalized);

        User toSave = normalized.toBuilder()
                .idUsuario(null)
                .contrasena(passwordHasher.hash(normalized.getContrasena()))
                .build();

        User saved = userRepository.save(toSave);
        log.info(() -> "Usuario registrado con id " + saved.getIdUsuario());
        return saved;
    }

    private static User normalize(User user) {
        if (user == null) {
            return null;
        }
        return user.toBuilder()
                .nombre(TextNormalizer.trim(user.getNombre()))
                .apellido(TextNormalizer.trim(user.getApellido()))
                .email(TextNormalizer.normalizeEmail(user.getEmail()))
                .documentoIdentidad(TextNormalizer.trim(user.getDocumentoIdentidad()))
                .telefono(TextNormalizer.trim(user.getTelefono()))
                .build();
    }

    private void ensureRoleExists(Integer idRol) {
        if (!roleRepository.existsById(idRol)) {
            log.warning(() -> "Rol inexistente: " + idRol);
            throw new InvalidUserDataException(List.of(ROL_INEXISTENTE));
        }
    }

    private void ensureNotRegistered(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warning("Intento de registro con email ya existente");
            throw new EmailAlreadyRegisteredException(user.getEmail());
        }
        if (userRepository.existsByDocumentoIdentidad(user.getDocumentoIdentidad())) {
            log.warning("Intento de registro con documento ya existente");
            throw new DocumentAlreadyRegisteredException(user.getDocumentoIdentidad());
        }
    }
}
