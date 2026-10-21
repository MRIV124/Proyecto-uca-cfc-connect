package sv.edu.udb.cfcconnect.exception;

/**
 * Se lanza cuando las credenciales enviadas en el login no son validas
 * (correo inexistente o contrasena incorrecta).
 *
 * <p>GlobalExceptionHandler la traduce a HTTP 401 (Unauthorized). El mensaje es
 * generico a proposito, para no revelar si el correo existe o no.</p>
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
