package co.com.crediya.model.exceptions;

import java.util.List;

/**
 * Se lanza cuando los datos de la solicitud no cumplen las validaciones de la HU2.
 * Lleva la lista completa de errores para devolverlos todos de una vez.
 */
public class InvalidSolicitudDataException extends BusinessException {

    private final transient List<String> errors;

    public InvalidSolicitudDataException(List<String> errors) {
        super("Los datos de la solicitud no son válidos");
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
