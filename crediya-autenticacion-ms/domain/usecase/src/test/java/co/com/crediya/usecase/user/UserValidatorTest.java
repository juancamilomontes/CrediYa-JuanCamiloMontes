package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.InvalidUserDataException;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba las reglas de FORMATO. No necesita mocks: el validador no depende de la base de datos.
 */
class UserValidatorTest {

    private User validUser() {
        return User.builder()
                .nombre("Ana")
                .apellido("Gomez")
                .email("ana@mail.com")
                .documentoIdentidad("1020304050")
                .telefono("3001234567")
                .idRol(3)
                .salarioBase(new BigDecimal("2500000"))
                .contrasena("Secreta123")
                .build();
    }

    private List<String> errorsOf(User user) {
        return assertThrows(InvalidUserDataException.class, () -> UserValidator.validate(user)).getErrors();
    }

    @Test
    void shouldAcceptValidUser() {
        assertDoesNotThrow(() -> UserValidator.validate(validUser()));
    }

    @Test
    void shouldAcceptMissingOptionalPhone() {
        assertDoesNotThrow(() -> UserValidator.validate(validUser().toBuilder().telefono(null).build()));
    }

    @Test
    void shouldReportAllMissingRequiredFields() {
        List<String> errors = errorsOf(User.builder().nombre(" ").build());

        assertEquals(7, errors.size());
        assertTrue(errors.contains(UserValidator.NOMBRE_REQUERIDO));
        assertTrue(errors.contains(UserValidator.APELLIDO_REQUERIDO));
        assertTrue(errors.contains(UserValidator.EMAIL_REQUERIDO));
        assertTrue(errors.contains(UserValidator.DOCUMENTO_REQUERIDO));
        assertTrue(errors.contains(UserValidator.ROL_REQUERIDO));
        assertTrue(errors.contains(UserValidator.SALARIO_REQUERIDO));
        assertTrue(errors.contains(UserValidator.CONTRASENA_REQUERIDA));
    }

    @Test
    void shouldRejectNullUser() {
        assertThrows(InvalidUserDataException.class, () -> UserValidator.validate(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ana", "ana@", "ana@mail", "@mail.com", "ana mail@mail.com"})
    void shouldRejectInvalidEmailFormat(String email) {
        assertTrue(errorsOf(validUser().toBuilder().email(email).build()).contains(UserValidator.EMAIL_INVALIDO));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "15000000.01", "20000000"})
    void shouldRejectSalaryOutOfRange(String salary) {
        User user = validUser().toBuilder().salarioBase(new BigDecimal(salary)).build();
        assertTrue(errorsOf(user).contains(UserValidator.SALARIO_FUERA_DE_RANGO));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "15000000", "2500000.500"})
    void shouldAcceptValidSalaries(String salary) {
        User user = validUser().toBuilder().salarioBase(new BigDecimal(salary)).build();
        assertDoesNotThrow(() -> UserValidator.validate(user));
    }

    @Test
    void shouldRejectSalaryWithMoreThanTwoDecimals() {
        User user = validUser().toBuilder().salarioBase(new BigDecimal("2500000.123")).build();
        assertTrue(errorsOf(user).contains(UserValidator.SALARIO_DECIMALES));
    }

    @Test
    void shouldRejectShortPassword() {
        assertTrue(errorsOf(validUser().toBuilder().contrasena("123").build())
                .contains(UserValidator.CONTRASENA_CORTA));
    }

    @Test
    void shouldRejectTooLongPassword() {
        assertTrue(errorsOf(validUser().toBuilder().contrasena("x".repeat(73)).build())
                .contains("El campo contrasena no puede tener más de 72 caracteres"));
    }

    @Test
    void shouldRejectFieldsLongerThanAllowed() {
        User user = validUser().toBuilder()
                .nombre("a".repeat(101))
                .apellido("b".repeat(101))
                .documentoIdentidad("1".repeat(21))
                .telefono("3".repeat(21))
                .email("x".repeat(145) + "@mail.com")
                .build();

        List<String> errors = errorsOf(user);

        assertTrue(errors.contains("El campo nombre no puede tener más de 100 caracteres"));
        assertTrue(errors.contains("El campo apellido no puede tener más de 100 caracteres"));
        assertTrue(errors.contains("El campo documento_identidad no puede tener más de 20 caracteres"));
        assertTrue(errors.contains("El campo telefono no puede tener más de 20 caracteres"));
        assertTrue(errors.contains("El campo email no puede tener más de 150 caracteres"));
    }
}
