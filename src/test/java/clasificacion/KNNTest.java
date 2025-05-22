package clasificacion;

import datos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import vectores.Vector;

import java.util.Arrays;
import java.util.List;

class KNNTest {
    private Dataset dataset;
    private KNN knn;

    @BeforeEach
    void setUp() {
        // Configurar dataset de forma compatible con la implementación actual
        dataset = new Dataset();

        // Primero crear y configurar los atributos adecuadamente
        Cuantitativo attr1 = new Cuantitativo("attr1");
        Cuantitativo attr2 = new Cuantitativo("attr2");
        Cualitativo clase = new Cualitativo("clase");

        // Añadir atributos al dataset
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);
        dataset.getAtributos().add(clase);

        // Añadir instancias con valores consistentes
        dataset.add(new Instancia(Arrays.asList(1.0, 1.0, "A")));
        dataset.add(new Instancia(Arrays.asList(2.0, 2.0, "A")));
        dataset.add(new Instancia(Arrays.asList(8.0, 8.0, "B")));

        knn = new KNN(2);
    }

    @Test
    void testClasificar() {
        // La instancia de prueba debe tener valores para todos los atributos + clase (null)
        Instancia prueba = new Instancia(Arrays.asList(1.5, 1.5));

        // Verificar que el dataset tiene casos
        assertTrue(dataset.numeroCasos() > 0, "El dataset no debe estar vacío");

        String resultado = knn.clasificar(dataset, prueba);
        assertEquals("A", resultado);
    }

    @Test
    void testGetDistancias() {
        // Verificar que el dataset tiene casos
        assertTrue(dataset.numeroCasos() > 0, "El dataset no debe estar vacío");

        Instancia prueba = new Instancia(Arrays.asList(1.5, 1.5));
        Vector distancias = knn.getDistancias(dataset, prueba);

        assertEquals(3, distancias.size(), "Debería haber una distancia por cada instancia en el dataset");
        assertTrue(distancias.get(0) < distancias.get(2), "La primera instancia debería estar más cerca");
    }

    @Test
    void testGetDistanciaEuclidea() {
        Vector v1 = new Vector(Arrays.asList(1.0, 2.0));
        Vector v2 = new Vector(Arrays.asList(4.0, 6.0));

        double distancia = knn.getDistanciaEuclidea(v1, v2);
        assertEquals(5.0, distancia, 0.001);
    }

    @Test
    void testGetDistanciaEuclideaPonderada() {
        // Preparar vectores según lo que espera la implementación actual
        Vector v1 = new Vector(Arrays.asList(1.0, 2.0, 0.0)); // Incluye clase como último elemento
        Vector v2 = new Vector(Arrays.asList(4.0, 6.0));      // Solo atributos
        double[] pesos = {0.5, 1.0};                          // Peso por atributo

        double distancia = knn.getDistanciaEuclidea(v1, v2, pesos);
        assertEquals(Math.sqrt(2.25 + 16), distancia, 0.001); // √((1-4)²×0.5 + (2-6)²×1)
    }

    @Test
    void testGetClase() {
        List<Instancia> instancias = Arrays.asList(
                new Instancia(Arrays.asList(1.0, 1.0, "A")),
                new Instancia(Arrays.asList(2.0, 2.0, "A")),
                new Instancia(Arrays.asList(3.0, 3.0, "B"))
        );

        assertEquals("A", knn.getClase(instancias));
    }

    @Test
    void testGetVecino() {
        List<Instancia> instancias = Arrays.asList(
                new Instancia(Arrays.asList(1.0, 1.0, "A")),
                new Instancia(Arrays.asList(2.0, 2.0, "A")),
                new Instancia(Arrays.asList(8.0, 8.0, "B"))
        );

        Vector distancias = new Vector(Arrays.asList(1.0, 2.0, 8.0));
        assertEquals("A", knn.getVecino(instancias, distancias));
    }
}