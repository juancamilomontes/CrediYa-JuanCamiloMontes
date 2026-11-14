package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.InvalidUserDataException;
import co.com.crediya.model.user.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reglas de FORMATO de los datos de un usuario (HU1): obligatorios, formatos, rangos y largos.
 * Una sola responsabilidad: decir si los datos están bien escritos. No consulta la base de datos;
 * las reglas que necesitan la base (rol existente, email/documento repetidos) viven en UserUseCase.
 */
public final class UserValidator {

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
    static final String SALARIO_REQUERIDO = "El campo salario_base es obligatorio";
    static final String SALARIO_FUERA_DE_RANGO = "El salario_base debe estar entre 0 y 15000000";
    static final String SALARIO_DECIMALES = "El salario_base no puede tener más de 2 decimales";
    static final String CONTRASENA_REQUERIDA = "El campo contrasena es obligatorio";
    static final String CONTRASENA_CORTA = "La contrasena debe tener al menos 8 caracteres";
    private static final String LONGITUD_MAXIMA = "El campo %s no puede tener más de %d caracteres";

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private UserValidator() {
    }

    /**
     * Revisa todos los campos y junta TODOS los errores en una lista.
     * Si hay al menos uno, lanza InvalidUserDataException (el API responde 400 con la lista).
     */
    public static void validate(User user) {
        if (user == null) {
            throw new InvalidUserDataException(List.of("Los datos del usuario son obligatorios"));
        }
        List<String> errors = new ArrayList<>();
        required(user.getNombre(), NOMBRE_REQUERIDO, errors);
        required(user.getApellido(), APELLIDO_REQUERIDO, errors);
        email(user.getEmail(), errors);
        required(user.getDocumentoIdentidad(), DOCUMENTO_REQUERIDO, errors);
        if (user.getIdRol() == null) {
            errors.add(ROL_REQUERIDO);
        }
        salary(user.getSalarioBase(), errors);
        password(user.getContrasena(), errors);
        maxLength("nombre", user.getNombre(), MAX_NOMBRE, errors);
        maxLength("apellido", user.getApellido(), MAX_APELLIDO, errors);
        maxLength("email", user.getEmail(), MAX_EMAIL, errors);
        maxLength("documento_identidad", user.getDocumentoIdentidad(), MAX_DOCUMENTO, errors);
        maxLength("telefono", user.getTelefono(), MAX_TELEFONO, errors);

        if (!errors.isEmpty()) {
            throw new InvalidUserDataException(errors);
        }
    }

    private static void required(String value, String message, List<String> errors) {
        if (isBlank(value)) {
            errors.add(message);
        }
    }

    private static void email(String email, List<String> errors) {
        if (isBlank(email)) {
            errors.add(EMAIL_REQUERIDO);
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add(EMAIL_INVALIDO);
        }
    }

    private static void salary(BigDecimal salary, List<String> errors) {
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

    private static void password(String password, List<String> errors) {
        if (isBlank(password)) {
            errors.add(CONTRASENA_REQUERIDA);
            return;
        }
        if (password.length() < MIN_CONTRASENA) {
            errors.add(CONTRASENA_CORTA);
        }
        if (password.length() > MAX_CONTRASENA) {
            errors.add(String.format(LONGITUD_MAXIMA, "contrasena", MAX_CONTRASENA));
        }
    }

    private static void maxLength(String field, String value, int max, List<String> errors) {
        if (value != null && value.length() > max) {
            errors.add(String.format(LONGITUD_MAXIMA, field, max));
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
