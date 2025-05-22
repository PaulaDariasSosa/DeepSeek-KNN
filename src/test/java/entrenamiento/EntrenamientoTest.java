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

class EntrenamientoTest {
    private Dataset datos;
    private Entrenamiento entrenamiento;

    @BeforeEach
    void setUp() {
        datos = new Dataset();
        // Configurar dataset de prueba
        datos.getAtributos().add(new Cuantitativo("edad"));
        datos.getAtributos().add(new Cualitativo("clase"));

        // Añadir instancias de prueba
        datos.add(List.of("25", "A"));
        datos.add(List.of("30", "B"));
        datos.add(List.of("22", "A"));
        datos.add(List.of("40", "B"));

        entrenamiento = new Entrenamiento(datos, 0.75);
    }

    @Test
    void testDivisionConjuntos() {
        Dataset train = entrenamiento.getTrainDataset();
        Dataset test = entrenamiento.getTestDataset();

        // Verificar proporción 75/25 (3 instancias train, 1 test)
        assertEquals(3, train.numeroCasos());
        assertEquals(1, test.numeroCasos());
    }

    @Test
    void testGetClases() {
        List<String> clases = entrenamiento.getClases();
        assertTrue(clases.contains("A") && clases.contains("B"));
    }

    @Test
    void testConstructorVacio() {
        Entrenamiento e = new Entrenamiento();
        assertNull(e.getTrainDataset());
        assertNull(e.getTestDataset());
        assertNull(e.getClases());
    }

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

        // Con misma semilla deberían ser iguales
        assertEquals(e1.getTrainDataset().getValores(),
                e2.getTrainDataset().getValores());
    }

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

        // Verificar que se creó el archivo
        assertTrue(new File("test_output.csv").exists());
        new File("test_output.csv").delete(); // Limpiar
    }

    @Test
    void testGenerarPrediccionConError() {
        Entrenamiento e = new Entrenamiento();
        assertThrows(NullPointerException.class, () -> {
            e.generarPrediccion(1, "test_output.csv");
        });
    }

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

    @Test
    void testGenerarMatrizSinDatos() {
        Entrenamiento e = new Entrenamiento();
        assertThrows(NullPointerException.class, () -> {
            e.generarMatriz(1);
        });
    }

    @Test
    void testReadWrite() throws IOException {
        // Preparar datos
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("attr1"));
        datos.getAtributos().add(new Cualitativo("clase"));
        datos.add(List.of("1.0", "A"));
        datos.add(List.of("2.0", "B"));

        Entrenamiento original = new Entrenamiento(datos, 0.5);

        // Escribir y leer
        original.write("train_test.csv", "test_test.csv");
        Entrenamiento leido = new Entrenamiento();
        leido.read("train_test.csv", "test_test.csv");

        // Verificar
        assertEquals(original.getTrainDataset().numeroCasos(),
                leido.getTrainDataset().numeroCasos());
        assertEquals(original.getTestDataset().numeroCasos(),
                leido.getTestDataset().numeroCasos());

        // Limpiar
        new File("train_test.csv").delete();
        new File("test_test.csv").delete();
    }

    @Test
    void testReadConArchivosInvalidos() {
        Entrenamiento e = new Entrenamiento();
        assertThrows(IOException.class, () -> {
            e.read("no_existe1.csv", "no_existe2.csv");
        });
    }

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

        // Verificar contenido
        List<String> lineas = Files.readAllLines(Paths.get("resultados_test.csv"));
        assertTrue(lineas.get(0).contains("Clase Real"));
        assertTrue(lineas.size() > 1);

        // Limpiar
        new File("resultados_test.csv").delete();
    }

    @Test
    void testExportarResultadosSinTest() {
        Entrenamiento e = new Entrenamiento();
        assertThrows(IllegalStateException.class, () -> {
            e.exportarResultados("resultados.csv", 1);
        });
    }

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

    @Test
    void testDatasetVacio() {
        Dataset datos = new Dataset();
        assertThrows(IndexOutOfBoundsException.class, () -> {
            new Entrenamiento(datos, 0.5);
        });
    }

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