package vectores;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

class MatrizTest {
    private Matriz matriz2x2;
    private Matriz matriz2x3;
    private Matriz matriz3x2;

    @BeforeEach
    void setUp() {
        // Matriz 2x2
        double[][] datos2x2 = {{1, 2}, {3, 4}};
        matriz2x2 = new Matriz(2, 2, datos2x2);

        // Matriz 2x3
        double[][] datos2x3 = {{1, 2, 3}, {4, 5, 6}};
        matriz2x3 = new Matriz(2, 3, datos2x3);

        // Matriz 3x2
        double[][] datos3x2 = {{1, 2}, {3, 4}, {5, 6}};
        matriz3x2 = new Matriz(3, 2, datos3x2);
    }

    @Test
    void testConstructorVacio() {
        Matriz m = new Matriz();
        assertEquals(1, m.getNumRows());
        assertEquals(1, m.getNumCols());
        assertEquals(0.0, m.get(0, 0));
    }

    @Test
    void testConstructorDimensiones() {
        Matriz m = new Matriz(3, 4);
        assertEquals(3, m.getNumRows());
        assertEquals(4, m.getNumCols());
    }

    @Test
    void testConstructorConDatos() {
        assertEquals(1.0, matriz2x2.get(0, 0));
        assertEquals(4.0, matriz2x2.get(1, 1));
    }

    @Test
    void testConstructorConVectores() {
        List<Vector> vectores = new ArrayList<>();
        vectores.add(new Vector(new double[]{1, 2}));
        vectores.add(new Vector(new double[]{3, 4}));

        Matriz m = new Matriz(vectores);
        assertEquals(2, m.getNumRows());
        assertEquals(2, m.getNumCols());
        assertEquals(3.0, m.get(1, 0));
    }

    @Test
    void testGetSet() {
        matriz2x2.set(0, 1, 5.0);
        assertEquals(5.0, matriz2x2.get(0, 1));
    }

    @Test
    void testGetInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> matriz2x2.get(3, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> matriz2x2.get(0, 3));
    }

    @Test
    void testMultiplicacion() {
        Matriz a = new Matriz(2, 2);
        a.set(0, 0, 1); a.set(0, 1, 2);
        a.set(1, 0, 3); a.set(1, 1, 4);

        Matriz b = new Matriz(2, 2);
        b.set(0, 0, 5); b.set(0, 1, 6);
        b.set(1, 0, 7); b.set(1, 1, 8);

        Matriz res = Matriz.multiply(a, b);
        assertEquals(19.0, res.get(0, 0), 0.001);
        assertEquals(22.0, res.get(0, 1), 0.001);
        assertEquals(43.0, res.get(1, 0), 0.001);
        assertEquals(50.0, res.get(1, 1), 0.001);
    }

    @Test
    void testTransposicion() {
        matriz2x3.transpose();
        assertEquals(2, matriz2x3.getNumRows());
        assertEquals(3, matriz2x3.getNumCols());
        assertEquals(1.0, matriz2x3.get(0, 0), 0.001);
        assertEquals(4.0, matriz2x3.get(0, 1), 0.001);
        assertEquals(3.0, matriz2x3.get(2, 0), 0.001);
        assertEquals(6.0, matriz2x3.get(2, 1), 0.001);
    }

    @Test
    void testDobleTransposicion() {
        matriz2x3.transpose();
        matriz2x3.transpose();
        assertEquals(2, matriz2x3.getNumRows());
        assertEquals(3, matriz2x3.getNumCols());
        assertEquals(1.0, matriz2x3.get(0, 0), 0.001);
    }

    @Test
    void testEquals() {
        Matriz m1 = new Matriz(2, 2);
        m1.set(0, 0, 1); m1.set(0, 1, 2);
        m1.set(1, 0, 3); m1.set(1, 1, 4);

        Matriz m2 = new Matriz(2, 2);
        m2.set(0, 0, 1); m2.set(0, 1, 2);
        m2.set(1, 0, 3); m2.set(1, 1, 4);

        assertTrue(m1.equals(m2));
    }

    @Test
    void testNotEquals() {
        Matriz m1 = new Matriz(2, 2);
        m1.set(0, 0, 1); m1.set(0, 1, 2);
        m1.set(1, 0, 3); m1.set(1, 1, 4);

        Matriz m2 = new Matriz(2, 2);
        m2.set(0, 0, 1); m2.set(0, 1, 2);
        m2.set(1, 0, 3); m2.set(1, 1, 5);

        assertFalse(m1.equals(m2));
    }

    @Test
    void testAddRows() {
        matriz2x2.addRows();
        assertEquals(3, matriz2x2.getNumRows());
    }

    @Test
    void testAddCols() {
        matriz2x2.addCols();
        assertEquals(3, matriz2x2.getNumCols());
    }

    @Test
    void testReadWrite() throws IOException {
        String filename = "test_matrix.txt";
        matriz2x2.write(filename);

        Matriz leida = new Matriz().read(filename);
        assertTrue(matriz2x2.equals(leida));
    }

    @Test
    void testDeleteRows() {
        assertEquals(3, matriz3x2.getNumRows());
        matriz3x2.deleteRows(1);
        assertEquals(2, matriz3x2.getNumRows());
        assertEquals(1.0, matriz3x2.get(0, 0));
        assertEquals(5.0, matriz3x2.get(1, 0));
    }

    @Test
    void testDeleteCols() {
        assertEquals(3, matriz2x3.getNumCols());
        matriz2x3.deleteCols(1);
        assertEquals(2, matriz2x3.getNumCols());
        assertEquals(1.0, matriz2x3.get(0, 0));
        assertEquals(3.0, matriz2x3.get(0, 1));
        assertEquals(4.0, matriz2x3.get(1, 0));
        assertEquals(6.0, matriz2x3.get(1, 1));
    }

    @Test
    void testMultiplicacionDimensionesInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> Matriz.multiply(matriz2x2, matriz3x2),
                "Debería lanzar IllegalArgumentException cuando las dimensiones no son compatibles");
    }

    @Test
    void testNormalizacion() {
        Matriz m = new Matriz(1, 3);
        m.set(0, 0, 10); m.set(0, 1, 20); m.set(0, 2, 30);

        List<Vector> norm = m.normalizar();
        assertEquals(0.5, norm.get(0).get(0), 0.001);
    }

    @Test
    void testNormalizacionTodosIguales() {
        Matriz m = new Matriz(1, 3);
        m.set(0, 0, 5); m.set(0, 1, 5); m.set(0, 2, 5);

        List<Vector> norm = m.normalizar();
        assertEquals(0.5, norm.get(0).get(0), 0.001);
    }
}
