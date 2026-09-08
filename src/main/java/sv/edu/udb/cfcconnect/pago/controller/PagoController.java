package sv.edu.udb.cfcconnect.pago.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.edu.udb.cfcconnect.pago.dto.PagoRequest;
import sv.edu.udb.cfcconnect.pago.dto.PagoResponse;
import sv.edu.udb.cfcconnect.pago.service.PagoService;

@RestController
@RequestMapping("/pagos")
@Tag(name = "Pagos", description = "Modulo de Pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los pagos")
    @GetMapping
    public List<PagoResponse> getPagos() {
        return service.findAll();
    }

    @Operation(summary = "Obtener un pago por id")
    @GetMapping("/{id}")
    public PagoResponse getPago(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Registrar un nuevo pago",
            description = "tipoReferencia: INSCRIPCION, COTIZACION, ESPACIO o CATERING. metodo: EFECTIVO, TARJETA, TRANSFERENCIA o DEPOSITO. Queda en estado PENDIENTE.")
    @PostMapping
    public ResponseEntity<PagoResponse> crearPago(@Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Confirmar un pago", description = "Cambia el estado del pago a PAGADO.")
    @PatchMapping("/{id}/confirmar")
    public PagoResponse confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @Operation(summary = "Eliminar un pago")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
