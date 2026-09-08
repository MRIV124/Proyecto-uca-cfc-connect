package sv.edu.udb.cfcconnect.rol.controller;

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
import sv.edu.udb.cfcconnect.rol.dto.RolRequest;
import sv.edu.udb.cfcconnect.rol.dto.RolResponse;
import sv.edu.udb.cfcconnect.rol.service.RolService;

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "Modulo de Seguridad - roles")
public class RolController {

    private final RolService service;

    public RolController(RolService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los roles")
    @GetMapping
    public List<RolResponse> getRoles() {
        return service.findAll();
    }

    @Operation(summary = "Obtener un rol por id")
    @GetMapping("/{id}")
    public RolResponse getRol(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Crear un nuevo rol")
    @PostMapping
    public ResponseEntity<RolResponse> crearRol(@Valid @RequestBody RolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Actualizar un rol existente")
    @PutMapping("/{id}")
    public RolResponse actualizarRol(@PathVariable Long id, @Valid @RequestBody RolRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar un rol")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
