package co.com.crediya.api.security;

import co.com.crediya.model.auth.gateways.TokenProvider;
import co.com.crediya.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Driven adapter de seguridad: genera y valida tokens JWT (HS256).
 * El secreto se comparte con el microservicio SOLICITUDES para que pueda validar el token.
 */
@Component
public class JwtTokenProvider implements TokenProvider {

    public static final String CLAIM_ROL = "rol";
    public static final String CLAIM_ID_USUARIO = "idUsuario";

    private final SecretKey key;
    private final long expirationMillis;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration-millis:3600000}") long expirationMillis) {
        this.key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    @Override
    public String generateToken(User user, String rolNombre) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim(CLAIM_ID_USUARIO, user.getIdUsuario())
                .claim(CLAIM_ROL, rolNombre)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key)
                .compact();
    }

    /** Valida la firma y expiración y devuelve los claims. Lanza JwtException si es inválido. */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
