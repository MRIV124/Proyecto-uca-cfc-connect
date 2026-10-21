package sv.edu.udb.cfcconnect.seguridad.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de informacion de la API.
 *
 * <p>La raiz ({@code /}) la sirve Spring Boot directamente desde
 * {@code src/main/resources/static/index.html} (el panel web), por eso no se
 * mapea aqui: un {@code @GetMapping("/")} tiene prioridad sobre el archivo
 * estatico y lo ocultaria.</p>
 */
@RestController
@Tag(name = "Informacion", description = "Informacion general de la API")
public class InfoController {

    @Operation(summary = "Informacion de la API",
            description = "Devuelve las URLs principales y las credenciales de prueba de los usuarios sembrados.")
    @SecurityRequirements
    @GetMapping("/api/info")
    public Map<String, Object> informacion() {
        return Map.of(
                "nombre", "UCA-CFC Connect API",
                "version", "v0.3 (Fase 3 - Seguridad)",
                "panelWeb", "/",
                "documentacion", "/swagger-ui.html",
                "openapi", "/v3/api-docs",
                "login", "/auth/login",
                "consolaH2", "/h2-console",
                "nota", "Todos los endpoints requieren un token JWT, excepto /auth/login. "
                        + "En Swagger UI usa el boton Authorize con el token obtenido en /auth/login.");
    }
}
