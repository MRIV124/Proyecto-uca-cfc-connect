package sv.edu.udb.cfcconnect.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /** Nombre del esquema de seguridad registrado en el documento OpenAPI. */
    public static final String ESQUEMA_BEARER = "bearerAuth";

    @Bean
    public OpenAPI cfcConnectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UCA-CFC Connect API")
                        .description("Sistema web empresarial para el Centro de Formacion Continua (CFC) de la UCA. "
                                + "Modulos: seguridad, clientes, cursos, diplomados, inscripciones, cotizaciones, "
                                + "alquiler de espacios, catering y pagos.")
                        .version("v0.3 (Fase 3)")
                        .contact(new Contact().name("Equipo de proyecto").email("yesenia.escobar@udb.edu.sv")))
                // Permite autorizar las peticiones desde Swagger UI con el token JWT.
                .components(new Components().addSecuritySchemes(ESQUEMA_BEARER,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido en POST /auth/login")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BEARER));
    }
}
