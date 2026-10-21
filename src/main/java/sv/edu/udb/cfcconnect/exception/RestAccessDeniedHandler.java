package sv.edu.udb.cfcconnect.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Manejador de acceso denegado de Spring Security.
 *
 * <p>Se invoca cuando el usuario esta autenticado pero no tiene el rol
 * necesario para el endpoint solicitado (por ejemplo, un usuario con rol
 * CLIENTE que intenta consumir {@code /usuarios}). Devuelve un
 * {@code 403 Forbidden} en formato {@link ApiError}.</p>
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ApiError error = ApiError.of(HttpStatus.FORBIDDEN.value(), "Forbidden",
                "No tiene permisos para acceder a este recurso.",
                request.getRequestURI());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), error);
    }
}
