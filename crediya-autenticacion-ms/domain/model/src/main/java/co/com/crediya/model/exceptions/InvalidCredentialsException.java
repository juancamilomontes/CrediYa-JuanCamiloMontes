package co.com.crediya.model.exceptions;

/**
 * Se lanza cuando el correo o la contraseña del login no son válidos.
 * No se distingue cuál de los dos falló, para no dar pistas a un atacante.
 */
public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super("Correo o contraseña incorrectos");
    }
}
