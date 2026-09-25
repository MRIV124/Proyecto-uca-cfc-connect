package sv.edu.udb.cfcconnect.dto;

import sv.edu.udb.cfcconnect.repository.domain.Cliente;

public class ClienteResponse {

    private Long id;
    private String nombre;
    private String correo;
    private String telefono;

    public static ClienteResponse fromEntity(Cliente c) {
        ClienteResponse r = new ClienteResponse();
        r.id = c.getId();
        r.nombre = c.getNombre();
        r.correo = c.getCorreo();
        r.telefono = c.getTelefono();
        return r;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
}
