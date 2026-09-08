package sv.edu.udb.cfcconnect.curso.controller;

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
import sv.edu.udb.cfcconnect.curso.dto.CursoRequest;
import sv.edu.udb.cfcconnect.curso.dto.CursoResponse;
import sv.edu.udb.cfcconnect.curso.service.CursoService;

@RestController
@RequestMapping("/cursos")
@Tag(name = "Cursos", description = "Modulo de Gestion Academica - cursos")
public class CursoController {

    private final CursoService service;

    public CursoController(CursoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los cursos")
    @GetMapping
    public List<CursoResponse> getCursos() {
        return service.findAll();
    }

    @Operation(summary = "Obtener un curso por id")
    @GetMapping("/{id}")
    public CursoResponse getCurso(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Crear un nuevo curso")
    @PostMapping
    public ResponseEntity<CursoResponse> crearCurso(@Valid @RequestBody CursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Actualizar un curso existente")
    @PutMapping("/{id}")
    public CursoResponse actualizarCurso(@PathVariable Long id, @Valid @RequestBody CursoRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Inscribir un participante en el curso",
            description = "Caso de exito: incrementa inscritos si hay cupo. Caso de fallo de negocio: lanza CupoAgotadoException (HTTP 409) si el cupo ya se alcanzo.")
    @PatchMapping("/{id}/inscribir")
    public CursoResponse inscribirParticipante(@PathVariable Long id) {
        return service.inscribirParticipante(id);
    }

    @Operation(summary = "Eliminar un curso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
