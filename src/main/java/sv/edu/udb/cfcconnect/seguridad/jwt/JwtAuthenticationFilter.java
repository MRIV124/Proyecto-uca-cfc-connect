package sv.edu.udb.cfcconnect.seguridad.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro que intercepta cada peticion para leer el token JWT de la cabecera
 * {@code Authorization: Bearer <token>}, validarlo y establecer la
 * autenticacion en el contexto de seguridad de Spring.
 *
 * <p>El filtro es "stateless": no crea ni mantiene sesiones HTTP. Si el token
 * falta, es invalido o esta expirado, la peticion sigue su curso sin
 * autenticacion y el {@code AuthenticationEntryPoint} respondiera con un
 * {@code 401 Unauthorized} cuando el endpoint lo requiera.</p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String PREFIX_BEARER = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String username = jwtService.extractUsername(token);

                if (username != null && !username.isBlank()) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (JwtException | AuthenticationException | IllegalArgumentException ex) {
                // Token invalido, malformado o expirado (o usuario eliminado):
                // se ignora y la peticion queda sin autenticar (401 en el entry point).
                log.debug("Token JWT rechazado: {}", ex.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /** Extrae el token de la cabecera Authorization, si viene en formato Bearer. */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        if (header != null && header.startsWith(PREFIX_BEARER)) {
            String token = header.substring(PREFIX_BEARER.length()).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }
}
