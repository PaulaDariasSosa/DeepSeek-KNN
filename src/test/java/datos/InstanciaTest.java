package datos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import vectores.Vector;

class InstanciaTest {

    @Test
    void testConstructorLista() {
        List<Object> valores = Arrays.asList(1.0, 2, "clase");
        Instancia instancia = new Instancia(valores);

        assertEquals(3, instancia.getValores().size());
        assertEquals(1.0, instancia.getValores().get(0));
        assertTrue(instancia.getValores().contains("clase"));
    }

    @Test
    void testConstructorString() {
        Instancia instancia = new Instancia("1.5,2,rojo");
        assertEquals(3, instancia.getValores().size());
        assertEquals("1.5", instancia.getValores().get(0));
        assertEquals("rojo", instancia.getValores().get(2));
    }

    @Test
    void testGetVector() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2, 3.5, "clase"));
        Vector vector = instancia.getVector();
        assertEquals(3, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(2.0, vector.get(1));
        assertEquals(3.5, vector.get(2));
    }

    @Test
    void testGetClase() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, "clase"));
        assertEquals("clase", instancia.getClase());
    }

    @Test
    void testNormalizar() {
        Instancia instancia = new Instancia(Arrays.asList(10.0, 20.0, 30.0, "clase"));
        instancia.normalizar();

        assertEquals(0.0, (Double)instancia.getValores().get(0), 0.001);
        assertEquals(0.5, (Double)instancia.getValores().get(1), 0.001);
        assertEquals(1.0, (Double)instancia.getValores().get(2), 0.001);
        assertEquals("clase", instancia.getValores().get(3));
    }

    @Test
    void testEstandarizar() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, 3.0, "clase"));
        instancia.estandarizar();

        // Verificar que la media es ~0 y se preservó la clase
        Vector vector = instancia.getVector();
        assertEquals(0.0, vector.avg(), 0.001);
        assertEquals("clase", instancia.getValores().get(3));
    }

    @Test
    void testDeleteClase() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, "clase"));
        instancia.deleteClase();
        assertEquals(2, instancia.getValores().size());
        assertFalse(instancia.getValores().contains("clase"));
    }

    @Test
    void testToString() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, "clase"));
        assertNotNull(instancia.toString());
        assertTrue(instancia.toString().contains("1.0"));
    }
}