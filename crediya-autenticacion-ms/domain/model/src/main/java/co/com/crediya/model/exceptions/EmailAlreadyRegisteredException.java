package co.com.crediya.model.exceptions;

/**
 * Se lanza cuando el email ya pertenece a otro usuario.
 */
public class EmailAlreadyRegisteredException extends BusinessException {

    public EmailAlreadyRegisteredException(String email) {
        super("El email " + email + " ya está registrado");
    }
}
