package co.com.crediya.model.exceptions;

/**
 * Se lanza cuando el documento de identidad ya pertenece a otro usuario.
 */
public class DocumentAlreadyRegisteredException extends BusinessException {

    public DocumentAlreadyRegisteredException(String documentoIdentidad) {
        super("El documento de identidad " + documentoIdentidad + " ya está registrado");
    }
}
