package sv.edu.udb.cfcconnect.espacio.dto;

import sv.edu.udb.cfcconnect.espacio.domain.Espacio;

public class EspacioResponse {

    private Long id;
    private String nombre;
    private String tipo;
    private Integer capacidad;
    private Double precio;
    private String equipamiento;
    private Boolean disponible;

    public static EspacioResponse fromEntity(Espacio e) {
        EspacioResponse r = new EspacioResponse();
        r.id = e.getId();
        r.nombre = e.getNombre();
        r.tipo = e.getTipo();
        r.capacidad = e.getCapacidad();
        r.precio = e.getPrecio();
        r.equipamiento = e.getEquipamiento();
        r.disponible = e.getDisponible();
        return r;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public Integer getCapacidad() { return capacidad; }
    public Double getPrecio() { return precio; }
    public String getEquipamiento() { return equipamiento; }
    public Boolean getDisponible() { return disponible; }
}
