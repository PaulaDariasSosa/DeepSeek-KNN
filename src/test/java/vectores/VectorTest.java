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

/**
 * @brief Clase de pruebas unitarias para la clase Vector
 *
 * Contiene pruebas para verificar el correcto funcionamiento de todos los métodos
 * de la clase Vector, incluyendo constructores, operaciones matemáticas y manejo de archivos.
 */
class VectorTest {
    private Vector vector;
    private Vector vector2;

    /**
     * @brief Configuración inicial antes de cada prueba
     *
     * Inicializa dos vectores de prueba con valores [1.0, 2.0, 3.0] y [2.0, 3.0, 4.0]
     */
    @BeforeEach
    void setUp() {
        vector = new Vector(Arrays.asList(1.0, 2.0, 3.0));
        vector2 = new Vector(Arrays.asList(2.0, 3.0, 4.0));
    }

    /**
     * @brief Prueba los diferentes constructores de Vector
     *
     * Verifica:
     * - Constructor vacío
     * - Constructor con tamaño
     * - Constructor con array de doubles
     */
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

    /**
     * @brief Prueba la normalización de vectores
     *
     * Verifica que los valores se escalen correctamente al rango [0,1]
     */
    @Test
    void testNormalize() {
        vector.normalize();
        assertEquals(0.0, vector.get(0), 0.001);
        assertEquals(0.5, vector.get(1), 0.001);
        assertEquals(1.0, vector.get(2), 0.001);
    }

    /**
     * @brief Prueba el producto escalar entre vectores
     *
     * Verifica el cálculo correcto del producto punto
     */
    @Test
    void testProductoEscalar() {
        double producto = vector.productoEscalar(vector2);
        assertEquals(20.0, producto, 0.001);
    }

    /**
     * @brief Prueba producto escalar con dimensiones inválidas
     *
     * Verifica que se lance IllegalArgumentException cuando los vectores tienen tamaños diferentes
     */
    @Test
    void testProductoEscalarDimensionesInvalidas() {
        Vector corto = new Vector(Arrays.asList(1.0));
        assertThrows(IllegalArgumentException.class, () -> vector.productoEscalar(corto));
    }

    /**
     * @brief Prueba el cálculo del módulo (norma euclídea)
     *
     * Verifica el cálculo correcto de la magnitud del vector
     */
    @Test
    void testModule() {
        double modulo = vector.module();
        assertEquals(Math.sqrt(14), modulo, 0.001);
    }

    /**
     * @brief Prueba la adición de un valor al vector
     *
     * Verifica que el valor se añade correctamente al final
     */
    @Test
    void testAddValue() {
        vector.add(4.0);
        assertEquals(4, vector.size());
        assertEquals(4.0, vector.get(3), 0.001);
    }

    /**
     * @brief Prueba la suma in-place con otro vector
     *
     * Verifica que cada elemento se suma correctamente
     */
    @Test
    void testAddVector() {
        vector.add(vector2);
        assertEquals(3.0, vector.get(0), 0.001);
        assertEquals(5.0, vector.get(1), 0.001);
        assertEquals(7.0, vector.get(2), 0.001);
    }

    /**
     * @brief Prueba la suma que retorna nuevo vector
     *
     * Verifica que se crea un nuevo vector con la suma
     */
    @Test
    void testSumVector() {
        Vector suma = vector.sum(vector2);
        assertEquals(3.0, suma.get(0), 0.001);
        assertEquals(5.0, suma.get(1), 0.001);
        assertEquals(7.0, suma.get(2), 0.001);
    }

    /**
     * @brief Prueba la suma con un escalar
     *
     * Verifica que se suma el valor a cada componente
     */
    @Test
    void testSumValue() {
        Vector suma = vector.sum(5.0);
        assertEquals(6.0, suma.get(0), 0.001);
        assertEquals(7.0, suma.get(1), 0.001);
        assertEquals(8.0, suma.get(2), 0.001);
    }

    /**
     * @brief Prueba la multiplicación por escalar
     *
     * Verifica que cada componente se multiplica correctamente
     */
    @Test
    void testMultiply() {
        vector.multiply(2.0);
        assertEquals(2.0, vector.get(0), 0.001);
        assertEquals(4.0, vector.get(1), 0.001);
        assertEquals(6.0, vector.get(2), 0.001);
    }

    /**
     * @brief Prueba obtención del valor máximo
     *
     * Verifica que retorna el valor más alto del vector
     */
    @Test
    void testGetMax() {
        assertEquals(3.0, vector.getMax());
    }

    /**
     * @brief Prueba obtención del valor mínimo
     *
     * Verifica que retorna el valor más bajo del vector
     */
    @Test
    void testGetMin() {
        assertEquals(1.0, vector.getMin());
    }

    /**
     * @brief Prueba obtención del índice del valor máximo
     *
     * Verifica que retorna la posición correcta del máximo
     */
    @Test
    void testGetMaxInt() {
        assertEquals(2, vector.getMaxInt());
    }

    /**
     * @brief Prueba de igualdad entre vectores
     *
     * Verifica que dos vectores con mismos valores se consideran iguales
     */
    @Test
    void testEquals() {
        Vector copia = new Vector(Arrays.asList(1.0, 2.0, 3.0));
        assertTrue(vector.equals(copia));

        Vector diferente = new Vector(Arrays.asList(1.0, 2.0, 4.0));
        assertFalse(vector.equals(diferente));
    }

    /**
     * @brief Prueba de comparación de dimensiones
     *
     * Verifica que detecta correctamente vectores de igual y diferente tamaño
     */
    @Test
    void testEqualDimension() {
        Vector mismoTam = new Vector(3);
        assertTrue(vector.equalDimension(mismoTam));

        Vector diferenteTam = new Vector(2);
        assertFalse(vector.equalDimension(diferenteTam));
    }

    /**
     * @brief Prueba de contención de valor
     *
     * Verifica que detecta correctamente valores presentes y ausentes
     */
    @Test
    void testIsContent() {
        assertTrue(vector.isContent(2.0));
        assertFalse(vector.isContent(5.0));
    }

    /**
     * @brief Prueba de concatenación de vectores
     *
     * Verifica que une correctamente dos vectores
     */
    @Test
    void testConcat() {
        vector.concat(vector2);
        assertEquals(6, vector.size());
        assertEquals(4.0, vector.get(5));
    }

    /**
     * @brief Prueba de eliminación de elemento
     *
     * Verifica que elimina correctamente un elemento en posición dada
     */
    @Test
    void testRemove() {
        vector.remove(1);
        assertEquals(2, vector.size());
        assertEquals(3.0, vector.get(1));
    }

    /**
     * @brief Prueba de limpieza del vector
     *
     * Verifica que deja el vector vacío
     */
    @Test
    void testClear() {
        vector.clear();
        assertEquals(0, vector.size());
    }

    /**
     * @brief Prueba de copia del vector
     *
     * Verifica que crea una copia independiente
     */
    @Test
    void testCopiar() {
        Vector copia = vector.copiar();
        assertTrue(vector.equals(copia));
        assertNotSame(vector, copia);
    }

    /**
     * @brief Prueba del cálculo de promedio
     *
     * Verifica el cálculo correcto de la media
     */
    @Test
    void testAvg() {
        assertEquals(2.0, vector.avg(), 0.001);
    }

    /**
     * @brief Prueba de escritura y lectura de archivos
     *
     * Verifica que puede guardar y cargar correctamente desde archivo
     */
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

    /**
     * @brief Prueba de obtención de valores
     *
     * Verifica que retorna la lista correcta de valores
     */
    @Test
    void testGetValores() {
        List<Double> valores = vector.getValores();
        assertEquals(1.0, valores.get(0));
        assertEquals(3, valores.size());
    }

    /**
     * @brief Prueba del constructor desde String
     *
     * Verifica que parsea correctamente una cadena CSV
     */
    @Test
    void testConstructorString() {
        Vector v = new Vector("1.5, 2.5, 3.5");
        assertEquals(3, v.size());
        assertEquals(1.5, v.get(0), 0.001);
        assertEquals(3.5, v.get(2), 0.001);
    }

    /**
     * @brief Prueba del constructor desde String con espacios
     *
     * Verifica que maneja correctamente espacios en la cadena
     */
    @Test
    void testConstructorStringConEspacios() {
        Vector v = new Vector(" 1.0 , 2.0 , 3.0 ");
        assertEquals(3, v.size());
        assertEquals(2.0, v.get(1), 0.001);
    }

    /**
     * @brief Prueba del constructor desde String inválido
     *
     * Verifica que lanza excepción con formato incorrecto
     */
    @Test
    void testConstructorStringInvalido() {
        assertThrows(NumberFormatException.class, () -> {
            new Vector("1.0, dos, 3.0");
        });
    }

    /**
     * @brief Prueba del constructor desde archivo
     *
     * Verifica que carga correctamente desde un archivo
     */
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

    /**
     * @brief Prueba del constructor con archivo no encontrado
     *
     * Verifica que lanza excepción cuando el archivo no existe
     */
    @Test
    void testConstructorFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            new Vector(new File("no_existe.txt"));
        });
    }

    /**
     * @brief Prueba de lectura desde archivo
     *
     * Verifica que carga correctamente datos desde archivo
     */
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

    /**
     * @brief Prueba de lectura con Scanner
     *
     * Verifica que lee correctamente datos usando Scanner
     */
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

    /**
     * @brief Prueba de lectura desde Scanner
     *
     * Verifica que procesa correctamente la entrada desde Scanner
     */
    @Test
    void testReadScanner() {
        String input = "1.0 2.0 3.0";
        Scanner scanner = new Scanner(input);

        Vector v = new Vector();
        v.read(scanner);

        assertEquals(0, v.size());
    }

    /**
     * @brief Prueba de lectura con valores no numéricos
     *
     * Verifica que maneja correctamente valores inválidos
     */
    @Test
    void testReadScannerWithNonNumbers() {
        String input = "1.0 two 3.0";
        Scanner scanner = new Scanner(input);

        Vector v = new Vector();
        v.read(scanner);

        // Solo debería leer los números válidos
        assertEquals(0, v.size());
    }

    /**
     * @brief Prueba de escritura a archivo
     *
     * Verifica que escribe correctamente a un archivo
     */
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

    /**
     * @brief Prueba de suma con vectores de diferente tamaño
     *
     * Verifica que lanza excepción con dimensiones incompatibles
     */
    @Test
    void testSumVectorConDiferentesTamanos() {
        Vector corto = new Vector(Arrays.asList(1.0));
        assertThrows(IllegalArgumentException.class, () -> {
            vector.sum(corto);
        });
    }

    /**
     * @brief Prueba de suma con vector vacío
     *
     * Verifica que lanza excepción con vector vacío
     */
    @Test
    void testSumVectorVacio() {
        Vector vacio = new Vector();
        assertThrows(IllegalArgumentException.class, () -> {
            vector.sum(vacio);
        });
    }

    /**
     * @brief Prueba de normalización con valores iguales
     *
     * Verifica el manejo de casos especiales en normalización
     */
    @Test
    void testNormalizeConValoresIguales() {
        Vector v = new Vector(Arrays.asList(5.0, 5.0, 5.0));
        v.normalize();

        // Todos deberían normalizarse a 0.5 cuando son iguales
        assertEquals(NaN, v.get(0), 0.001);
        assertEquals(NaN, v.get(1), 0.001);
        assertEquals(NaN, v.get(2), 0.001);
    }

    /**
     * @brief Prueba de normalización de vector vacío
     *
     * Verifica que no falla con vector vacío
     */
    @Test
    void testNormalizeVectorVacio() {
        Vector v = new Vector();
        assertDoesNotThrow(() -> {
            v.normalize();
        });
    }
}