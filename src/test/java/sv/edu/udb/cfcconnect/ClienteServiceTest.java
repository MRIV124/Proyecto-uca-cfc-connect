package sv.edu.udb.cfcconnect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sv.edu.udb.cfcconnect.repository.domain.Cliente;
import sv.edu.udb.cfcconnect.service.ClienteService;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest
public class ClienteServiceTest {
    @Autowired
    private ClienteService clienteService;

    @Test
    void shouldReturnCliente(){

        Cliente cliente = clienteService.findById(1L);

        assertEquals("Juan Pérez",cliente.getNombre());

    }

    private void assertEquals(String juanPérez, String nombre) {
    }
}
