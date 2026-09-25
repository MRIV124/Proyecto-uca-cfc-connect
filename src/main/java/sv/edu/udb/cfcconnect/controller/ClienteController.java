package sv.edu.udb.cfcconnect.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.cfcconnect.repository.domain.Cliente;
import sv.edu.udb.cfcconnect.service.ClienteService;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    // READ - Obtener todos
    @GetMapping
    public ResponseEntity<List<Cliente>> getClientes() {

        return ResponseEntity.ok(service.findAll());
    }

    // READ - Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getCliente(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(
            @RequestBody Cliente cliente) {

        Cliente nuevoCliente = service.save(cliente);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoCliente);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(
            @PathVariable Long id,
            @RequestBody Cliente cliente) {

        return ResponseEntity.ok(
                service.update(id, cliente)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
