package co.com.crediya.model.exceptions;

import java.util.List;

/**
 * Se lanza cuando los datos del usuario no cumplen las validaciones de la HU1.
 * Lleva la lista completa de errores para devolverlos todos de una vez.
 */
public class InvalidUserDataException extends BusinessException {

    private final List<String> errors;

    public InvalidUserDataException(List<String> errors) {
        super("Los datos del usuario no son válidos");
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
