package procesamiento;

import datos.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatosCrudosTest {

    @Test
    void testProcesar() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("edad", 25.0));

        DatosCrudos proc = new DatosCrudos();
        List<Atributo> resultado = proc.procesar(datos);

        assertEquals(1, resultado.size());
        assertEquals(25.0, resultado.get(0).getValor(0));
    }
}
