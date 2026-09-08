package sv.edu.udb.cfcconnect.cliente.controller;

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
import sv.edu.udb.cfcconnect.cliente.dto.ClienteRequest;
import sv.edu.udb.cfcconnect.cliente.dto.ClienteResponse;
import sv.edu.udb.cfcconnect.cliente.service.ClienteService;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Modulo de Gestion de Clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los clientes")
    @GetMapping
    public List<ClienteResponse> getClientes() {
        return service.findAll();
    }

    @Operation(summary = "Obtener un cliente por id")
    @GetMapping("/{id}")
    public ClienteResponse getCliente(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Registrar un nuevo cliente", description = "Valida los datos (correo, telefono, etc.) y que el correo no este duplicado.")
    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Actualizar un cliente existente")
    @PutMapping("/{id}")
    public ClienteResponse actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar un cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
