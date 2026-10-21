package sv.edu.udb.cfcconnect.seguridad.service.implementation;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.cfcconnect.seguridad.domain.UsuarioPrincipal;
import sv.edu.udb.cfcconnect.usuario.repository.UsuarioRepository;

/**
 * Implementacion de {@link UserDetailsService} que obtiene el usuario desde la
 * tabla {@code usuarios} usando el correo como identificador de inicio de
 * sesion.
 *
 * <p>Spring Security la usa tanto para el login (comparando el hash BCrypt de
 * la contrasena) como para recargar los datos del usuario en cada peticion
 * autenticada por token JWT.</p>
 */
@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(username)
                .map(UsuarioPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe un usuario registrado con el correo " + username));
    }
}
