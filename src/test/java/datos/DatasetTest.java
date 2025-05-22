package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class DatasetTest {
    private Dataset dataset;

    @BeforeEach
    void setUp() {
        dataset = new Dataset();
        dataset.getAtributos().add(new Cuantitativo("edad"));
        dataset.getAtributos().add(new Cualitativo("color"));
    }

    @Test
    void testConstructorVacio() {
        Dataset ds = new Dataset();
        assertEquals(0, ds.numeroAtributos());
        assertEquals(0, ds.numeroCasos());
    }

    @Test
    void testAddInstancia() {
        Instancia instancia = new Instancia(Arrays.asList(25.0, "rojo"));
        dataset.add(instancia);

        assertEquals(1, dataset.numeroCasos());
        assertEquals(25.0, dataset.getAtributos().get(0).getValor(0));
        assertEquals("rojo", dataset.getAtributos().get(1).getValor(0));
    }

    @Test
    void testDeleteInstancia() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        dataset.add(new Instancia(Arrays.asList(30.0, "azul")));

        dataset.delete(0);

        assertEquals(1, dataset.numeroCasos());
        assertEquals(30.0, dataset.getAtributos().get(0).getValor(0));
        assertEquals("azul", dataset.getAtributos().get(1).getValor(0));
    }

    @Test
    void testCambiarPesoIndividual() {
        dataset.cambiarPeso(0, 0.5);
        assertEquals(0.5, dataset.getAtributos().get(0).getPeso(), 0.001);
    }

    @Test
    void testCambiarPesoGlobal() {
        dataset.cambiarPeso(0.7);
        assertEquals(0.7, dataset.getAtributos().get(0).getPeso(), 0.001);
        assertEquals(0.7, dataset.getAtributos().get(1).getPeso(), 0.001);
    }

    @Test
    void testGetInstance() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        Instancia instancia = dataset.getInstance(0);

        assertEquals(25.0, instancia.getValores().get(0));
        assertEquals("rojo", instancia.getValores().get(1));
    }

    @Test
    void testCopiar() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        Dataset copia = dataset.copiar();

        assertEquals(dataset.numeroAtributos(), copia.numeroAtributos());
        assertEquals(dataset.numeroCasos(), copia.numeroCasos());
        assertNotSame(dataset.getAtributos().get(0), copia.getAtributos().get(0));
    }

    @Test
    void testConstructorConListaAtributos() {
        List<Atributo> atributos = Arrays.asList(
                new Cuantitativo("edad"),
                new Cualitativo("color")
        );
        Dataset ds = new Dataset(atributos);

        assertEquals(2, ds.numeroAtributos());
        assertEquals(0, ds.numeroCasos());
    }

    @Test
    void testConstructorCopia() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        Dataset copia = new Dataset(dataset);

        assertEquals(dataset.numeroAtributos(), copia.numeroAtributos());
        assertEquals(dataset.numeroCasos(), copia.numeroCasos());
        assertNotSame(dataset.getAtributos(), copia.getAtributos());
    }

    @Test
    void testAddInstanciaInvalida() {
        // Tamaño incorrecto
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dataset.add(new Instancia(Arrays.asList(25.0))); // Falta el color
        });

        // Instancia nula
        assertThrows(NullPointerException.class, () -> {
            dataset.add((Instancia) null);
        });
    }

    @Test
    void testDeleteInstanciaInvalida() {
        // Dataset vacío
        assertThrows(Dataset.DatasetOperationException.class, () -> {
            new Dataset().delete(0);
        });

        // Índice negativo
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        assertThrows(Dataset.DatasetOperationException.class, () -> {
            dataset.delete(-1);
        });

        // Índice fuera de rango
        assertThrows(Dataset.DatasetOperationException.class, () -> {
            dataset.delete(1);
        });
    }

    @Test
    void testCambiarPesoInvalido() {
        // Peso fuera de rango
        assertThrows(IllegalArgumentException.class, () -> {
            dataset.cambiarPeso(1.5); // > 1
        });

        // Lista de pesos incorrecta
        assertThrows(IllegalArgumentException.class, () -> {
            dataset.cambiarPeso(Arrays.asList("0.5")); // Solo 1 peso para 2 atributos
        });

        // Peso no numérico
        assertThrows(IllegalArgumentException.class, () -> {
            dataset.cambiarPeso(Arrays.asList("0.5", "no_numérico"));
        });
    }

    @Test
    void testReadWriteFile() throws IOException {
        // Preparar archivo temporal
        File tempFile = File.createTempFile("dataset_test", ".csv");
        tempFile.deleteOnExit();

        // Datos de prueba
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        dataset.add(new Instancia(Arrays.asList(30.0, "azul")));

        // Escribir y leer
        dataset.write(tempFile.getAbsolutePath());
        Dataset nuevo = new Dataset();
        nuevo.read(tempFile.getAbsolutePath());

        // Verificar
        assertEquals(dataset.numeroAtributos(), nuevo.numeroAtributos());
        assertEquals(dataset.numeroCasos(), nuevo.numeroCasos());
        assertEquals(dataset.getValores(), nuevo.getValores());
    }

    @Test
    void testReadFileInvalido() {
        assertThrows(IOException.class, () -> {
            new Dataset().read("archivo_inexistente.csv");
        });
    }

    @Test
    void testNombreAtributos() {
        List<String> nombres = dataset.nombreAtributos();
        assertEquals(2, nombres.size());
        assertEquals("edad", nombres.get(0));
        assertEquals("color", nombres.get(1));
    }

    @Test
    void testGetValores() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        dataset.add(new Instancia(Arrays.asList(30.0, "azul")));

        List<String> valores = dataset.getValores();
        assertEquals(4, valores.size()); // 2 instancias x 2 atributos
        assertTrue(valores.contains("25.0"));
        assertTrue(valores.contains("azul"));
    }

    @Test
    void testGetAtributosEmpty() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        List<Atributo> vacios = dataset.getAtributosEmpty();

        assertEquals(2, vacios.size());
        assertEquals(0, vacios.get(0).size()); // Deben estar vacíos
        assertEquals(0, vacios.get(1).size());
    }

    @Test
    void testGetClases() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        dataset.add(new Instancia(Arrays.asList(30.0, "azul")));
        dataset.add(new Instancia(Arrays.asList(35.0, "rojo")));

        List<String> clases = dataset.getClases();
        assertEquals(2, clases.size()); // Debería tener 2 clases únicas
        assertTrue(clases.contains("rojo"));
        assertTrue(clases.contains("azul"));
    }

    @Test
    void testPreprocesado() {
        assertEquals(0, dataset.getPreprocesado()); // Valor por defecto

        dataset.setPreprocesado(2);
        assertEquals(2, dataset.getPreprocesado());
    }

    @Test
    void testToString() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        String str = dataset.toString();

        assertTrue(str.contains("edad"));
        assertTrue(str.contains("color"));
        assertTrue(str.contains("25.0"));
        assertTrue(str.contains("rojo"));
    }

    @Test
    void testPrint() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        // No hay forma directa de verificar el logging, pero podemos llamar al método
        assertDoesNotThrow(() -> dataset.print());
    }

    @Test
    void testAddListaStrings() {
        List<String> valores = Arrays.asList("25.0", "rojo");
        dataset.add(valores);

        assertEquals(1, dataset.numeroCasos());
        assertEquals(25.0, dataset.getAtributos().get(0).getValor(0));
        assertEquals("rojo", dataset.getAtributos().get(1).getValor(0));
    }

    @Test
    void testAddListaStringsInvalida() {
        // Lista nula
        assertThrows(IllegalArgumentException.class, () -> {
            dataset.add((List<String>) null);
        });

        // Lista vacía
        assertThrows(IllegalArgumentException.class, () -> {
            dataset.add(Collections.emptyList());
        });

        // Tamaño incorrecto
        assertThrows(IllegalArgumentException.class, () -> {
            dataset.add(Arrays.asList("25.0")); // Falta el color
        });

        // Valor no numérico para atributo cuantitativo
        assertThrows(IllegalArgumentException.class, () -> {
            dataset.add(Arrays.asList("no_numérico", "rojo"));
        });
    }

    @Test
    void testRollbackChanges() {
        // Añadir instancia con error en segundo atributo
        try {
            dataset.add(Arrays.asList("25.0", "rojo", "extra")); // Demasiados valores
        } catch (IllegalArgumentException e) {
            // Verificar que se hizo rollback
            assertEquals(0, dataset.numeroCasos());
        }
    }

    @Test
    void testGetByIndex() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        Atributo atributo = dataset.get(0);

        assertEquals("edad", atributo.getNombre());
        assertEquals(1, atributo.size());
    }

    @Test
    void testGetByIndexInvalido() {
        // Índice negativo
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dataset.get(-1);
        });

        // Índice fuera de rango
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dataset.get(2); // Solo hay 2 atributos
        });
    }

    @Test
    void testGetInstanceComplete() {
        dataset.add(new Instancia(Arrays.asList(25.0, "rojo")));
        dataset.add(new Instancia(Arrays.asList(30.0, "azul")));

        Instancia instancia = dataset.getInstance(1);
        assertEquals(30.0, instancia.getValores().get(0));
        assertEquals("azul", instancia.getValores().get(1));
    }

    @Test
    void testGetInstanceInvalido() {
        // Índice negativo
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dataset.getInstance(-1);
        });

        // Índice fuera de rango
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dataset.getInstance(2); // Solo hay 2 instancias
        });
    }

    @Test
    void testGetPesos() {
        dataset.cambiarPeso(0.5);
        List<String> pesos = dataset.getPesos();

        assertEquals("edad: 0.5", pesos.get(0));
        assertEquals("color: 0.5", pesos.get(1));
    }

    @Test
    void testGetPesosInvalido() {
        // Cambiar peso a un índice inválido
        assertThrows(IndexOutOfBoundsException.class, () -> {
            dataset.cambiarPeso(2, 0.5); // Índice fuera de rango
        });
    }
}