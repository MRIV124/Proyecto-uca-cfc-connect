package sv.edu.udb.cfcconnect.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.cfcconnect.repository.domain.Curso;
import sv.edu.udb.cfcconnect.service.CursoService;

import java.util.List;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService service;

    public CursoController(CursoService service) {
        this.service = service;
    }

    // READ - Obtener todos
    @GetMapping
    public ResponseEntity<List<Curso>> getCursos() {

        return ResponseEntity.ok(service.findAll());
    }

    // READ - Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Curso> getCurso(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Curso> crearCurso(
            @RequestBody Curso curso) {

        Curso nuevoCurso = service.save(curso);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoCurso);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Curso> actualizarCurso(
            @PathVariable Long id,
            @RequestBody Curso curso) {

        return ResponseEntity.ok(
                service.update(id, curso)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
