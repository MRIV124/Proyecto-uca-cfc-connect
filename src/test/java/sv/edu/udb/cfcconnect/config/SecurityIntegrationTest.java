package sv.edu.udb.cfcconnect.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.cfcconnect.seguridad.dto.AuthRequest;
import sv.edu.udb.cfcconnect.seguridad.dto.AuthResponse;
import sv.edu.udb.cfcconnect.seguridad.service.AuthService;

/**
 * Pruebas de integracion de la proteccion de endpoints (Fase 3 - Seguridad).
 *
 * <p>Verifican que:</p>
 * <ul>
 *   <li>el login emite un token JWT valido;</li>
 *   <li>los endpoints protegidos responden 401 sin token;</li>
 *   <li>la administracion de usuarios exige el rol ADMIN (403 con otro rol);</li>
 *   <li>un token valido permite el acceso.</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.security.admin.email}")
    private String adminEmail;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Value("${app.security.recepcionista.email}")
    private String recepcionistaEmail;

    @Value("${app.security.recepcionista.password}")
    private String recepcionistaPassword;

    private String tokenDe(String email, String password) {
        AuthResponse response = authService.login(new AuthRequest(email, password));
        return response.getToken();
    }

    @Test
    void shouldPermitirElLoginSinToken() throws Exception {
        String cuerpo = objectMapper.writeValueAsString(new AuthRequest(adminEmail, adminPassword));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    void shouldRechazarLoginConCredencialesInvalidas() throws Exception {
        String cuerpo = objectMapper.writeValueAsString(new AuthRequest(adminEmail, "mala-clave"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void shouldExigirTokenEnEndpointsProtegidos() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldPermitirAccesoConTokenDeAdmin() throws Exception {
        String token = tokenDe(adminEmail, adminPassword);

        mockMvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenegarAdministracionDeUsuariosSinRolAdmin() throws Exception {
        String token = tokenDe(recepcionistaEmail, recepcionistaPassword);

        mockMvc.perform(get("/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void shouldPermitirAccesoAModulosConTokenValido() throws Exception {
        String token = tokenDe(recepcionistaEmail, recepcionistaPassword);

        mockMvc.perform(get("/clientes").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRechazarTokenInvalido() throws Exception {
        mockMvc.perform(get("/clientes").header("Authorization", "Bearer token.invalido.firma"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldPermitirAccesoALaDocumentacion() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void debeServirElPanelWebSinToken() throws Exception {
        // El panel (static/index.html) es publico: el login se hace desde el
        // propio formulario, no con el token.
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/app.js"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/app.css"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPermitirInformacionDeLaApi() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("/auth/login"));
    }
}
