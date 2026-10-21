package sv.edu.udb.cfcconnect.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Manejo centralizado de excepciones para toda la API REST del proyecto.
 * Requisito tecnico obligatorio de la Fase 2 (Manejo de errores):
 * uso de @ControllerAdvice + @ExceptionHandler en lugar de try/catch
 * repetido en cada controlador.
 *
 * Cada tipo de excepcion se traduce a un codigo HTTP y a un cuerpo JSON
 * consistente (ApiError), para que el frontend siempre sepa que esperar.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // 404 - el recurso solicitado (Cliente, Curso, Espacio, etc.) no existe.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 409 - la operacion viola una regla de negocio (cupo agotado, espacio ocupado,
    // DUI/NIT duplicado, etc.). Se agrupan porque comparten el mismo tratamiento HTTP.
    @ExceptionHandler({CupoAgotadoException.class, EspacioOcupadoException.class, ReglaNegocioException.class})
    public ResponseEntity<ApiError> handleBusinessRule(RuntimeException ex, HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 400 - argumentos invalidos que no pasaron por Bean Validation (ej. reglas
    // cruzadas como "fecha fin antes que fecha inicio").
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.badRequest().body(error);
    }

    // 400 - fallo de validacion de un @RequestBody anotado con @Valid
    // (@NotBlank, @Email, @Size, @Positive, etc. definidos en los DTO *Request).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

        ApiError error = ApiError.of(HttpStatus.BAD_REQUEST.value(), "Validation Failed",
                "Uno o mas campos no son validos", request.getRequestURI());
        error.setValidationErrors(fieldErrors);
        return ResponseEntity.badRequest().body(error);
    }

    // 401 - las credenciales del login no son validas (correo o contrasena).
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(CredencialesInvalidasException ex, HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // 403 - el usuario esta inactivo (estado = false) e intenta iniciar sesion.
    @ExceptionHandler(UsuarioInactivoException.class)
    public ResponseEntity<ApiError> handleInactiveUser(UsuarioInactivoException ex, HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.FORBIDDEN.value(), "Forbidden", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // 405 - el endpoint existe pero no admite el metodo HTTP utilizado
    // (por ejemplo, GET /auth/login, cuando el login solo admite POST).
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                              HttpServletRequest request) {
        StringBuilder mensaje = new StringBuilder("El endpoint ")
                .append(request.getRequestURI())
                .append(" no admite el metodo ")
                .append(ex.getMethod());

        ResponseEntity.BodyBuilder respuesta = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED);

        if (ex.getSupportedHttpMethods() != null && !ex.getSupportedHttpMethods().isEmpty()) {
            String permitidos = ex.getSupportedHttpMethods().stream()
                    .map(HttpMethod::name)
                    .collect(Collectors.joining(", "));
            mensaje.append(". Metodos permitidos: ").append(permitidos);
            respuesta = respuesta.header(HttpHeaders.ALLOW, permitidos);
        }

        ApiError error = ApiError.of(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method Not Allowed",
                mensaje.toString(), request.getRequestURI());
        return respuesta.body(error);
    }

    // 400 - el cuerpo JSON enviado no se pudo leer (mal formado, tipo
    // incorrecto, etc.). Sin este handler caeria en el 500 generico.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "El cuerpo de la peticion no es valido: se espera un JSON con el formato correcto",
                request.getRequestURI());
        return ResponseEntity.badRequest().body(error);
    }

    // 400 - falta un parametro obligatorio en la URL o en la query string.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException ex,
                                                           HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Falta el parametro obligatorio '" + ex.getParameterName() + "'",
                request.getRequestURI());
        return ResponseEntity.badRequest().body(error);
    }

    // 400 - el tipo de un parametro no coincide con el esperado
    // (por ejemplo, un id que no es numerico).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                        HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "El valor '" + ex.getValue() + "' no es valido para el parametro '" + ex.getName() + "'",
                request.getRequestURI());
        return ResponseEntity.badRequest().body(error);
    }

    // 415 - el Content-Type de la peticion no es compatible con el endpoint
    // (por ejemplo, enviar texto plano a un endpoint que espera JSON).
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex,
                                                                 HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Unsupported Media Type",
                "El tipo de contenido '" + ex.getContentType() + "' no es soportado por este endpoint",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    // 500 - cualquier otra excepcion no controlada explicitamente.
    // Siempre debe ir de ultimo: es la red de seguridad final.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
