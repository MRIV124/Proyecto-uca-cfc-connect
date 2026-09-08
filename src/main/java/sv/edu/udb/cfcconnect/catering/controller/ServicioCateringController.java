package sv.edu.udb.cfcconnect.catering.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.edu.udb.cfcconnect.catering.dto.ServicioCateringRequest;
import sv.edu.udb.cfcconnect.catering.dto.ServicioCateringResponse;
import sv.edu.udb.cfcconnect.catering.service.ServicioCateringService;

@RestController
@RequestMapping("/catering")
@Tag(name = "Catering", description = "Modulo de Catering")
public class ServicioCateringController {

    private final ServicioCateringService service;

    public ServicioCateringController(ServicioCateringService service) {
        this.service = service;
    }

    @Operation(summary = "Listar servicios de catering disponibles")
    @GetMapping
    public List<ServicioCateringResponse> getServicios() {
        return service.findAll();
    }

    @Operation(summary = "Obtener un servicio de catering por id")
    @GetMapping("/{id}")
    public ServicioCateringResponse getServicio(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Crear un nuevo servicio de catering")
    @PostMapping
    public ResponseEntity<ServicioCateringResponse> crearServicio(@Valid @RequestBody ServicioCateringRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Actualizar un servicio de catering existente")
    @PutMapping("/{id}")
    public ServicioCateringResponse actualizarServicio(@PathVariable Long id, @Valid @RequestBody ServicioCateringRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar un servicio de catering")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
