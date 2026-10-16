package sv.edu.udb.cfcconnect.inscripcion.service.implementation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.udb.cfcconnect.cliente.repository.ClienteRepository;
import sv.edu.udb.cfcconnect.curso.repository.CursoRepository;
import sv.edu.udb.cfcconnect.inscripcion.repository.InscripcionRepository;

/**
 * Verifica que el filtro de busqueda de inscripciones elija la consulta
 * correcta del repositorio segun los parametros recibidos.
 */
@ExtendWith(MockitoExtension.class)
class InscripcionServiceImplTest {

    @Mock
    private InscripcionRepository repository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private InscripcionServiceImpl service;

    @Test
    @DisplayName("Con clienteId y cursoId usa la consulta combinada")
    void buscar_clienteYCurso_usaConsultaCombinada() {
        when(repository.findByClienteIdAndCursoId(1L, 2L)).thenReturn(List.of());

        assertTrue(service.buscar(1L, 2L).isEmpty());

        verify(repository).findByClienteIdAndCursoId(1L, 2L);
        verify(repository, never()).findAll();
    }

    @Test
    @DisplayName("Solo con clienteId filtra por cliente")
    void buscar_soloCliente_filtraPorCliente() {
        when(repository.findByClienteId(5L)).thenReturn(List.of());

        assertTrue(service.buscar(5L, null).isEmpty());

        verify(repository).findByClienteId(5L);
    }

    @Test
    @DisplayName("Solo con cursoId filtra por curso")
    void buscar_soloCurso_filtraPorCurso() {
        when(repository.findByCursoId(8L)).thenReturn(List.of());

        assertTrue(service.buscar(null, 8L).isEmpty());

        verify(repository).findByCursoId(8L);
    }

    @Test
    @DisplayName("Sin filtros devuelve todas las inscripciones")
    void buscar_sinFiltros_devuelveTodas() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(service.buscar(null, null).isEmpty());

        verify(repository).findAll();
    }
}
