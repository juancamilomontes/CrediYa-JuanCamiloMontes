package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.exceptions.InvalidUserDataException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository);
    }

    private User validUser() {
        return User.builder()
                .nombres("Ana")
                .apellidos("Gomez")
                .fechaNacimiento(LocalDate.of(1995, 5, 10))
                .direccion("Calle 1")
                .telefono("3001234567")
                .correoElectronico("ana@mail.com")
                .salarioBase(new BigDecimal("2500000"))
                .build();
    }

    @Test
    void shouldRegisterValidUser() {
        when(userRepository.existsByCorreoElectronico("ana@mail.com")).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> ((User) invocation.getArgument(0)).toBuilder().id("1").build());

        User saved = userUseCase.registerUser(validUser());

        assertEquals("1", saved.getId());
        assertEquals("ana@mail.com", saved.getCorreoElectronico());
    }

    @Test
    void shouldNormalizeEmailAndTrimNamesBeforeSaving() {
        User input = validUser().toBuilder()
                .id("id-enviado-por-cliente")
                .nombres("  Ana  ")
                .correoElectronico("  ANA@Mail.com ")
                .build();
        when(userRepository.existsByCorreoElectronico("ana@mail.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userUseCase.registerUser(input);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("Ana", captor.getValue().getNombres());
        assertEquals("ana@mail.com", captor.getValue().getCorreoElectronico());
        assertNull(captor.getValue().getId());
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        when(userRepository.existsByCorreoElectronico("ana@mail.com")).thenReturn(true);

        User user = validUser();
        assertThrows(EmailAlreadyRegisteredException.class, () -> userUseCase.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReportAllMissingRequiredFields() {
        User empty = User.builder().nombres(" ").build();

        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(empty));

        assertEquals(4, ex.getErrors().size());
        assertTrue(ex.getErrors().contains(UserUseCase.NOMBRES_REQUERIDO));
        assertTrue(ex.getErrors().contains(UserUseCase.APELLIDOS_REQUERIDO));
        assertTrue(ex.getErrors().contains(UserUseCase.CORREO_REQUERIDO));
        assertTrue(ex.getErrors().contains(UserUseCase.SALARIO_REQUERIDO));
        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ana", "ana@", "ana@mail", "@mail.com", "ana mail@mail.com"})
    void shouldRejectInvalidEmailFormat(String email) {
        User user = validUser().toBuilder().correoElectronico(email).build();

        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(user));

        assertTrue(ex.getErrors().contains(UserUseCase.CORREO_INVALIDO));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "15000000.01", "20000000"})
    void shouldRejectSalaryOutOfRange(String salary) {
        User user = validUser().toBuilder().salarioBase(new BigDecimal(salary)).build();

        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(user));

        assertTrue(ex.getErrors().contains(UserUseCase.SALARIO_FUERA_DE_RANGO));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "15000000"})
    void shouldAcceptSalaryAtRangeLimits(String salary) {
        when(userRepository.existsByCorreoElectronico(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = validUser().toBuilder().salarioBase(new BigDecimal(salary)).build();

        assertEquals(new BigDecimal(salary), userUseCase.registerUser(user).getSalarioBase());
    }

    @Test
    void shouldRejectNullUser() {
        assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(null));
    }

    @Test
    void shouldRejectFieldsLongerThanAllowed() {
        User user = validUser().toBuilder()
                .nombres("a".repeat(101))
                .apellidos("b".repeat(101))
                .direccion("c".repeat(201))
                .telefono("1".repeat(31))
                .correoElectronico("x".repeat(145) + "@mail.com")
                .build();

        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(user));

        assertTrue(ex.getErrors().contains("El campo nombres no puede tener más de 100 caracteres"));
        assertTrue(ex.getErrors().contains("El campo apellidos no puede tener más de 100 caracteres"));
        assertTrue(ex.getErrors().contains("El campo direccion no puede tener más de 200 caracteres"));
        assertTrue(ex.getErrors().contains("El campo telefono no puede tener más de 30 caracteres"));
        assertTrue(ex.getErrors().contains("El campo correo_electronico no puede tener más de 150 caracteres"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRejectSalaryWithMoreThanTwoDecimals() {
        User user = validUser().toBuilder().salarioBase(new BigDecimal("2500000.123")).build();

        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(user));

        assertTrue(ex.getErrors().contains(UserUseCase.SALARIO_DECIMALES));
    }

    @Test
    void shouldAcceptSalaryWithTrailingZeroDecimals() {
        when(userRepository.existsByCorreoElectronico(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = validUser().toBuilder().salarioBase(new BigDecimal("2500000.500")).build();

        assertEquals(new BigDecimal("2500000.500"), userUseCase.registerUser(user).getSalarioBase());
    }

    @Test
    void shouldRejectBirthDateInTheFuture() {
        User user = validUser().toBuilder().fechaNacimiento(LocalDate.now().plusDays(1)).build();

        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(user));

        assertTrue(ex.getErrors().contains(UserUseCase.FECHA_NACIMIENTO_FUTURA));
    }

    @Test
    void shouldAcceptMissingOptionalFields() {
        when(userRepository.existsByCorreoElectronico(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = validUser().toBuilder().fechaNacimiento(null).direccion(null).telefono(null).build();

        assertEquals("ana@mail.com", userUseCase.registerUser(user).getCorreoElectronico());
    }
}
