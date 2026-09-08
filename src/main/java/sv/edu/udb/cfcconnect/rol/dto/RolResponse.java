package sv.edu.udb.cfcconnect.rol.dto;

import sv.edu.udb.cfcconnect.rol.domain.Rol;

public class RolResponse {
    private Long id;
    private String nombre;

    public static RolResponse fromEntity(Rol rol) {
        RolResponse r = new RolResponse();
        r.id = rol.getId();
        r.nombre = rol.getNombre();
        return r;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}
