package procesamiento;

import datos.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class NormalizacionTest {
    @Test
    void testProcesar() {
        Dataset datos = new Dataset();
        Cuantitativo edad = new Cuantitativo("edad");
        edad.add(20.0);
        edad.add(40.0);
        edad.add(60.0);
        datos.getAtributos().add(edad);

        Normalizacion normalizacion = new Normalizacion();
        List<Atributo> resultado = normalizacion.procesar(datos);

        Cuantitativo edadNorm = (Cuantitativo) resultado.get(0);
        assertEquals(0.0, edadNorm.getValores().get(0), 0.001);
        assertEquals(0.5, edadNorm.getValores().get(1), 0.001);
        assertEquals(1.0, edadNorm.getValores().get(2), 0.001);
    }

    @Test
    void testProcesarConAtributoCualitativo() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cualitativo("color", "rojo"));

        Normalizacion normalizacion = new Normalizacion();
        List<Atributo> resultado = normalizacion.procesar(datos);

        // No debería modificar atributos cualitativos
        assertEquals("rojo", resultado.get(0).getValor(0));
    }
}