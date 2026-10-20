package sv.edu.udb.cfcconnect.espacio.service.implementation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.udb.cfcconnect.espacio.domain.Espacio;
import sv.edu.udb.cfcconnect.espacio.repository.EspacioRepository;
import sv.edu.udb.cfcconnect.exception.EspacioOcupadoException;
import sv.edu.udb.cfcconnect.exception.ResourceNotFoundException;

/** Pruebas unitarias de la reserva de espacios (JUnit 5 + Mockito). */
@ExtendWith(MockitoExtension.class)
class EspacioServiceImplTest {

    @Mock
    private EspacioRepository repository;

    @InjectMocks
    private EspacioServiceImpl service;

    private Espacio espacio(Long id, String nombre, boolean disponible) {
        Espacio e = new Espacio();
        e.setId(id);
        e.setNombre(nombre);
        e.setTipo("AUDITORIO");
        e.setCapacidad(100);
        e.setPrecio(250.0);
        e.setDisponible(disponible);
        return e;
    }

    @Test
    @DisplayName("Reservar un espacio libre lo marca como no disponible (caso de exito)")
    void reservar_espacioLibre_loMarcaOcupado() {
        Espacio libre = espacio(1L, "Auditorio Central", true);
        when(repository.findById(1L)).thenReturn(Optional.of(libre));
        when(repository.save(any(Espacio.class))).thenAnswer(inv -> inv.getArgument(0));

        service.reservar(1L);

        ArgumentCaptor<Espacio> captor = ArgumentCaptor.forClass(Espacio.class);
        verify(repository).save(captor.capture());
        assertFalse(captor.getValue().getDisponible());
    }

    @Test
    @DisplayName("Reservar un espacio ya ocupado lanza EspacioOcupadoException (fallo por solapamiento)")
    void reservar_espacioOcupado_lanzaEspacioOcupado() {
        Espacio ocupado = espacio(2L, "Sala de Reuniones", false);
        when(repository.findById(2L)).thenReturn(Optional.of(ocupado));

        EspacioOcupadoException ex = assertThrows(EspacioOcupadoException.class,
                () -> service.reservar(2L));

        assertTrue(ex.getMessage().contains("Sala de Reuniones"));
        verify(repository, never()).save(any(Espacio.class));
    }

    @Test
    @DisplayName("Reservar un espacio inexistente lanza ResourceNotFoundException")
    void reservar_espacioInexistente_lanzaNotFound() {
        when(repository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.reservar(77L));
        verify(repository, never()).save(any(Espacio.class));
    }

    @Test
    @DisplayName("Liberar un espacio ocupado lo deja disponible nuevamente")
    void liberar_espacioOcupado_loDejaDisponible() {
        Espacio ocupado = espacio(3L, "Laboratorio 1", false);
        when(repository.findById(3L)).thenReturn(Optional.of(ocupado));
        when(repository.save(any(Espacio.class))).thenAnswer(inv -> inv.getArgument(0));

        service.liberar(3L);

        ArgumentCaptor<Espacio> captor = ArgumentCaptor.forClass(Espacio.class);
        verify(repository).save(captor.capture());
        assertTrue(captor.getValue().getDisponible());
    }
}
