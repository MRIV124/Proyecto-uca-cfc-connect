package sv.edu.udb.cfcconnect.usuario.dto;

import sv.edu.udb.cfcconnect.usuario.domain.Usuario;

public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private Boolean estado;

    // La contrasena nunca se expone en las respuestas de la API.
    public static UsuarioResponse fromEntity(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.id = u.getId();
        r.nombre = u.getNombre();
        r.email = u.getEmail();
        r.rol = u.getRol() != null ? u.getRol().getNombre() : null;
        r.estado = u.getEstado();
        return r;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public Boolean getEstado() { return estado; }
}
