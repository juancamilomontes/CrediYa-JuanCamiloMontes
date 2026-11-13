package co.com.crediya.model.auth.gateways;

/**
 * Puerto de salida: hashing y verificación de contraseñas.
 * La implementación (BCrypt) vive en infrastructure; el dominio no conoce la librería.
 */
public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
