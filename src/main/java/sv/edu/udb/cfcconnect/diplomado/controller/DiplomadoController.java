package sv.edu.udb.cfcconnect.diplomado.controller;

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
import sv.edu.udb.cfcconnect.diplomado.dto.DiplomadoRequest;
import sv.edu.udb.cfcconnect.diplomado.dto.DiplomadoResponse;
import sv.edu.udb.cfcconnect.diplomado.service.DiplomadoService;

@RestController
@RequestMapping("/diplomados")
@Tag(name = "Diplomados", description = "Modulo de Gestion Academica - diplomados")
public class DiplomadoController {

    private final DiplomadoService service;

    public DiplomadoController(DiplomadoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los diplomados")
    @GetMapping
    public List<DiplomadoResponse> getDiplomados() {
        return service.findAll();
    }

    @Operation(summary = "Obtener un diplomado por id")
    @GetMapping("/{id}")
    public DiplomadoResponse getDiplomado(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Crear un nuevo diplomado")
    @PostMapping
    public ResponseEntity<DiplomadoResponse> crearDiplomado(@Valid @RequestBody DiplomadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Actualizar un diplomado existente")
    @PutMapping("/{id}")
    public DiplomadoResponse actualizarDiplomado(@PathVariable Long id, @Valid @RequestBody DiplomadoRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Inscribir un participante en el diplomado",
            description = "Lanza CupoAgotadoException (HTTP 409) si el diplomado ya alcanzo su cupo maximo.")
    @PatchMapping("/{id}/inscribir")
    public DiplomadoResponse inscribirParticipante(@PathVariable Long id) {
        return service.inscribirParticipante(id);
    }

    @Operation(summary = "Eliminar un diplomado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDiplomado(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
