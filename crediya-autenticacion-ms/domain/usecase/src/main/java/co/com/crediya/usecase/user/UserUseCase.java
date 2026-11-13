package co.com.crediya.usecase.user;

import co.com.crediya.model.auth.gateways.PasswordHasher;
import co.com.crediya.model.exceptions.DocumentAlreadyRegisteredException;
import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.exceptions.InvalidUserDataException;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * HU1 - Registrar usuarios en el sistema.
 * Aquí viven las reglas de negocio; no depende de Spring, HTTP ni base de datos.
 */
@Log
@RequiredArgsConstructor
public class UserUseCase {

    static final BigDecimal SALARIO_MINIMO = BigDecimal.ZERO;
    static final BigDecimal SALARIO_MAXIMO = new BigDecimal("15000000");
    static final int SALARIO_MAX_DECIMALES = 2;

    static final int MAX_NOMBRE = 100;
    static final int MAX_APELLIDO = 100;
    static final int MAX_EMAIL = 150;
    static final int MAX_DOCUMENTO = 20;
    static final int MAX_TELEFONO = 20;
    static final int MIN_CONTRASENA = 8;
    static final int MAX_CONTRASENA = 72;

    static final String NOMBRE_REQUERIDO = "El campo nombre es obligatorio";
    static final String APELLIDO_REQUERIDO = "El campo apellido es obligatorio";
    static final String EMAIL_REQUERIDO = "El campo email es obligatorio";
    static final String EMAIL_INVALIDO = "El email no tiene un formato válido";
    static final String DOCUMENTO_REQUERIDO = "El campo documento_identidad es obligatorio";
    static final String ROL_REQUERIDO = "El campo id_rol es obligatorio";
    static final String ROL_INEXISTENTE = "El rol indicado no existe";
    static final String SALARIO_REQUERIDO = "El campo salario_base es obligatorio";
    static final String SALARIO_FUERA_DE_RANGO = "El salario_base debe estar entre 0 y 15000000";
    static final String SALARIO_DECIMALES = "El salario_base no puede tener más de 2 decimales";
    static final String CONTRASENA_REQUERIDA = "El campo contrasena es obligatorio";
    static final String CONTRASENA_CORTA = "La contrasena debe tener al menos 8 caracteres";
    private static final String LONGITUD_MAXIMA = "El campo %s no puede tener más de %d caracteres";

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;

    public User registerUser(User user) {
        log.info("Iniciando registro de usuario");
        User normalizedUser = normalize(user);
        validate(normalizedUser);
        validateRoleExists(normalizedUser.getIdRol());
        validateNotRegistered(normalizedUser);

        User toSave = normalizedUser.toBuilder()
                .contrasena(passwordHasher.hash(normalizedUser.getContrasena()))
                .build();

        User savedUser = userRepository.save(toSave);
        log.info(() -> "Usuario registrado con id " + savedUser.getIdUsuario());
        return savedUser;
    }

    private User normalize(User user) {
        if (user == null) {
            throw new InvalidUserDataException(List.of("Los datos del usuario son obligatorios"));
        }
        return user.toBuilder()
                .idUsuario(null)
                .nombre(trim(user.getNombre()))
                .apellido(trim(user.getApellido()))
                .email(normalizeEmail(user.getEmail()))
                .documentoIdentidad(trim(user.getDocumentoIdentidad()))
                .telefono(trim(user.getTelefono()))
                .build();
    }

    private void validate(User user) {
        List<String> errors = new ArrayList<>();
        validateRequired(user.getNombre(), NOMBRE_REQUERIDO, errors);
        validateRequired(user.getApellido(), APELLIDO_REQUERIDO, errors);
        validateEmail(user.getEmail(), errors);
        validateRequired(user.getDocumentoIdentidad(), DOCUMENTO_REQUERIDO, errors);
        if (user.getIdRol() == null) {
            errors.add(ROL_REQUERIDO);
        }
        validateSalary(user.getSalarioBase(), errors);
        validateContrasena(user.getContrasena(), errors);
        validateMaxLength("nombre", user.getNombre(), MAX_NOMBRE, errors);
        validateMaxLength("apellido", user.getApellido(), MAX_APELLIDO, errors);
        validateMaxLength("email", user.getEmail(), MAX_EMAIL, errors);
        validateMaxLength("documento_identidad", user.getDocumentoIdentidad(), MAX_DOCUMENTO, errors);
        validateMaxLength("telefono", user.getTelefono(), MAX_TELEFONO, errors);

        if (!errors.isEmpty()) {
            log.warning(() -> "Datos de usuario inválidos: " + errors);
            throw new InvalidUserDataException(errors);
        }
    }

    private void validateRoleExists(Integer idRol) {
        if (!roleRepository.existsById(idRol)) {
            log.warning(() -> "Rol inexistente: " + idRol);
            throw new InvalidUserDataException(List.of(ROL_INEXISTENTE));
        }
    }

    private void validateNotRegistered(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warning("Intento de registro con email ya existente");
            throw new EmailAlreadyRegisteredException(user.getEmail());
        }
        if (userRepository.existsByDocumentoIdentidad(user.getDocumentoIdentidad())) {
            log.warning("Intento de registro con documento ya existente");
            throw new DocumentAlreadyRegisteredException(user.getDocumentoIdentidad());
        }
    }

    private void validateRequired(String value, String message, List<String> errors) {
        if (isBlank(value)) {
            errors.add(message);
        }
    }

    private void validateEmail(String email, List<String> errors) {
        if (isBlank(email)) {
            errors.add(EMAIL_REQUERIDO);
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add(EMAIL_INVALIDO);
        }
    }

    private void validateSalary(BigDecimal salary, List<String> errors) {
        if (salary == null) {
            errors.add(SALARIO_REQUERIDO);
            return;
        }
        if (salary.compareTo(SALARIO_MINIMO) < 0 || salary.compareTo(SALARIO_MAXIMO) > 0) {
            errors.add(SALARIO_FUERA_DE_RANGO);
        }
        if (salary.stripTrailingZeros().scale() > SALARIO_MAX_DECIMALES) {
            errors.add(SALARIO_DECIMALES);
        }
    }

    private void validateContrasena(String contrasena, List<String> errors) {
        if (isBlank(contrasena)) {
            errors.add(CONTRASENA_REQUERIDA);
            return;
        }
        if (contrasena.length() < MIN_CONTRASENA) {
            errors.add(CONTRASENA_CORTA);
        }
        if (contrasena.length() > MAX_CONTRASENA) {
            errors.add(String.format(LONGITUD_MAXIMA, "contrasena", MAX_CONTRASENA));
        }
    }

    private void validateMaxLength(String field, String value, int max, List<String> errors) {
        if (value != null && value.length() > max) {
            errors.add(String.format(LONGITUD_MAXIMA, field, max));
        }
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
