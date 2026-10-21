package sv.edu.udb.cfcconnect.seguridad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import sv.edu.udb.cfcconnect.exception.CredencialesInvalidasException;
import sv.edu.udb.cfcconnect.seguridad.dto.AuthRequest;
import sv.edu.udb.cfcconnect.seguridad.dto.AuthResponse;
import sv.edu.udb.cfcconnect.seguridad.service.AuthService;

/**
 * Pruebas del servicio de autenticacion (Fase 3 - Seguridad): login correcto,
 * login con credenciales invalidas y emision del token.
 */
@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Value("${app.security.admin.email}")
    private String adminEmail;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Test
    void shouldEmitirTokenConCredencialesValidas() {
        AuthResponse response = authService.login(new AuthRequest(adminEmail, adminPassword));

        assertNotNull(response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("ADMIN", response.getRol());
        assertEquals(adminEmail, response.getEmail());
        assertTrue(response.getExpiresIn() > 0, "El token debe tener una expiracion configurada");
        assertNotNull(response.getExpiracion());
    }

    @Test
    void shouldRechazarContrasenaIncorrecta() {
        CredencialesInvalidasException error = assertThrows(CredencialesInvalidasException.class,
                () -> authService.login(new AuthRequest(adminEmail, "clave-incorrecta")));

        assertTrue(error.getMessage().contains("incorrectos"));
    }

    @Test
    void shouldRechazarCorreoInexistente() {
        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login(new AuthRequest("no-existe@cfc.edu.sv", "Admin123*")));
    }
}
