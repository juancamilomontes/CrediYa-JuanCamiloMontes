package co.com.crediya.api;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.api.dto.RegisterSolicitudRequest;
import co.com.crediya.api.dto.SolicitudResponse;
import co.com.crediya.api.mapper.SolicitudDtoMapper;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
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
 * Entry point HTTP de la HU2: POST /api/v1/solicitud.
 * Solo traduce HTTP <-> dominio; la lógica está en SolicitudUseCase.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/solicitud", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "Registro de solicitudes de préstamo (HU2)")
public class SolicitudController {

    private final SolicitudUseCase solicitudUseCase;

    @Operation(summary = "Registrar una solicitud de préstamo",
            description = "Registra una solicitud en la tabla solicitud con estado inicial "
                    + "'Pendiente de revisión'. Valida email, monto (>0 y dentro del rango del tipo), "
                    + "plazo y que el tipo de préstamo exista.")
    @ApiResponse(responseCode = "201", description = "Solicitud registrada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitudResponse> registrarSolicitud(@RequestBody RegisterSolicitudRequest request) {
        log.info("POST /api/v1/solicitud - solicitud de registro recibida");
        Solicitud registrada = solicitudUseCase.registrarSolicitud(SolicitudDtoMapper.toDomain(request));
        log.info("POST /api/v1/solicitud - solicitud registrada con id {}", registrada.getIdSolicitud());
        return ResponseEntity.status(HttpStatus.CREATED).body(SolicitudDtoMapper.toResponse(registrada));
    }
}
