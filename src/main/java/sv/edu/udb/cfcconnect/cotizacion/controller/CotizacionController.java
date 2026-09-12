package sv.edu.udb.cfcconnect.cotizacion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.edu.udb.cfcconnect.cotizacion.dto.CotizacionRequest;
import sv.edu.udb.cfcconnect.cotizacion.dto.CotizacionResponse;
import sv.edu.udb.cfcconnect.cotizacion.service.CotizacionService;

@RestController
@RequestMapping("/cotizaciones")
@Tag(name = "Cotizaciones", description = "Modulo de Cotizaciones")
public class CotizacionController {

    private final CotizacionService service;

    public CotizacionController(CotizacionService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas las cotizaciones")
    @GetMapping
    public List<CotizacionResponse> getCotizaciones() {
        return service.findAll();
    }

    @Operation(summary = "Obtener una cotizacion por id")
    @GetMapping("/{id}")
    public CotizacionResponse getCotizacion(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Solicitar una nueva cotizacion",
            description = "tipo debe ser CURSO, DIPLOMADO, ESPACIO, CATERING o COMBINADO. Queda en estado PENDIENTE.")
    @PostMapping
    public ResponseEntity<CotizacionResponse> crearCotizacion(@Valid @RequestBody CotizacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Actualizar una cotizacion",
            description = "Solo se puede editar mientras este en estado PENDIENTE o EN_PROCESO; lanza ReglaNegocioException (HTTP 409) si ya fue aprobada o rechazada.")
    @PutMapping("/{id}")
    public CotizacionResponse actualizarCotizacion(@PathVariable Long id, @Valid @RequestBody CotizacionRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Aprobar una cotizacion",
            description = "Lanza ReglaNegocioException (HTTP 409) si la cotizacion ya fue rechazada.")
    @PatchMapping("/{id}/aprobar")
    public CotizacionResponse aprobar(@PathVariable Long id) {
        return service.aprobar(id);
    }

    @Operation(summary = "Rechazar una cotizacion",
            description = "Lanza ReglaNegocioException (HTTP 409) si la cotizacion ya fue aprobada.")
    @PatchMapping("/{id}/rechazar")
    public CotizacionResponse rechazar(@PathVariable Long id) {
        return service.rechazar(id);
    }

    @Operation(summary = "Eliminar una cotizacion")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCotizacion(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
