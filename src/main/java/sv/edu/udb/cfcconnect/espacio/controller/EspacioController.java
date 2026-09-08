package sv.edu.udb.cfcconnect.espacio.controller;

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
import sv.edu.udb.cfcconnect.espacio.dto.EspacioRequest;
import sv.edu.udb.cfcconnect.espacio.dto.EspacioResponse;
import sv.edu.udb.cfcconnect.espacio.service.EspacioService;

@RestController
@RequestMapping("/espacios")
@Tag(name = "Espacios", description = "Modulo de Alquiler de Espacios")
public class EspacioController {

    private final EspacioService service;

    public EspacioController(EspacioService service) {
        this.service = service;
    }

    @Operation(summary = "Listar espacios", description = "Usar ?soloDisponibles=true para consultar solo los espacios disponibles.")
    @GetMapping
    public List<EspacioResponse> getEspacios(@RequestParam(required = false, defaultValue = "false") boolean soloDisponibles) {
        return soloDisponibles ? service.findDisponibles() : service.findAll();
    }

    @Operation(summary = "Obtener un espacio por id")
    @GetMapping("/{id}")
    public EspacioResponse getEspacio(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Registrar un nuevo espacio")
    @PostMapping
    public ResponseEntity<EspacioResponse> crearEspacio(@Valid @RequestBody EspacioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Actualizar un espacio existente")
    @PutMapping("/{id}")
    public EspacioResponse actualizarEspacio(@PathVariable Long id, @Valid @RequestBody EspacioRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Reservar el espacio",
            description = "Caso de fallo por solapamiento: lanza EspacioOcupadoException (HTTP 409) si el espacio ya esta reservado.")
    @PatchMapping("/{id}/reservar")
    public EspacioResponse reservar(@PathVariable Long id) {
        return service.reservar(id);
    }

    @Operation(summary = "Liberar el espacio (cancelacion o finalizacion del evento)")
    @PatchMapping("/{id}/liberar")
    public EspacioResponse liberar(@PathVariable Long id) {
        return service.liberar(id);
    }

    @Operation(summary = "Eliminar un espacio")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEspacio(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
