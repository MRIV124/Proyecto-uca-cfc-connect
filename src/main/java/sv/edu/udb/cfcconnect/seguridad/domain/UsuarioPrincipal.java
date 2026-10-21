package sv.edu.udb.cfcconnect.seguridad.domain;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sv.edu.udb.cfcconnect.usuario.domain.Usuario;

/**
 * Representacion de un usuario autenticado que implementa {@link UserDetails}.
 *
 * <p>Spring Security trabaja con {@code UserDetails}, pero el proyecto ya
 * tiene su entidad {@link Usuario}. Esta clase funciona como adaptador entre
 * ambas: conserva los datos del usuario (id, nombre, correo, rol) y expone las
 * autoridades convertidas a roles de Spring ({@code ROLE_ADMIN},
 * {@code ROLE_RECEPCIONISTA}, etc.) para el control de acceso por rol.</p>
 *
 * <p>El estado de la cuenta ({@code estado}) se expone como
 * {@link #isEnabled()}: si el usuario esta inactivo, Spring Security rechaza
 * el inicio de sesion automaticamente.</p>
 */
public class UsuarioPrincipal implements UserDetails {

    private static final long serialVersionUID = 1L;

    /** Prefijo estandar de Spring Security para convertir un rol en autoridad. */
    public static final String ROLE_PREFIX = "ROLE_";

    private final Long id;
    private final String nombre;
    private final String email;
    private final String password;
    private final String rol;
    private final boolean estado;
    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioPrincipal(Long id, String nombre, String email, String password, String rol, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
        this.authorities = List.of(new SimpleGrantedAuthority(ROLE_PREFIX + rol));
    }

    /** Construye el principal a partir de la entidad {@link Usuario}. */
    public static UsuarioPrincipal from(Usuario usuario) {
        return new UsuarioPrincipal(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getRol() != null ? usuario.getRol().getNombre() : "CLIENTE",
                !Boolean.FALSE.equals(usuario.getEstado()));
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public boolean isEstado() { return estado; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return estado;
    }
}
