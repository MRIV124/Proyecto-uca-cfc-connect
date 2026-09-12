package sv.edu.udb.cfcconnect.inscripcion.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sv.edu.udb.cfcconnect.inscripcion.dto.InscripcionRequest;
import sv.edu.udb.cfcconnect.inscripcion.dto.InscripcionResponse;
import sv.edu.udb.cfcconnect.inscripcion.service.InscripcionService;

@RestController
@RequestMapping("/inscripciones")
@Tag(name = "Inscripciones", description = "Modulo de Inscripciones")
public class InscripcionController {

    private final InscripcionService service;

    public InscripcionController(InscripcionService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas las inscripciones")
    @GetMapping
    public List<InscripcionResponse> getInscripciones() {
        return service.findAll();
    }

    @Operation(summary = "Obtener una inscripcion por id")
    @GetMapping("/{id}")
    public InscripcionResponse getInscripcion(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Registrar una nueva inscripcion",
            description = "Valida que el cliente y el curso existan, y que el curso tenga cupo disponible (CupoAgotadoException si no).")
    @PostMapping
    public ResponseEntity<InscripcionResponse> crearInscripcion(@Valid @RequestBody InscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Actualizar una inscripcion",
            description = "Permite cambiar cliente o curso. Si el curso cambia, ajusta el cupo del curso anterior y valida cupo en el nuevo (CupoAgotadoException si no hay). No aplica a inscripciones CANCELADA o FINALIZADA.")
    @PutMapping("/{id}")
    public InscripcionResponse actualizarInscripcion(@PathVariable Long id, @Valid @RequestBody InscripcionRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Cambiar el estado de una inscripcion",
            description = "Valores permitidos: PENDIENTE, CONFIRMADA, CANCELADA, FINALIZADA.")
    @PatchMapping("/{id}/estado")
    public InscripcionResponse cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return service.cambiarEstado(id, estado);
    }

    @Operation(summary = "Eliminar una inscripcion")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInscripcion(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
