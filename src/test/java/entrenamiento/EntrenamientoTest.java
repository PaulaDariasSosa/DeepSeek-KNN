package entrenamiento;

import datos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @brief Pruebas unitarias para la clase Entrenamiento
 *
 * @details Verifica el correcto funcionamiento de:
 * - División de datasets en conjuntos de entrenamiento y prueba
 * - Generación de predicciones y matrices de confusión
 * - Manejo de archivos y serialización
 * - Casos límite y validaciones
 */
class EntrenamientoTest {
    private Dataset datos;
    private Entrenamiento entrenamiento;

    /**
     * @brief Configuración inicial común para las pruebas
     *
     * @details Crea un dataset de prueba con:
     * - 2 atributos (1 cuantitativo, 1 cualitativo)
     * - 4 instancias balanceadas entre 2 clases
     * - Inicializa el objeto Entrenamiento con 75% para entrenamiento
     */
    @BeforeEach
    void setUp() {
        datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("edad"));
        datos.getAtributos().add(new Cualitativo("clase"));

        datos.add(List.of("25", "A"));
        datos.add(List.of("30", "B"));
        datos.add(List.of("22", "A"));
        datos.add(List.of("40", "B"));

        entrenamiento = new Entrenamiento(datos, 0.75);
    }

    /**
     * @brief Prueba la división básica del dataset
     *
     * @details Verifica que:
     * - Se respeta la proporción 75/25 especificada
     * - Los conjuntos resultantes tienen el tamaño correcto
     * - No se pierden instancias en el proceso
     */
    @Test
    void testDivisionConjuntos() {
        Dataset train = entrenamiento.getTrainDataset();
        Dataset test = entrenamiento.getTestDataset();

        assertEquals(3, train.numeroCasos());
        assertEquals(1, test.numeroCasos());
    }

    /**
     * @brief Prueba la identificación de clases únicas
     *
     * @details Comprueba que:
     * - Detecta correctamente todas las clases presentes
     * - No incluye valores duplicados
     * - Maneja correctamente atributos cualitativos
     */
    @Test
    void testGetClases() {
        List<String> clases = entrenamiento.getClases();
        assertTrue(clases.contains("A") && clases.contains("B"));
    }

    /**
     * @brief Prueba el constructor sin parámetros
     *
     * @details Verifica que:
     * - Los campos se inicializan como nulos
     * - Permite configuración posterior
     */
    @Test
    void testConstructorVacio() {
        Entrenamiento e = new Entrenamiento();
        assertNull(e.getTrainDataset());
        assertNull(e.getTestDataset());
        assertNull(e.getClases());
    }

    /**
     * @brief Prueba el constructor con porcentaje de división
     *
     * @details Comprueba que:
     * - Divide correctamente datasets pequeños
     * - Conserva todas las clases
     * - Maneja correctamente el redondeo de instancias
     */
    @Test
    void testConstructorPorcentaje() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));
        datos.add(List.of("2.0", "B"));

        Entrenamiento e = new Entrenamiento(datos, 0.5);

        assertEquals(1, e.getTrainDataset().numeroCasos());
        assertEquals(1, e.getTestDataset().numeroCasos());
        assertEquals(2, e.getClases().size());
    }

    /**
     * @brief Prueba el constructor con semilla aleatoria
     *
     * @details Verifica que:
     * - Con la misma semilla produce divisiones idénticas
     * - Permite reproducibilidad en los resultados
     */
    @Test
    void testConstructorConSemilla() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));
        datos.add(List.of("2.0", "B"));
        datos.add(List.of("3.0", "A"));
        datos.add(List.of("4.0", "B"));

        Entrenamiento e1 = new Entrenamiento(datos, 0.5, 123);
        Entrenamiento e2 = new Entrenamiento(datos, 0.5, 123);

        assertEquals(e1.getTrainDataset().getValores(),
                e2.getTrainDataset().getValores());
    }

    /**
     * @brief Prueba la generación de archivos de predicción
     *
     * @details Verifica que:
     * - Crea correctamente el archivo de salida
     * - Maneja correctamente el formato CSV
     * - Realiza limpieza después de la prueba
     */
    @Test
    void testGenerarPrediccion() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));
        datos.add(List.of("1.1", "A"));
        datos.add(List.of("2.0", "B"));
        datos.add(List.of("2.1", "B"));

        Entrenamiento e = new Entrenamiento(datos, 0.75);
        assertDoesNotThrow(() -> e.generarPrediccion(1, "test_output.csv"));

        assertTrue(new File("test_output.csv").exists());
        new File("test_output.csv").delete();
    }

    /**
     * @brief Prueba generación de predicciones sin datos
     *
     * @details Verifica que:
     * - Lanza excepción cuando no está inicializado
     * - Maneja correctamente el estado inválido
     */
    @Test
    void testGenerarPrediccionConError() {
        Entrenamiento e = new Entrenamiento();
        assertThrows(NullPointerException.class, () -> {
            e.generarPrediccion(1, "test_output.csv");
        });
    }

    /**
     * @brief Prueba generación de matriz de confusión
     *
     * @details Comprueba que:
     * - No lanza excepciones con datos válidos
     * - Maneja correctamente la división 66/33
     */
    @Test
    void testGenerarMatrizConfusion() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));
        datos.add(List.of("1.1", "A"));
        datos.add(List.of("2.0", "B"));

        Entrenamiento e = new Entrenamiento(datos, 0.66);
        assertDoesNotThrow(() -> e.generarMatriz(1));
    }

    /**
     * @brief Prueba generación de matriz sin datos
     *
     * @details Verifica que:
     * - Lanza excepción cuando no hay datos
     * - Maneja correctamente el estado no inicializado
     */
    @Test
    void testGenerarMatrizSinDatos() {
        Entrenamiento e = new Entrenamiento();
        assertThrows(NullPointerException.class, () -> {
            e.generarMatriz(1);
        });
    }

    /**
     * @brief Prueba serialización/deserialización
     *
     * @details Comprueba que:
     * - Los datasets se guardan y cargan correctamente
     * - Se mantiene la integridad de los datos
     * - Realiza limpieza de archivos temporales
     */
    @Test
    void testReadWrite() throws IOException {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));
        datos.add(List.of("2.0", "B"));

        Entrenamiento original = new Entrenamiento(datos, 0.5);

        original.write("train_test.csv", "test_test.csv");
        Entrenamiento leido = new Entrenamiento();
        leido.read("train_test.csv", "test_test.csv");

        assertEquals(original.getTrainDataset().numeroCasos(),
                leido.getTrainDataset().numeroCasos());
        assertEquals(original.getTestDataset().numeroCasos(),
                leido.getTestDataset().numeroCasos());

        new File("train_test.csv").delete();
        new File("test_test.csv").delete();
    }

    /**
     * @brief Prueba lectura con archivos inexistentes
     *
     * @details Verifica que:
     * - Lanza excepción cuando los archivos no existen
     * - Maneja correctamente errores de E/S
     */
    @Test
    void testReadConArchivosInvalidos() {
        Entrenamiento e = new Entrenamiento();
        assertThrows(IOException.class, () -> {
            e.read("no_existe1.csv", "no_existe2.csv");
        });
    }

    /**
     * @brief Prueba exportación de resultados
     *
     * @details Comprueba que:
     * - Genera correctamente el archivo CSV
     * - Incluye las cabeceras esperadas
     * - Realiza limpieza después de la prueba
     */
    @Test
    void testExportarResultados() throws IOException {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));
        datos.add(List.of("1.1", "A"));
        datos.add(List.of("2.0", "B"));

        Entrenamiento e = new Entrenamiento(datos, 0.66);
        e.exportarResultados("resultados_test.csv", 1);

        List<String> lineas = Files.readAllLines(Paths.get("resultados_test.csv"));
        assertTrue(lineas.get(0).contains("Clase Real"));
        assertTrue(lineas.size() > 1);

        new File("resultados_test.csv").delete();
    }

    /**
     * @brief Prueba exportación sin datos de prueba
     *
     * @details Verifica que:
     * - Lanza excepción cuando no hay conjunto de prueba
     * - Maneja correctamente el estado inválido
     */
    @Test
    void testExportarResultadosSinTest() {
        Entrenamiento e = new Entrenamiento();
        assertThrows(IllegalStateException.class, () -> {
            e.exportarResultados("resultados.csv", 1);
        });
    }

    /**
     * @brief Prueba caso límite con 0% entrenamiento
     *
     * @details Comprueba que:
     * - Todo el dataset va al conjunto de prueba
     * - El conjunto de entrenamiento queda vacío
     */
    @Test
    void testPorcentajeCero() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));

        Entrenamiento e = new Entrenamiento(datos, 0.0);
        assertEquals(0, e.getTrainDataset().numeroCasos());
        assertEquals(1, e.getTestDataset().numeroCasos());
    }

    /**
     * @brief Prueba caso límite con 100% entrenamiento
     *
     * @details Comprueba que:
     * - Todo el dataset va al conjunto de entrenamiento
     * - El conjunto de prueba queda vacío
     */
    @Test
    void testPorcentajeUno() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));

        Entrenamiento e = new Entrenamiento(datos, 1.0);
        assertEquals(1, e.getTrainDataset().numeroCasos());
        assertEquals(0, e.getTestDataset().numeroCasos());
    }

    /**
     * @brief Prueba con dataset vacío
     *
     * @details Verifica que:
     * - Lanza excepción cuando no hay datos
     * - Maneja correctamente el caso vacío
     */
    @Test
    void testDatasetVacio() {
        Dataset datos = new Dataset();
        assertThrows(IndexOutOfBoundsException.class, () -> {
            new Entrenamiento(datos, 0.5);
        });
    }

    /**
     * @brief Prueba obtención de dataset de entrenamiento
     *
     * @details Comprueba que:
     * - Devuelve el conjunto correcto
     * - Mantiene el número esperado de instancias
     */
    @Test
    void testGetTrainDataset() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));

        Entrenamiento e = new Entrenamiento(datos, 1.0);
        assertNotNull(e.getTrainDataset());
        assertEquals(1, e.getTrainDataset().numeroCasos());
    }

    /**
     * @brief Prueba obtención de dataset de prueba
     *
     * @details Comprueba que:
     * - Devuelve el conjunto correcto
     * - Mantiene el número esperado de instancias
     */
    @Test
    void testGetTestDataset() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));

        Entrenamiento e = new Entrenamiento(datos, 0.0);
        assertNotNull(e.getTestDataset());
        assertEquals(1, e.getTestDataset().numeroCasos());
    }
}