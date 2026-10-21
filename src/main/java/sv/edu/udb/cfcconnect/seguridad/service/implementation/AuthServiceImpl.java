package sv.edu.udb.cfcconnect.seguridad.service.implementation;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sv.edu.udb.cfcconnect.exception.CredencialesInvalidasException;
import sv.edu.udb.cfcconnect.exception.UsuarioInactivoException;
import sv.edu.udb.cfcconnect.seguridad.domain.UsuarioPrincipal;
import sv.edu.udb.cfcconnect.seguridad.dto.AuthRequest;
import sv.edu.udb.cfcconnect.seguridad.dto.AuthResponse;
import sv.edu.udb.cfcconnect.seguridad.dto.SesionResponse;
import sv.edu.udb.cfcconnect.seguridad.jwt.JwtService;
import sv.edu.udb.cfcconnect.seguridad.service.AuthService;

/**
 * Implementacion del login basado en JWT (Fase 3).
 *
 * <p>El flujo es el siguiente:</p>
 * <ol>
 *   <li>Las credenciales se validan contra la base de datos mediante el
 *       {@code AuthenticationManager} de Spring Security, que compara la
 *       contrasena en texto plano contra el hash BCrypt almacenado.</li>
 *   <li>Si las credenciales son correctas se emite un token JWT firmado que
 *       contiene el correo y el rol del usuario.</li>
 *   <li>El cliente envia ese token en la cabecera
 *       {@code Authorization: Bearer <token>} en las siguientes peticiones.</li>
 * </ol>
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (DisabledException ex) {
            throw new UsuarioInactivoException("El usuario esta inactivo. Contacte al administrador.");
        } catch (AuthenticationException ex) {
            // Mensaje generico: no revela si el correo existe o no.
            throw new CredencialesInvalidasException("Correo o contrasena incorrectos");
        }

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtService.getExpirationSeconds());
        response.setExpiracion(jwtService.getExpirationDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(FORMATO_FECHA));

        if (principal instanceof UsuarioPrincipal usuario) {
            response.setEmail(usuario.getEmail());
            response.setNombre(usuario.getNombre());
            response.setRol(usuario.getRol());
        }
        return response;
    }

    @Override
    public SesionResponse sesionActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioPrincipal usuario)) {
            throw new CredencialesInvalidasException("No hay una sesion autenticada");
        }

        List<String> autoridades = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();

        return new SesionResponse(usuario.getEmail(), usuario.getNombre(), usuario.getRol(), autoridades);
    }
}
