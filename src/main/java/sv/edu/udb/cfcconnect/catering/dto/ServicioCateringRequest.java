package sv.edu.udb.cfcconnect.catering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ServicioCateringRequest {

    @NotBlank(message = "El nombre del servicio es obligatorio")
    private String nombre;

    @NotNull(message = "El precio por persona es obligatorio")
    @Positive(message = "El precio por persona debe ser mayor a cero")
    private Double precioPorPersona;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecioPorPersona() { return precioPorPersona; }
    public void setPrecioPorPersona(Double precioPorPersona) { this.precioPorPersona = precioPorPersona; }
}
