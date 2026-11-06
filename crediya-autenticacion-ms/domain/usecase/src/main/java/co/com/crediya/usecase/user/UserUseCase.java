package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.exceptions.InvalidUserDataException;
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

    static final String NOMBRES_REQUERIDO = "El campo nombres es obligatorio";
    static final String APELLIDOS_REQUERIDO = "El campo apellidos es obligatorio";
    static final String CORREO_REQUERIDO = "El campo correo_electronico es obligatorio";
    static final String CORREO_INVALIDO = "El correo_electronico no tiene un formato válido";
    static final String SALARIO_REQUERIDO = "El campo salario_base es obligatorio";
    static final String SALARIO_FUERA_DE_RANGO = "El salario_base debe estar entre 0 y 15000000";

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;

    public User registerUser(User user) {
        log.info("Iniciando registro de usuario");
        User normalizedUser = normalize(user);
        validate(normalizedUser);

        if (userRepository.existsByCorreoElectronico(normalizedUser.getCorreoElectronico())) {
            log.warning("Intento de registro con correo ya existente");
            throw new EmailAlreadyRegisteredException(normalizedUser.getCorreoElectronico());
        }

        User savedUser = userRepository.save(normalizedUser);
        log.info(() -> "Usuario registrado con id " + savedUser.getId());
        return savedUser;
    }

    private User normalize(User user) {
        if (user == null) {
            throw new InvalidUserDataException(List.of("Los datos del usuario son obligatorios"));
        }
        return user.toBuilder()
                .id(null)
                .nombres(trim(user.getNombres()))
                .apellidos(trim(user.getApellidos()))
                .direccion(trim(user.getDireccion()))
                .telefono(trim(user.getTelefono()))
                .correoElectronico(normalizeEmail(user.getCorreoElectronico()))
                .build();
    }

    private void validate(User user) {
        List<String> errors = new ArrayList<>();
        validateRequired(user.getNombres(), NOMBRES_REQUERIDO, errors);
        validateRequired(user.getApellidos(), APELLIDOS_REQUERIDO, errors);
        validateEmail(user.getCorreoElectronico(), errors);
        validateSalary(user.getSalarioBase(), errors);

        if (!errors.isEmpty()) {
            log.warning(() -> "Datos de usuario inválidos: " + errors);
            throw new InvalidUserDataException(errors);
        }
    }

    private void validateRequired(String value, String message, List<String> errors) {
        if (isBlank(value)) {
            errors.add(message);
        }
    }

    private void validateEmail(String email, List<String> errors) {
        if (isBlank(email)) {
            errors.add(CORREO_REQUERIDO);
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add(CORREO_INVALIDO);
        }
    }

    private void validateSalary(BigDecimal salary, List<String> errors) {
        if (salary == null) {
            errors.add(SALARIO_REQUERIDO);
        } else if (salary.compareTo(SALARIO_MINIMO) < 0 || salary.compareTo(SALARIO_MAXIMO) > 0) {
            errors.add(SALARIO_FUERA_DE_RANGO);
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
