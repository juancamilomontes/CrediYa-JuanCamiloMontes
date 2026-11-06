package co.com.crediya.model.exceptions;

/**
 * Se lanza cuando el correo electrónico ya pertenece a otro solicitante.
 */
public class EmailAlreadyRegisteredException extends BusinessException {

    public EmailAlreadyRegisteredException(String correoElectronico) {
        super("El correo electrónico " + correoElectronico + " ya está registrado");
    }
}
