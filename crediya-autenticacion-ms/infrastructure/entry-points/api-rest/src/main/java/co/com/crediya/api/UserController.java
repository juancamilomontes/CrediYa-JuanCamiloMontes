package co.com.crediya.api;

import co.com.crediya.api.dto.RegisterUserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.api.mapper.UserDtoMapper;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
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
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterUserRequest request) {
        log.info("POST /api/v1/usuarios - solicitud de registro recibida");
        User registered = userUseCase.registerUser(UserDtoMapper.toDomain(request));
        log.info("POST /api/v1/usuarios - usuario registrado con id {}", registered.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDtoMapper.toResponse(registered));
    }
}
