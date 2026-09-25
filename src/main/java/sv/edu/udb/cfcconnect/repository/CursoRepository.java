package sv.edu.udb.cfcconnect.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;
import sv.edu.udb.cfcconnect.repository.domain.Curso;

import java.util.ArrayList;
import java.util.List;

@Component
public class CursoRepository {

    private List<Curso> cursos;

    @PostConstruct
    public void init() {

        cursos = new ArrayList<>();

        cursos.add(
                Curso.builder()
                        .id(1L)
                        .nombre("Spring Boot")
                        .precio(120.0)
                        .cupo(30)
                        .build()
        );

        cursos.add(
                Curso.builder()
                        .id(2L)
                        .nombre("React")
                        .precio(150.0)
                        .cupo(25)
                        .build()
        );
    }

    public List<Curso> findAll() {
        return cursos;
    }

    public Curso findById(Long id) {

        return cursos.stream()
                .filter(curso -> curso.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Curso no encontrado con ID: " + id
                        )
                );
    }

    public Curso save(Curso curso) {

        Long nuevoId = cursos.stream()
                .mapToLong(Curso::getId)
                .max()
                .orElse(0L) + 1;

        curso.setId(nuevoId);

        cursos.add(curso);

        return curso;
    }

    public Curso update(Long id, Curso cursoActualizado) {

        Curso curso = findById(id);

        curso.setNombre(cursoActualizado.getNombre());
        curso.setPrecio(cursoActualizado.getPrecio());
        curso.setCupo(cursoActualizado.getCupo());

        return curso;
    }

    public void delete(Long id) {

        Curso curso = findById(id);

        cursos.remove(curso);
    }
}
