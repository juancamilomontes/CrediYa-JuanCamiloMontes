package co.com.crediya.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Valida los tokens JWT emitidos por el microservicio AUTENTICACION usando el secreto compartido.
 * SOLICITUDES actúa como "resource server": no genera tokens, solo los verifica.
 */
@Component
public class JwtService {

    public static final String CLAIM_ROL = "rol";

    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Valida firma y expiración y devuelve los claims. Lanza JwtException si es inválido. */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
