package co.com.crediya.api;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.api.dto.RegisterUserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.api.mapper.UserDtoMapper;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entry point HTTP de la HU1: POST /api/v1/usuarios.
 * Solo traduce HTTP <-> dominio; la lógica está en UserUseCase.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro de usuarios (HU1)")
public class UserController {

    private final UserUseCase userUseCase;

    @Operation(summary = "Registrar un usuario",
            description = "Registra un solicitante. Valida obligatorios, formato de correo, "
                    + "salario entre 0 y 15.000.000 y que el correo no esté registrado.")
    @ApiResponse(responseCode = "201", description = "Usuario registrado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "El correo ya está registrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterUserRequest request) {
        log.info("POST /api/v1/usuarios - solicitud de registro recibida");
        User registered = userUseCase.registerUser(UserDtoMapper.toDomain(request));
        log.info("POST /api/v1/usuarios - usuario registrado con id {}", registered.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDtoMapper.toResponse(registered));
    }
}
