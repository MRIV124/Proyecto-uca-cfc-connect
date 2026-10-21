package sv.edu.udb.cfcconnect.seguridad.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.edu.udb.cfcconnect.seguridad.dto.AuthRequest;
import sv.edu.udb.cfcconnect.seguridad.dto.AuthResponse;
import sv.edu.udb.cfcconnect.seguridad.dto.SesionResponse;
import sv.edu.udb.cfcconnect.seguridad.service.AuthService;

/**
 * Endpoints de autenticacion (Fase 3).
 *
 * <p>{@code POST /auth/login} es el unico endpoint publico de la API: recibe
 * las credenciales y devuelve un token JWT. Ese token debe enviarse en la
 * cabecera {@code Authorization: Bearer <token>} para acceder al resto de los
 * endpoints.</p>
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticacion", description = "Login y emision de tokens JWT")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(summary = "Iniciar sesion y obtener un token JWT",
            description = "Valida las credenciales contra el hash BCrypt almacenado y devuelve un token "
                    + "firmado con la duracion configurada en 'app.jwt.expiration-seconds'.")
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @Operation(summary = "Consultar la sesion actual",
            description = "Devuelve los datos del usuario autenticado y los roles con los que puede acceder.")
    @GetMapping("/me")
    public SesionResponse me() {
        return service.sesionActual();
    }
}
