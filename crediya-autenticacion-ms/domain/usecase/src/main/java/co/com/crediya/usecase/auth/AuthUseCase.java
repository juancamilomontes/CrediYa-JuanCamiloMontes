package co.com.crediya.usecase.auth;

import co.com.crediya.model.auth.Session;
import co.com.crediya.model.auth.gateways.PasswordHasher;
import co.com.crediya.model.auth.gateways.TokenProvider;
import co.com.crediya.model.common.TextNormalizer;
import co.com.crediya.model.exceptions.InvalidCredentialsException;
import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/**
 * HU3 - Inicio de sesión. Valida credenciales y entrega un token de sesión (JWT).
 * No depende de Spring, HTTP ni JWT: solo del negocio.
 */
@Log
@RequiredArgsConstructor
public class AuthUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;

    public Session login(String email, String rawPassword) {
        log.info("Intento de inicio de sesión");
        String normalizedEmail = TextNormalizer.normalizeEmail(email);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    log.warning("Login fallido: email no registrado");
                    return new InvalidCredentialsException();
                });

        if (rawPassword == null || !passwordHasher.matches(rawPassword, user.getContrasena())) {
            log.warning("Login fallido: contraseña incorrecta");
            throw new InvalidCredentialsException();
        }

        String rolNombre = roleRepository.findById(user.getIdRol())
                .map(Role::getNombre)
                .orElse(null);

        String token = tokenProvider.generateToken(user, rolNombre);
        log.info(() -> "Inicio de sesión exitoso para el usuario " + user.getIdUsuario());
        return Session.builder()
                .token(token)
                .idUsuario(user.getIdUsuario())
                .email(user.getEmail())
                .idRol(user.getIdRol())
                .build();
    }
}
