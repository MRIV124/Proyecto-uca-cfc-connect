package sv.edu.udb.cfcconnect.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    // 500 - cualquier otra excepcion no controlada explicitamente.
    // Siempre debe ir de ultimo: es la red de seguridad final.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        ApiError error = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
