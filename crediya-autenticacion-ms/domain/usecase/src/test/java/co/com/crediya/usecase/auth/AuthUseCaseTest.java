package co.com.crediya.usecase.auth;

import co.com.crediya.model.auth.Session;
import co.com.crediya.model.auth.gateways.PasswordHasher;
import co.com.crediya.model.auth.gateways.TokenProvider;
import co.com.crediya.model.exceptions.InvalidCredentialsException;
import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenProvider tokenProvider;

    private AuthUseCase authUseCase;

    @BeforeEach
    void setUp() {
        authUseCase = new AuthUseCase(userRepository, roleRepository, passwordHasher, tokenProvider);
    }

    private User storedUser() {
        return User.builder()
                .idUsuario(7)
                .email("cliente@mail.com")
                .idRol(3)
                .contrasena("HASHED")
                .build();
    }

    @Test
    void shouldLoginWithValidCredentials() {
        when(userRepository.findByEmail("cliente@mail.com")).thenReturn(Optional.of(storedUser()));
        when(passwordHasher.matches("Secreta123", "HASHED")).thenReturn(true);
        when(roleRepository.findById(3)).thenReturn(Optional.of(Role.builder().idRol(3).nombre("CLIENTE").build()));
        when(tokenProvider.generateToken(any(User.class), eq("CLIENTE"))).thenReturn("jwt-token");

        Session session = authUseCase.login("cliente@mail.com", "Secreta123");

        assertEquals("jwt-token", session.getToken());
        assertEquals("cliente@mail.com", session.getEmail());
        assertEquals(3, session.getIdRol());
    }

    @Test
    void shouldNormalizeEmailBeforeLookup() {
        when(userRepository.findByEmail("cliente@mail.com")).thenReturn(Optional.of(storedUser()));
        when(passwordHasher.matches(any(), any())).thenReturn(true);
        lenient().when(roleRepository.findById(any()))
                .thenReturn(Optional.of(Role.builder().idRol(3).nombre("CLIENTE").build()));
        when(tokenProvider.generateToken(any(), any())).thenReturn("jwt-token");

        authUseCase.login("  CLIENTE@Mail.com ", "Secreta123");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByEmail(captor.capture());
        assertEquals("cliente@mail.com", captor.getValue());
    }

    @Test
    void shouldRejectUnknownEmail() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authUseCase.login("nadie@mail.com", "x"));
        verify(tokenProvider, never()).generateToken(any(), any());
    }

    @Test
    void shouldRejectWrongPassword() {
        when(userRepository.findByEmail("cliente@mail.com")).thenReturn(Optional.of(storedUser()));
        when(passwordHasher.matches("mala", "HASHED")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authUseCase.login("cliente@mail.com", "mala"));
        verify(tokenProvider, never()).generateToken(any(), any());
    }
}
