package co.com.crediya.usecase.user;

import co.com.crediya.model.auth.gateways.PasswordHasher;
import co.com.crediya.model.exceptions.DocumentAlreadyRegisteredException;
import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.exceptions.InvalidUserDataException;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Prueba el FLUJO del registro. Las reglas de formato se prueban en UserValidatorTest.
 */
@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    private static final Integer ROL_CLIENTE = 3;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository, roleRepository, passwordHasher);
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
                .contrasena("Secreta123")
                .build();
    }

    private void givenNothingRegistered() {
        lenient().when(roleRepository.existsById(any())).thenReturn(true);
        lenient().when(userRepository.existsByEmail(any())).thenReturn(false);
        lenient().when(userRepository.existsByDocumentoIdentidad(any())).thenReturn(false);
        lenient().when(passwordHasher.hash(any())).thenAnswer(inv -> "HASHED:" + inv.getArgument(0));
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
    void shouldHashPasswordBeforeSaving() {
        givenNothingRegistered();

        userUseCase.registerUser(validUser());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("HASHED:Secreta123", captor.getValue().getContrasena());
    }

    @Test
    void shouldNormalizeFieldsAndIgnoreClientIdBeforeSaving() {
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
    void shouldNotTouchRepositoriesWhenFormatIsInvalid() {
        User invalid = validUser().toBuilder().email("ana@").build();

        assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(invalid));
        verifyNoInteractions(roleRepository, userRepository, passwordHasher);
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
    void shouldRejectNullUser() {
        assertThrows(InvalidUserDataException.class, () -> userUseCase.registerUser(null));
    }
}
