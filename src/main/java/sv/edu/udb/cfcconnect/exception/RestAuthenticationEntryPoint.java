package sv.edu.udb.cfcconnect.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Punto de entrada de autenticacion de Spring Security.
 *
 * <p>Se invoca cuando una peticion llega a un endpoint protegido sin un token
 * JWT valido (o sin token). En lugar de la pagina HTML por defecto de Spring,
 * devuelve el mismo formato {@link ApiError} que usa el resto de la API, de
 * modo que el frontend siempre recibe JSON consistente.</p>
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ApiError error = ApiError.of(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
                "Se requiere un token JWT valido. Inicie sesion en POST /auth/login y envie el token "
                        + "en la cabecera 'Authorization: Bearer <token>'.",
                request.getRequestURI());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), error);
    }
}
