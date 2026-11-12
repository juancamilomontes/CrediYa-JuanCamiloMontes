package co.com.crediya.api.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato único de error que devuelve la API: el cliente nunca recibe un stack trace.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> errors,
        String path
) {
}
