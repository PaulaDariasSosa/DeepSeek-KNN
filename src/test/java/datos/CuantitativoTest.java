package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import vectores.Vector;

class CuantitativoTest {
    private Cuantitativo atributo;

    @BeforeEach
    void setUp() {
        atributo = new Cuantitativo("edad");
        atributo.add(25.0);
        atributo.add(30.0);
        atributo.add(35.0);
    }

    @Test
    void testConstructorVacio() {
        Cuantitativo c = new Cuantitativo();
        assertEquals("", c.getNombre());
        assertEquals(0, c.size());
    }

    @Test
    void testConstructorConNombre() {
        Cuantitativo c = new Cuantitativo("altura");
        assertEquals("altura", c.getNombre());
        assertEquals(0, c.size());
    }

    @Test
    void testConstructorConNombreYValor() {
        Cuantitativo c = new Cuantitativo("altura", 180.0);
        assertEquals("altura", c.getNombre());
        assertEquals(1, c.size());
        assertEquals(180.0, c.getValor(0));
    }

    @Test
    void testConstructorConNombreYVector() {
        Vector v = new Vector(new double[]{25, 30, 35});
        Cuantitativo c = new Cuantitativo("edad", v);
        assertEquals("edad", c.getNombre());
        assertEquals(3, c.size());
        assertEquals(25.0, c.getValor(0));
    }

    @Test
    void testMinimo() {
        assertEquals(25.0, atributo.minimo(), 0.001);

        // Añadir valor más pequeño
        atributo.add(20.0);
        assertEquals(20.0, atributo.minimo(), 0.001);
    }

    @Test
    void testMaximo() {
        assertEquals(35.0, atributo.maximo(), 0.001);

        // Añadir valor más grande
        atributo.add(40.0);
        assertEquals(40.0, atributo.maximo(), 0.001);
    }

    @Test
    void testMedia() {
        // [25, 30, 35] -> media = (25 + 30 + 35)/3 = 30
        assertEquals(30.0, atributo.media(), 0.001);

        Cuantitativo vacio = new Cuantitativo("vacio");
        assertEquals(NaN, vacio.media());
    }

    @Test
    void testDesviacionTipica() {
        // Para [25, 30, 35]:
        // Varianza = [(25-30)² + (30-30)² + (35-30)²] / (3-1) = [25 + 0 + 25]/2 = 25
        // Desviación = √25 = 5
        double desviacion = atributo.desviacion();
        assertEquals(5.0, desviacion, 0.001);

        // Test con un solo valor
        Cuantitativo c = new Cuantitativo("test", 10.0);
        assertEquals(0.0, c.desviacion(), 0.001);
    }

    @Test
    void testEstandarizacion() {
        atributo.estandarizacion();
        Vector valores = atributo.getValores();

        // Valores esperados para [25,30,35] estandarizados:
        // (25-30)/5 = -1
        // (30-30)/5 = 0
        // (35-30)/5 = 1
        assertEquals(-1.0, valores.get(0), 0.001);
        assertEquals(0.0, valores.get(1), 0.001);
        assertEquals(1.0, valores.get(2), 0.001);
    }

    @Test
    void testAddObject() {
        atributo.add(40.0);
        assertEquals(4, atributo.size());
        assertEquals(40.0, atributo.getValor(3));

        // Test con enteros
        atributo.add(45);
        assertEquals(45.0, atributo.getValor(4));

        // Test con valor no numérico
        assertThrows(IllegalArgumentException.class, () -> atributo.add("no numérico"));
    }

    @Test
    void testGetValor() {
        assertEquals(25.0, (Double)atributo.getValor(0), 0.001);
        assertThrows(IndexOutOfBoundsException.class, () -> atributo.getValor(5));
    }

    @Test
    void testDelete() {
        atributo.delete(0);
        assertEquals(2, atributo.size());
        assertEquals(30.0, atributo.getValor(0));

        assertThrows(IndexOutOfBoundsException.class, () -> atributo.delete(5));
    }

    @Test
    void testCopiar() {
        Cuantitativo copia = atributo.copiar();
        assertEquals(atributo.getNombre(), copia.getNombre());
        assertEquals(atributo.size(), copia.size());
        assertNotSame(atributo.getValores(), copia.getValores());
        assertEquals(atributo.getValor(0), copia.getValor(0));
    }

    @Test
    void testToString() {
        String str = atributo.toString();
        assertTrue(str.contains("25.0"));
        assertTrue(str.contains("35.0"));
    }

    @Test
    void testClear() {
        atributo.clear();
        assertEquals(0, atributo.size());
    }
}