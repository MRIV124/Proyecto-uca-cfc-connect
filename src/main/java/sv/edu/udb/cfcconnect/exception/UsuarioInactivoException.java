package sv.edu.udb.cfcconnect.exception;

/**
 * Se lanza cuando el usuario existe pero esta inactivo ({@code estado = false})
 * e intenta iniciar sesion. GlobalExceptionHandler la traduce a HTTP 403.
 */
public class UsuarioInactivoException extends RuntimeException {

    public UsuarioInactivoException(String message) {
        super(message);
    }
}
