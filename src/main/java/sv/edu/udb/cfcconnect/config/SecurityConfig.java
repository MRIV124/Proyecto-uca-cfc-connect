package sv.edu.udb.cfcconnect.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sv.edu.udb.cfcconnect.exception.RestAccessDeniedHandler;
import sv.edu.udb.cfcconnect.exception.RestAuthenticationEntryPoint;
import sv.edu.udb.cfcconnect.seguridad.jwt.JwtAuthenticationFilter;

/**
 * Configuracion de Spring Security (Fase 3).
 *
 * <p>Aspectos principales:</p>
 * <ul>
 *   <li><b>BCrypt</b>: el {@link PasswordEncoder} oficial de Spring
 *       ({@link BCryptPasswordEncoder}) cifra las contrasenas antes de
 *       guardarlas y las compara en el momento del login.</li>
 *   <li><b>Stateless</b>: la aplicacion no usa sesiones HTTP; cada peticion
 *       se autentica con el token JWT de la cabecera {@code Authorization}.</li>
 *   <li><b>Sin CSRF</b>: al no haber sesiones ni formularios con cookies, el
 *       token JWT es el mecanismo de proteccion.</li>
 *   <li><b>Control de acceso por rol</b>: la administracion de usuarios y de
 *       roles ({@code /usuarios/**} y {@code /roles/**}) exige el rol ADMIN;
 *       el resto de los endpoints exige un usuario autenticado.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** Rutas publicas: login, documentacion y consola H2. */
    private static final String[] RUTAS_PUBLICAS = {
            "/",
            "/index.html",
            "/app.js",
            "/app.css",
            "/favicon.ico",
            "/api/info",
            "/auth/login",
            "/error",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/h2-console/**"
    };

    /**
     * Cifrado de contrasenas con BCrypt. El mismo bean se usa para cifrar al
     * guardar y para comparar al autenticar.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticacion que valida las credenciales contra la base de
     * datos, comparando la contrasena en texto plano con el hash BCrypt
     * almacenado.
     */
    private DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Gestor de autenticacion usado por el servicio de login. Se construye de
     * forma explicita (y no como bean de tipo {@code AuthenticationProvider})
     * para que Spring Security use siempre nuestro cifrado BCrypt y no genere
     * avisos sobre configuracion duplicada de usuarios.
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        return new ProviderManager(authenticationProvider(userDetailsService, passwordEncoder));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   RestAuthenticationEntryPoint authenticationEntryPoint,
                                                   RestAccessDeniedHandler accessDeniedHandler,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // La consola H2 usa frames; se permite el mismo origen.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(RUTAS_PUBLICAS).permitAll()
                        // Administracion de usuarios y de roles: solo ADMIN.
                        .requestMatchers("/usuarios/**", "/roles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuracion CORS para que el frontend pueda consumir la API desde otro
     * origen enviando la cabecera {@code Authorization}.
     *
     * <p>Los origenes permitidos se configuran en
     * {@code app.cors.allowed-origins} (separados por comas). En desarrollo el
     * valor por defecto es {@code *}.</p>
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:*}") List<String> origenesPermitidos) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(origenesPermitidos);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
