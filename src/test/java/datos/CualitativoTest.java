package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class CualitativoTest {
    private Cualitativo atributo;

    @BeforeEach
    void setUp() {
        atributo = new Cualitativo("color");
        atributo.add("rojo");
        atributo.add("azul");
        atributo.add("rojo");
        atributo.add("verde");
    }

    @Test
    void testClases() {
        List<String> clases = atributo.clases();
        assertEquals(3, clases.size());
        assertTrue(clases.contains("rojo"));
    }

    @Test
    void testFrecuencia() {
        List<Double> frecuencias = atributo.frecuencia();
        assertEquals(0.5, frecuencias.get(0), 0.001); // rojo
        assertEquals(0.25, frecuencias.get(1), 0.001); // azul
    }

    @Test
    void testAddObject() {
        atributo.add("amarillo");
        assertEquals(5, atributo.size());
    }

    @Test
    void testGetValor() {
        assertEquals("rojo", atributo.getValor(0));
    }

    @Test
    void testCopiar() {
        Cualitativo copia = atributo.copiar();
        assertEquals(atributo.getNombre(), copia.getNombre());
        assertEquals(atributo.size(), copia.size());
        assertNotSame(atributo, copia);
    }
}
