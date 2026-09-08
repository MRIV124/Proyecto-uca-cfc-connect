package sv.edu.udb.cfcconnect.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cfcConnectOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("UCA-CFC Connect API")
                .description("Sistema web empresarial para el Centro de Formacion Continua (CFC) de la UCA. "
                        + "Modulos: seguridad, clientes, cursos, diplomados, inscripciones, cotizaciones, "
                        + "alquiler de espacios, catering y pagos.")
                .version("v0.2 (Fase 2)")
                .contact(new Contact().name("Equipo de proyecto").email("yesenia.escobar@udb.edu.sv")));
    }
}
