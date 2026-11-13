package co.com.crediya.usecase.user;

import co.com.crediya.model.exceptions.DocumentAlreadyRegisteredException;
import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.exceptions.InvalidUserDataException;
import co.com.crediya.model.role.gateways.RoleRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    private static final Integer ROL_CLIENTE = 3;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository, roleRepository);
    }

    private User validUser() {
        return User.builder()
                .nombre("Ana")
                .apellido("Gomez")
                .email("ana@mail.com")
                .documentoIdentidad("1020304050")
                .telefono("3001234567")
                .idRol(ROL_CLIENTE)
                .salarioBase(new BigDecimal("2500000"))
                .build();
    }

    private void givenNothingRegistered() {
        lenient().when(roleRepository.existsById(any())).thenReturn(true);
        lenient().when(userRepository.existsByEmail(any())).thenReturn(false);
        lenient().when(userRepository.existsByDocumentoIdentidad(any())).thenReturn(false);
        lenient().when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldRegisterValidUser() {
        givenNothingRegistered();
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> ((User) invocation.getArgument(0)).toBuilder().idUsuario(1).build());

        User saved = userUseCase.registerUser(validUser());

        assertEquals(1, saved.getIdUsuario());
        assertEquals("ana@mail.com", saved.getEmail());
        assertEquals(ROL_CLIENTE, saved.getIdRol());
    }

    @Test
    void shouldNormalizeEmailAndTrimFieldsBeforeSaving() {
        givenNothingRegistered();
        User input = validUser().toBuilder()
                .idUsuario(99)
                .nombre("  Ana  ")
                .documentoIdentidad(" 1020304050 ")
                .email("  ANA@Mail.com ")
                .build();

        userUseCase.registerUser(input);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("Ana", captor.getValue().getNombre());
        assertEquals("1020304050", captor.getValue().getDocumentoIdentidad());
        assertEquals("ana@mail.com", captor.getValue().getEmail());
        assertNull(captor.getValue().getIdUsuario());
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        givenNothingRegistered();
        when(userRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        User user = validUser();
        assertThrows(EmailAlreadyRegisteredException.class, () -> userUseCase.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicatedDocument() {
        givenNothingRegistered();
        when(userRepository.existsByDocumentoIdentidad("1020304050")).thenReturn(true);

        User user = validUser();
        assertThrows(DocumentAlreadyRegisteredException.class, () -> userUseCase.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRejectUnknownRole() {
        when(roleRepository.existsById(99)).thenReturn(false);

        User user = validUser().toBuilder().idRol(99).build();
        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(user));

        assertTrue(ex.getErrors().contains(UserUseCase.ROL_INEXISTENTE));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReportAllMissingRequiredFields() {
        User empty = User.builder().nombre(" ").build();

        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(empty));

        assertEquals(6, ex.getErrors().size());
        assertTrue(ex.getErrors().contains(UserUseCase.NOMBRE_REQUERIDO));
        assertTrue(ex.getErrors().contains(UserUseCase.APELLIDO_REQUERIDO));
        assertTrue(ex.getErrors().contains(UserUseCase.EMAIL_REQUERIDO));
        assertTrue(ex.getErrors().contains(UserUseCase.DOCUMENTO_REQUERIDO));
        assertTrue(ex.getErrors().contains(UserUseCase.ROL_REQUERIDO));
        assertTrue(ex.getErrors().contains(UserUseCase.SALARIO_REQUERIDO));
        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ana", "ana@", "ana@mail", "@mail.com", "ana mail@mail.com"})
    void shouldRejectInvalidEmailFormat(String email) {
        User user = validUser().toBuilder().email(email).build();

        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(user));

        assertTrue(ex.getErrors().contains(UserUseCase.EMAIL_INVALIDO));
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
    @ValueSource(strings = {"0", "15000000", "2500000.500"})
    void shouldAcceptValidSalaries(String salary) {
        givenNothingRegistered();
        User user = validUser().toBuilder().salarioBase(new BigDecimal(salary)).build();

        assertEquals(new BigDecimal(salary), userUseCase.registerUser(user).getSalarioBase());
    }

    @Test
    void shouldRejectSalaryWithMoreThanTwoDecimals() {
        User user = validUser().toBuilder().salarioBase(new BigDecimal("2500000.123")).build();

        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(user));

        assertTrue(ex.getErrors().contains(UserUseCase.SALARIO_DECIMALES));
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

        InvalidUserDataException ex =
                assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(user));

        assertTrue(ex.getErrors().contains("El campo nombre no puede tener más de 100 caracteres"));
        assertTrue(ex.getErrors().contains("El campo apellido no puede tener más de 100 caracteres"));
        assertTrue(ex.getErrors().contains("El campo documento_identidad no puede tener más de 20 caracteres"));
        assertTrue(ex.getErrors().contains("El campo telefono no puede tener más de 20 caracteres"));
        assertTrue(ex.getErrors().contains("El campo email no puede tener más de 150 caracteres"));
    }

    @Test
    void shouldAcceptMissingOptionalPhone() {
        givenNothingRegistered();
        User user = validUser().toBuilder().telefono(null).build();

        assertNull(userUseCase.registerUser(user).getTelefono());
    }

    @Test
    void shouldRejectNullUser() {
        assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(null));
    }
}
