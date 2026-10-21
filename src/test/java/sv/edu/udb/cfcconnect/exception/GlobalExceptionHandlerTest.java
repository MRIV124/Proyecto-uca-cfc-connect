package sv.edu.udb.cfcconnect.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.cfcconnect.seguridad.dto.AuthRequest;
import sv.edu.udb.cfcconnect.seguridad.service.AuthService;

/**
 * Pruebas del manejo centralizado de excepciones.
 *
 * <p>Verifican que las excepciones estandar de Spring MVC se traducen a su
 * codigo HTTP correcto (405, 400, 415) y no al 500 generico que produce el
 * manejador de ultimo recurso.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Value("${app.security.admin.email}")
    private String adminEmail;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Test
    void debeResponder405SiElMetodoNoEstaPermitido() throws Exception {
        // GET /auth/login no existe: el login solo admite POST.
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "POST"))
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"));
    }

    @Test
    void debeResponder400SiElJsonEsInvalido() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{esto-no-es-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void debeResponder400SiFaltanCamposObligatorios() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@cfc.edu.sv\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void debeResponder400SiElIdNoEsNumerico() throws Exception {
        // El endpoint es privado: sin token responderia 401 antes de llegar
        // al controlador, asi que se envia un token valido.
        String token = authService.login(new AuthRequest(adminEmail, adminPassword)).getToken();

        mockMvc.perform(get("/clientes/abc").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void debeResponder415SiElContentTypeNoEsJson() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("admin@cfc.edu.sv"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415));
    }
}
