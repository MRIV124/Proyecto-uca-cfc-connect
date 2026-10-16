package sv.edu.udb.cfcconnect.curso.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sv.edu.udb.cfcconnect.curso.domain.Curso;
import sv.edu.udb.cfcconnect.curso.dto.CursoRequest;
import sv.edu.udb.cfcconnect.curso.dto.CursoResponse;
import sv.edu.udb.cfcconnect.curso.repository.CursoRepository;
import sv.edu.udb.cfcconnect.exception.CupoAgotadoException;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;

/**
 * Pruebas unitarias de la capa de negocio de Cursos (JUnit 5 + Mockito).
 * El repositorio se simula con Mockito: no se usa base de datos ni Spring.
 */
@ExtendWith(MockitoExtension.class)
class CursoServiceImplTest {

    @Mock
    private CursoRepository repository;

    @InjectMocks
    private CursoServiceImpl service;

    private Curso curso(Long id, String nombre, int cupo, int inscritos) {
        Curso c = new Curso();
        c.setId(id);
        c.setNombre(nombre);
        c.setPrecio(100.0);
        c.setCupo(cupo);
        c.setInscritos(inscritos);
        return c;
    }

    // ---------- Caso de exito ----------

    @Test
    @DisplayName("Inscribir con cupo disponible incrementa los inscritos y guarda")
    void inscribirParticipante_conCupo_incrementaInscritos() {
        Curso c = curso(1L, "Java Avanzado", 3, 2);
        when(repository.findById(1L)).thenReturn(Optional.of(c));
        when(repository.save(any(Curso.class))).thenAnswer(inv -> inv.getArgument(0));

        CursoResponse respuesta = service.inscribirParticipante(1L);

        assertEquals(3, respuesta.getInscritos());
        assertFalse(respuesta.getCupoDisponible(), "al llegar al maximo ya no debe quedar cupo");
        verify(repository).save(c);
    }

    // ---------- Caso de fallo por regla de negocio ----------

    @Test
    @DisplayName("Inscribir en un curso lleno lanza CupoAgotadoException y no guarda nada")
    void inscribirParticipante_sinCupo_lanzaCupoAgotado() {
        Curso lleno = curso(2L, "Spring Boot", 2, 2);
        when(repository.findById(2L)).thenReturn(Optional.of(lleno));

        CupoAgotadoException ex = assertThrows(CupoAgotadoException.class,
                () -> service.inscribirParticipante(2L));

        assertTrue(ex.getMessage().contains("Spring Boot"));
        verify(repository, never()).save(any(Curso.class));
    }

    // ---------- Caso de fallo por recurso inexistente ----------

    @Test
    @DisplayName("Inscribir en un curso inexistente lanza ResourceNotFoundException")
    void inscribirParticipante_cursoInexistente_lanzaNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.inscribirParticipante(99L));
        verify(repository, never()).save(any(Curso.class));
    }

    @Test
    @DisplayName("Eliminar un curso inexistente lanza ResourceNotFoundException y no elimina")
    void delete_cursoInexistente_lanzaNotFound() {
        when(repository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(50L));
        verify(repository, never()).delete(any(Curso.class));
    }

    // ---------- CRUD ----------

    @Test
    @DisplayName("Crear un curso siempre inicia con 0 inscritos")
    void create_iniciaInscritosEnCero() {
        CursoRequest request = new CursoRequest();
        request.setNombre("Excel Empresarial");
        request.setPrecio(75.5);
        request.setCupo(30);
        when(repository.save(any(Curso.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(request);

        ArgumentCaptor<Curso> captor = ArgumentCaptor.forClass(Curso.class);
        verify(repository).save(captor.capture());
        Curso guardado = captor.getValue();
        assertEquals("Excel Empresarial", guardado.getNombre());
        assertEquals(30, guardado.getCupo());
        assertEquals(0, guardado.getInscritos());
    }

    // ---------- Busqueda (tarea de busquedas personalizadas) ----------

    @Test
    @DisplayName("Buscar con nombre nulo se normaliza a cadena vacia (sin filtro)")
    void buscar_nombreNulo_seNormalizaAVacio() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Curso> pagina = new PageImpl<>(List.of(curso(1L, "Java Avanzado", 20, 5)), pageable, 1);
        when(repository.buscar("", false, pageable)).thenReturn(pagina);

        Page<CursoResponse> resultado = service.buscar(null, false, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Java Avanzado", resultado.getContent().get(0).getNombre());
    }

    @Test
    @DisplayName("Buscar recorta espacios del nombre y respeta el filtro soloConCupo")
    void buscar_recortaNombreYPasaFiltroCupo() {
        Pageable pageable = PageRequest.of(0, 5);
        when(repository.buscar("java", true, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<CursoResponse> resultado = service.buscar("  java  ", true, pageable);

        assertTrue(resultado.isEmpty());
        verify(repository).buscar("java", true, pageable);
    }
}
