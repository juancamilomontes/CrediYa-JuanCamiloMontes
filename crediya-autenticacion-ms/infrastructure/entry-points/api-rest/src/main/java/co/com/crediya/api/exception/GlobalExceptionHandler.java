package co.com.crediya.api.exception;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.DocumentAlreadyRegisteredException;
import co.com.crediya.model.exceptions.EmailAlreadyRegisteredException;
import co.com.crediya.model.exceptions.InvalidCredentialsException;
import co.com.crediya.model.exceptions.InvalidUserDataException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Atrapa TODAS las excepciones y las convierte en un ErrorResponse controlado,
 * para que el consumidor de la API nunca reciba mensajes inesperados.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    static final String MENSAJE_JSON_INVALIDO =
            "El cuerpo de la petición no es válido. Revisa el formato JSON y los tipos de datos "
                    + "(salario_base numérico, id_rol entero)";
    static final String MENSAJE_ERROR_INTERNO = "Ocurrió un error inesperado. Intenta más tarde";

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserData(InvalidUserDataException ex, HttpServletRequest request) {
        log.warn("Validación fallida: {}", ex.getErrors());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrors(), request);
    }

    @ExceptionHandler({EmailAlreadyRegisteredException.class, DocumentAlreadyRegisteredException.class})
    public ResponseEntity<ErrorResponse> handleAlreadyRegistered(BusinessException ex, HttpServletRequest request) {
        log.warn("Conflicto: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), List.of(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex,
                                                                  HttpServletRequest request) {
        log.warn("Login fallido: credenciales inválidas");
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), List.of(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        log.warn("JSON inválido: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.BAD_REQUEST, MENSAJE_JSON_INVALIDO, List.of(), request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex,
                                                         HttpServletRequest request) {
        log.warn("Content-Type no soportado: {}", ex.getContentType());
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "El Content-Type debe ser application/json", List.of(), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethod(HttpRequestMethodNotSupportedException ex,
                                                      HttpServletRequest request) {
        log.warn("Método no soportado: {}", ex.getMethod());
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no permitido para este recurso", List.of(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado: {}", request.getRequestURI());
        return build(HttpStatus.NOT_FOUND, "El recurso solicitado no existe", List.of(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado procesando {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, MENSAJE_ERROR_INTERNO, List.of(), request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, List<String> errors,
                                                HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                errors,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
