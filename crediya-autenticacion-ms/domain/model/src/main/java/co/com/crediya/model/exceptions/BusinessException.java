package co.com.crediya.model.exceptions;

/**
 * Excepción base para errores de reglas de negocio.
 * El entry point la traduce a una respuesta HTTP controlada.
 */
public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }
}
