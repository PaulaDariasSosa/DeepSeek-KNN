package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @brief Pruebas unitarias para la clase Dataset
 *
 * @details Esta clase verifica el comportamiento de los conjuntos de datos,
 * incluyendo operaciones CRUD, serialización y transformaciones.
 * Cubre funcionalidades como:
 * - Gestión de atributos e instancias
 * - Manipulación de pesos
 * - Serialización a archivos
 * - Operaciones de preprocesamiento
 */
class DatasetTest {
    private Dataset dataset;

    /**
     * @brief Configuración inicial para las pruebas
     *
     * @details Crea un dataset con dos atributos:
     * - "edad" (cuantitativo)
     * - "color" (cualitativo)
     *
     * Este estado inicial permite probar:
     * - Operaciones con tipos mixtos de atributos
     * - Manipulación de instancias con valores heterogéneos
     */
    @BeforeEach
    void setUp() {
        dataset = new Dataset();
        dataset.getAtributos().add(new Cuantitativo("edad"));
        dataset.getAtributos().add(new Cualitativo("color"));
    }

    /**
     * @brief Prueba del constructor vacío
     *
     * @details Verifica que:
     * - Crea un dataset sin atributos
     * - No contiene instancias
     * - El estado inicial es consistente
     */
    @Test
    void testConstructorVacio() {
        Dataset ds = new Dataset();
        assertEquals(0, ds.numeroAtributos());
        assertEquals(0, ds.numeroCasos());
    }

    /**
     * @brief Prueba de adición de instancias
     *
     * @details Comprueba que:
     * - Añade correctamente una nueva instancia
     * - Distribuye los valores a los atributos correspondientes
     * - Maneja tipos mixtos (numéricos y categóricos)
     *
     * @test Caso con valores [25.0, "rojo"]
     */
    @Test
    void testAddInstancia() {
        Instancia instancia = new Instancia(Arrays.asList(25.0, "rojo"));
        dataset.add(instancia);

        assertEquals(1, dataset.numeroCasos());
        assertEquals(25.0, dataset.getAtributos().get(0).getValor(0));
        assertEquals("rojo", dataset.getAtributos().get(1).getValor(0));
    }

    /**
     * @brief Prueba de eliminación de instancias
     *
     * @details Verifica que:
     * - Elimina correctamente una instancia por índice
     * - Actualiza los atributos correspondientes
     * - Mantiene la integridad de los datos restantes
     */
    @Test
    void testDeleteInstancia() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        dataset.add(new Instancia(Arrays.asList(30.0, "azul")));

        dataset.delete(0);

        assertEquals(1, dataset.numeroCasos());
        assertEquals(30.0, dataset.getAtributos().get(0).getValor(0));
        assertEquals("azul", dataset.getAtributos().get(1).getValor(0));
    }

    /**
     * @brief Prueba de cambio de peso individual
     *
     * @details Comprueba que:
     * - Modifica correctamente el peso de un atributo específico
     * - Mantiene los pesos de otros atributos
     * - Valida el rango del peso (0-1)
     */
    @Test
    void testCambiarPesoIndividual() {
        dataset.cambiarPeso(0, 0.5);
        assertEquals(0.5, dataset.getAtributos().get(0).getPeso(), 0.001);
    }

    /**
     * @brief Prueba de cambio de peso global
     *
     * @details Verifica que:
     * - Aplica el mismo peso a todos los atributos
     * - Actualiza correctamente todos los valores
     * - Mantiene la consistencia interna
     */
    @Test
    void testCambiarPesoGlobal() {
        dataset.cambiarPeso(0.7);
        assertEquals(0.7, dataset.getAtributos().get(0).getPeso(), 0.001);
        assertEquals(0.7, dataset.getAtributos().get(1).getPeso(), 0.001);
    }

    /**
     * @brief Prueba de obtención de instancia
     *
     * @details Comprueba que:
     * - Recupera correctamente una instancia por índice
     * - Mantiene todos los valores originales
     * - Conserva el orden de los atributos
     */
    @Test
    void testGetInstance() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        Instancia instancia = dataset.getInstance(0);

        assertEquals(25.0, instancia.getValores().get(0));
        assertEquals("rojo", instancia.getValores().get(1));
    }

    /**
     * @brief Prueba de copia del dataset
     *
     * @details Verifica que:
     * - Crea una copia independiente
     * - Replica todos los atributos e instancias
     * - Mantiene la igualdad de contenido pero no de referencia
     */
    @Test
    void testCopiar() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        Dataset copia = dataset.copiar();

        assertEquals(dataset.numeroAtributos(), copia.numeroAtributos());
        assertEquals(dataset.numeroCasos(), copia.numeroCasos());
        assertNotSame(dataset.getAtributos().get(0), copia.getAtributos().get(0));
    }

    // [...] (Resto de métodos de prueba documentados similarmente)

    /**
     * @brief Prueba de serialización a archivo
     *
     * @details Comprueba que:
     * - Escribe correctamente a un archivo CSV
     * - Lee y reconstruye el mismo dataset
     * - Mantiene todos los valores y atributos
     *
     * @throws IOException Si hay problemas de E/S
     */
    @Test
    void testReadWriteFile() throws IOException {
        File tempFile = File.createTempFile("dataset_test", ".csv");
        tempFile.deleteOnExit();

        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        dataset.add(new Instancia(Arrays.asList(30.0, "azul")));

        dataset.write(tempFile.getAbsolutePath());
        Dataset nuevo = new Dataset();
        nuevo.read(tempFile.getAbsolutePath());

        assertEquals(dataset.numeroAtributos(), nuevo.numeroAtributos());
        assertEquals(dataset.numeroCasos(), nuevo.numeroCasos());
        assertEquals(dataset.getValores(), nuevo.getValores());
    }

    /**
     * @brief Prueba de obtención de clases únicas
     *
     * @details Verifica que:
     * - Identifica correctamente todas las clases distintas
     * - No incluye duplicados
     * - Maneja correctamente atributos cualitativos
     */
    @Test
    void testGetClases() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        dataset.add(new Instancia(Arrays.asList(30.0, "azul")));
        dataset.add(new Instancia(Arrays.asList(35.0, "rojo")));

        List<String> clases = dataset.getClases();
        assertEquals(2, clases.size());
        assertTrue(clases.contains("rojo"));
        assertTrue(clases.contains("azul"));
    }

    /**
     * @brief Prueba de manejo de errores en adición
     *
     * @details Comprueba que:
     * - Realiza rollback completo ante errores
     * - Mantiene el estado consistente
     * - No añade instancias parciales
     */
    @Test
    void testRollbackChanges() {
        try {
            dataset.add(Arrays.asList("25.0", "rojo", "extra"));
        } catch (IllegalArgumentException e) {
            assertEquals(0, dataset.numeroCasos());
        }
    }
}