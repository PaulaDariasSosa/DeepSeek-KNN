package procesamiento;

import datos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class EstandarizacionTest {
    private Dataset dataset;

    @BeforeEach
    void setUp() {
        dataset = new Dataset();
        Cuantitativo edad = new Cuantitativo("edad");
        edad.add(10.0);
        edad.add(20.0);
        edad.add(30.0);
        dataset.getAtributos().add(edad);
    }

    @Test
    void testProcesar() {
        Estandarizacion est = new Estandarizacion();
        List<Atributo> resultado = est.procesar(dataset);

        Cuantitativo edadEst = (Cuantitativo) resultado.get(0);
        // Valores esperados usando fórmula z = (x - μ)/σ
        // μ = 20, σ = 8.164966
        assertEquals(-1.0, edadEst.getValores().get(0), 0.001);
        assertEquals(0.0, edadEst.getValores().get(1), 0.001);
        assertEquals(1.0, edadEst.getValores().get(2), 0.001);
    }

    @Test
    void testIgnorarCualitativos() {
        dataset.getAtributos().add(new Cualitativo("color", "rojo"));

        Estandarizacion est = new Estandarizacion();
        List<Atributo> resultado = est.procesar(dataset);

        assertEquals(2, resultado.size());
        assertTrue(resultado.get(1) instanceof Cualitativo);
        assertEquals("rojo", resultado.get(1).getValor(0));
    }

    @Test
    void testDatasetVacio() {
        Dataset vacio = new Dataset();
        Estandarizacion est = new Estandarizacion();

        List<Atributo> resultado = est.procesar(vacio);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testUnSoloValor() {
        Dataset ds = new Dataset();
        Cuantitativo temp = new Cuantitativo("temp", 25.0);
        ds.getAtributos().add(temp);

        Estandarizacion est = new Estandarizacion();
        List<Atributo> resultado = est.procesar(ds);

        // Con un solo valor, la desviación es 0 y no se puede estandarizar
        Cuantitativo tempEst = (Cuantitativo) resultado.get(0);
        assertEquals(25.0, tempEst.getValores().get(0), 0.001);
    }
}