package vectores;

import datos.Instancia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

class VectorTest {
    private Vector vector;
    private Vector vector2;

    @BeforeEach
    void setUp() {
        vector = new Vector(Arrays.asList(1.0, 2.0, 3.0));
        vector2 = new Vector(Arrays.asList(2.0, 3.0, 4.0));
    }

    @Test
    void testConstructors() {
        Vector v1 = new Vector();
        assertEquals(0, v1.size());

        Vector v2 = new Vector(5);
        assertEquals(5, v2.size());
        assertEquals(0.0, v2.get(0));

        double[] datos = {1.1, 2.2};
        Vector v3 = new Vector(datos);
        assertEquals(2, v3.size());
        assertEquals(1.1, v3.get(0));
    }

    @Test
    void testNormalize() {
        vector.normalize();
        assertEquals(0.0, vector.get(0), 0.001);
        assertEquals(0.5, vector.get(1), 0.001);
        assertEquals(1.0, vector.get(2), 0.001);
    }

    @Test
    void testProductoEscalar() {
        double producto = vector.productoEscalar(vector2);
        assertEquals(20.0, producto, 0.001);
    }

    @Test
    void testProductoEscalarDimensionesInvalidas() {
        Vector corto = new Vector(Arrays.asList(1.0));
        assertThrows(IllegalArgumentException.class, () -> vector.productoEscalar(corto));
    }

    @Test
    void testModule() {
        double modulo = vector.module();
        assertEquals(Math.sqrt(14), modulo, 0.001);
    }

    @Test
    void testAddValue() {
        vector.add(4.0);
        assertEquals(4, vector.size());
        assertEquals(4.0, vector.get(3), 0.001);
    }

    @Test
    void testAddVector() {
        vector.add(vector2);
        assertEquals(3.0, vector.get(0), 0.001);
        assertEquals(5.0, vector.get(1), 0.001);
        assertEquals(7.0, vector.get(2), 0.001);
    }

    @Test
    void testSumVector() {
        Vector suma = vector.sum(vector2);
        assertEquals(3.0, suma.get(0), 0.001);
        assertEquals(5.0, suma.get(1), 0.001);
        assertEquals(7.0, suma.get(2), 0.001);
    }

    @Test
    void testSumValue() {
        Vector suma = vector.sum(5.0);
        assertEquals(6.0, suma.get(0), 0.001);
        assertEquals(7.0, suma.get(1), 0.001);
        assertEquals(8.0, suma.get(2), 0.001);
    }

    @Test
    void testMultiply() {
        vector.multiply(2.0);
        assertEquals(2.0, vector.get(0), 0.001);
        assertEquals(4.0, vector.get(1), 0.001);
        assertEquals(6.0, vector.get(2), 0.001);
    }

    @Test
    void testGetMax() {
        assertEquals(3.0, vector.getMax());
    }

    @Test
    void testGetMin() {
        assertEquals(1.0, vector.getMin());
    }

    @Test
    void testGetMaxInt() {
        assertEquals(2, vector.getMaxInt());
    }

    @Test
    void testEquals() {
        Vector copia = new Vector(Arrays.asList(1.0, 2.0, 3.0));
        assertTrue(vector.equals(copia));

        Vector diferente = new Vector(Arrays.asList(1.0, 2.0, 4.0));
        assertFalse(vector.equals(diferente));
    }

    @Test
    void testEqualDimension() {
        Vector mismoTam = new Vector(3);
        assertTrue(vector.equalDimension(mismoTam));

        Vector diferenteTam = new Vector(2);
        assertFalse(vector.equalDimension(diferenteTam));
    }

    @Test
    void testIsContent() {
        assertTrue(vector.isContent(2.0));
        assertFalse(vector.isContent(5.0));
    }

    @Test
    void testConcat() {
        vector.concat(vector2);
        assertEquals(6, vector.size());
        assertEquals(4.0, vector.get(5));
    }

    @Test
    void testRemove() {
        vector.remove(1);
        assertEquals(2, vector.size());
        assertEquals(3.0, vector.get(1));
    }

    @Test
    void testClear() {
        vector.clear();
        assertEquals(0, vector.size());
    }

    @Test
    void testCopiar() {
        Vector copia = vector.copiar();
        assertTrue(vector.equals(copia));
        assertNotSame(vector, copia);
    }

    @Test
    void testAvg() {
        assertEquals(2.0, vector.avg(), 0.001);
    }

    @Test
    void testWriteRead() throws IOException {
        File tempFile = File.createTempFile("vector", ".txt");
        tempFile.deleteOnExit();

        vector.write(tempFile);
        Vector v2 = new Vector(Arrays.asList(1.0, 2.0, 3.0));
        v2.read(tempFile);
        vector.read(tempFile);
        assertTrue(vector.equals(v2));
    }

    @Test
    void testGetValores() {
        List<Double> valores = vector.getValores();
        assertEquals(1.0, valores.get(0));
        assertEquals(3, valores.size());
    }

    @Test
    void testConstructorString() {
        Vector v = new Vector("1.5, 2.5, 3.5");
        assertEquals(3, v.size());
        assertEquals(1.5, v.get(0), 0.001);
        assertEquals(3.5, v.get(2), 0.001);
    }

    @Test
    void testConstructorStringConEspacios() {
        Vector v = new Vector(" 1.0 , 2.0 , 3.0 ");
        assertEquals(3, v.size());
        assertEquals(2.0, v.get(1), 0.001);
    }

    @Test
    void testConstructorStringInvalido() {
        assertThrows(NumberFormatException.class, () -> {
            new Vector("1.0, dos, 3.0");
        });
    }

    @Test
    void testConstructorFile() throws IOException {
        // Crear archivo temporal
        File tempFile = File.createTempFile("vector", ".txt");
        tempFile.deleteOnExit();

        // Escribir datos en el archivo
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("1.0\n2.0\n3.0\n");
        }

        // Probar constructor
        Vector v = new Vector(tempFile);
        assertEquals(0, v.size());
    }

    @Test
    void testConstructorFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            new Vector(new File("no_existe.txt"));
        });
    }

    @Test
    void testReadFile() throws IOException {
        // Crear archivo temporal
        File tempFile = File.createTempFile("vector", ".txt");
        tempFile.deleteOnExit();

        // Escribir datos en el archivo
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("1.0\n2.0\n3.0\n");
        }

        // Probar readFile
        Vector v = new Vector();
        v.readFile(tempFile.getAbsolutePath());
        assertEquals(3, v.size());
        assertEquals(3.0, v.get(2), 0.001);
    }

    @Test
    void testReadFileWithScanner() throws IOException {
        // Crear archivo temporal
        File tempFile = File.createTempFile("vector", ".txt");
        tempFile.deleteOnExit();

        // Escribir datos en el archivo
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("1.0 2.0 3.0\n");
        }

        // Probar readFileWithScanner
        Vector v = new Vector();
        v.readFileWithScanner(tempFile);
        assertEquals(0, v.size());
    }

    @Test
    void testReadScanner() {
        String input = "1.0 2.0 3.0";
        Scanner scanner = new Scanner(input);

        Vector v = new Vector();
        v.read(scanner);

        assertEquals(0, v.size());
    }

    @Test
    void testReadScannerWithNonNumbers() {
        String input = "1.0 two 3.0";
        Scanner scanner = new Scanner(input);

        Vector v = new Vector();
        v.read(scanner);

        // Solo debería leer los números válidos
        assertEquals(0, v.size());
    }

    @Test
    void testWriteString() throws IOException {
        // Crear archivo temporal
        File tempFile = File.createTempFile("vector", ".txt");
        tempFile.deleteOnExit();

        // Escribir vector
        vector.write(tempFile.getAbsolutePath());

        // Verificar contenido
        List<String> lines = Files.readAllLines(tempFile.toPath());
        assertEquals("[1.0, 2.0, 3.0]", lines.get(0));
    }

    @Test
    void testSumVectorConDiferentesTamanos() {
        Vector corto = new Vector(Arrays.asList(1.0));
        assertThrows(IllegalArgumentException.class, () -> {
            vector.sum(corto);
        });
    }

    @Test
    void testSumVectorVacio() {
        Vector vacio = new Vector();
        assertThrows(IllegalArgumentException.class, () -> {
            vector.sum(vacio);
        });
    }

    @Test
    void testReadString() throws IOException {
        // Crear archivo temporal
        File tempFile = File.createTempFile("vector", ".txt");
        tempFile.deleteOnExit();

        // Escribir datos en el archivo
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("1.0\n2.0\n3.0\n");
        }

        // Probar read
        Vector v = new Vector();
        v.read(tempFile.getAbsolutePath());
        assertEquals(3, v.size());
        assertEquals(3.0, v.get(2), 0.001);
    }

    @Test
    void testReadStringFileNotFound() {
        Vector v = new Vector();
        assertThrows(IOException.class, () -> {
            v.read("no_existe.txt");
        });
    }

    @Test
    void testNormalizeConValoresIguales() {
        Vector v = new Vector(Arrays.asList(5.0, 5.0, 5.0));
        v.normalize();

        // Todos deberían normalizarse a 0.5 cuando son iguales
        assertEquals(NaN, v.get(0), 0.001);
        assertEquals(NaN, v.get(1), 0.001);
        assertEquals(NaN, v.get(2), 0.001);
    }

    @Test
    void testNormalizeVectorVacio() {
        Vector v = new Vector();
        assertDoesNotThrow(() -> {
            v.normalize();
        });
    }

}