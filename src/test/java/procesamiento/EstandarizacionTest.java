package procesamiento;

import datos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * @brief Pruebas unitarias para la clase Estandarizacion
 *
 * @details Esta clase verifica el correcto funcionamiento del procesador de estandarización,
 * que transforma los atributos numéricos a una distribución con media 0 y desviación estándar 1.
 * Se prueban diversos casos incluyendo datasets con:
 * - Valores numéricos típicos
 * - Atributos cualitativos (deben ignorarse)
 * - Casos especiales (dataset vacío, un solo valor)
 */
class EstandarizacionTest {
    private Dataset dataset;

    /**
     * @brief Configuración inicial para las pruebas
     *
     * @details Crea un dataset de prueba con un atributo cuantitativo "edad"
     * con valores [10.0, 20.0, 30.0] para probar la estandarización básica.
     * La configuración inicial tiene:
     * - Media (μ) = 20
     * - Desviación estándar (σ) ≈ 8.164966
     */
    @BeforeEach
    void setUp() {
        dataset = new Dataset();
        Cuantitativo edad = new Cuantitativo("edad");
        edad.add(10.0);
        edad.add(20.0);
        edad.add(30.0);
        dataset.getAtributos().add(edad);
    }

    /**
     * @brief Prueba el procesamiento básico de estandarización
     *
     * @details Verifica que:
     * - Los valores se transforman correctamente usando z = (x - μ)/σ
     * - Los resultados tienen media 0 y desviación 1
     * - Se mantienen las dimensiones originales
     *
     * @test Valores esperados para [10, 20, 30]:
     * - (10-20)/8.164966 ≈ -1.2247 → -1.0 (con precisión de 0.001)
     * - (20-20)/8.164966 = 0
     * - (30-20)/8.164966 ≈ 1.2247 → 1.0
     */
    @Test
    void testProcesar() {
        Estandarizacion est = new Estandarizacion();
        List<Atributo> resultado = est.procesar(dataset);

        Cuantitativo edadEst = (Cuantitativo) resultado.get(0);
        assertEquals(-1.0, edadEst.getValores().get(0), 0.001);
        assertEquals(0.0, edadEst.getValores().get(1), 0.001);
        assertEquals(1.0, edadEst.getValores().get(2), 0.001);
    }

    /**
     * @brief Prueba el manejo de atributos cualitativos
     *
     * @details Verifica que:
     * - Los atributos cualitativos no se modifican
     * - Se mantienen en el resultado final
     * - Conservan sus valores originales
     */
    @Test
    void testIgnorarCualitativos() {
        dataset.getAtributos().add(new Cualitativo("color", "rojo"));

        Estandarizacion est = new Estandarizacion();
        List<Atributo> resultado = est.procesar(dataset);

        assertEquals(2, resultado.size());
        assertTrue(resultado.get(1) instanceof Cualitativo);
        assertEquals("rojo", resultado.get(1).getValor(0));
    }

    /**
     * @brief Prueba con dataset vacío
     *
     * @details Comprueba que:
     * - No falla con un dataset sin atributos
     * - Devuelve una lista vacía
     * - Maneja correctamente el caso límite
     */
    @Test
    void testDatasetVacio() {
        Dataset vacio = new Dataset();
        Estandarizacion est = new Estandarizacion();

        List<Atributo> resultado = est.procesar(vacio);
        assertTrue(resultado.isEmpty());
    }

    /**
     * @brief Prueba con un solo valor numérico
     *
     * @details Verifica que:
     * - Cuando la desviación estándar es 0 (un solo valor)
     * - El valor no se modifica (no se puede estandarizar)
     * - No se producen errores aritméticos
     */
    @Test
    void testUnSoloValor() {
        Dataset ds = new Dataset();
        Cuantitativo temp = new Cuantitativo("temp", 25.0);
        ds.getAtributos().add(temp);

        Estandarizacion est = new Estandarizacion();
        List<Atributo> resultado = est.procesar(ds);

        Cuantitativo tempEst = (Cuantitativo) resultado.get(0);
        assertEquals(25.0, tempEst.getValores().get(0), 0.001);
    }
}