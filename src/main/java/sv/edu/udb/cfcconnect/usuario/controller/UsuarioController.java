package sv.edu.udb.cfcconnect.usuario.controller;

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
import sv.edu.udb.cfcconnect.usuario.dto.UsuarioRequest;
import sv.edu.udb.cfcconnect.usuario.dto.UsuarioResponse;
import sv.edu.udb.cfcconnect.usuario.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Modulo de Seguridad - usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los usuarios")
    @GetMapping
    public List<UsuarioResponse> getUsuarios() {
        return service.findAll();
    }

    @Operation(summary = "Obtener un usuario por id")
    @GetMapping("/{id}")
    public UsuarioResponse getUsuario(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Crear un nuevo usuario",
            description = "Valida que el rolId exista y que el correo no este duplicado (ReglaNegocioException si lo esta).")
    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Actualizar un usuario existente")
    @PutMapping("/{id}")
    public UsuarioResponse actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar un usuario")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
