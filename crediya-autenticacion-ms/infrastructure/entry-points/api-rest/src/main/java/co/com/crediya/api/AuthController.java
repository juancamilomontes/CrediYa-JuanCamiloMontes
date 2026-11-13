package co.com.crediya.api;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.LoginResponse;
import co.com.crediya.model.auth.Session;
import co.com.crediya.usecase.auth.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entry point HTTP de la HU3: POST /api/v1/login.
 * Traduce HTTP <-> dominio; la lógica está en AuthUseCase.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/login", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Inicio de sesión y emisión de token (HU3)")
public class AuthController {

    private static final String TIPO_TOKEN = "Bearer";

    private final AuthUseCase authUseCase;

    @Operation(summary = "Iniciar sesión",
            description = "Valida correo y clave y devuelve un token JWT con el rol del usuario.")
    @ApiResponse(responseCode = "200", description = "Login exitoso")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("POST /api/v1/login - intento de inicio de sesión");
        Session session = authUseCase.login(request.correo(), request.clave());
        log.info("POST /api/v1/login - sesión emitida para el usuario {}", session.getIdUsuario());
        return ResponseEntity.ok(new LoginResponse(
                session.getToken(), TIPO_TOKEN, session.getIdUsuario(), session.getEmail(), session.getIdRol()));
    }
}
