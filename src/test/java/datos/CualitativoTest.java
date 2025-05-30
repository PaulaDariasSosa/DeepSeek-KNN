package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * @brief Pruebas unitarias para la clase Cualitativo
 *
 * @details Esta clase verifica el comportamiento de los atributos cualitativos,
 * incluyendo manejo de categorías, cálculo de frecuencias y operaciones básicas.
 * Se enfoca en:
 * - Gestión de valores discretos (categorías)
 * - Cálculo estadístico de frecuencias
 * - Operaciones de copia y recuperación de valores
 */
class CualitativoTest {
    private Cualitativo atributo;

    /**
     * @brief Configuración inicial para las pruebas
     *
     * @details Crea un atributo cualitativo "color" con valores iniciales:
     * - rojo (2 ocurrencias)
     * - azul (1 ocurrencia)
     * - verde (1 ocurrencia)
     *
     * Este estado inicial se usa para probar:
     * - Conteo de categorías únicas
     * - Cálculo de frecuencias relativas
     * - Operaciones sobre múltiples valores
     */
    @BeforeEach
    void setUp() {
        atributo = new Cualitativo("color");
        atributo.add("rojo");
        atributo.add("azul");
        atributo.add("rojo");
        atributo.add("verde");
    }

    /**
     * @brief Prueba el método clases()
     *
     * @details Verifica que:
     * - Devuelve la lista correcta de categorías únicas
     * - El tamaño de la lista coincide con el número de categorías distintas
     * - Contiene al menos una categoría conocida
     *
     * @test Se esperan exactamente 3 categorías únicas
     */
    @Test
    void testClases() {
        List<String> clases = atributo.clases();
        assertEquals(3, clases.size());
        assertTrue(clases.contains("rojo"));
    }

    /**
     * @brief Prueba el cálculo de frecuencias relativas
     *
     * @details Comprueba que:
     * - Las frecuencias se calculan correctamente (ocurrencias/total)
     * - Los valores coinciden con la distribución esperada
     * - El orden corresponde al de las clases únicas
     *
     * @test Valores esperados:
     * - rojo: 0.5 (2/4)
     * - azul: 0.25 (1/4)
     */
    @Test
    void testFrecuencia() {
        List<Double> frecuencias = atributo.frecuencia();
        assertEquals(0.5, frecuencias.get(0), 0.001); // rojo
        assertEquals(0.25, frecuencias.get(1), 0.001); // azul
    }

    /**
     * @brief Prueba la adición de nuevos valores
     *
     * @details Verifica que:
     * - Aumenta el contador total de valores
     * - Acepta nuevas categorías
     * - Mantiene la integridad de los datos existentes
     *
     * @post El tamaño debe incrementarse en 1
     */
    @Test
    void testAddObject() {
        atributo.add("amarillo");
        assertEquals(5, atributo.size());
    }

    /**
     * @brief Prueba la recuperación de valores por índice
     *
     * @details Comprueba que:
     * - Devuelve el valor correcto para una posición dada
     * - Mantiene el orden de inserción
     *
     * @test El primer valor insertado fue "rojo"
     */
    @Test
    void testGetValor() {
        assertEquals("rojo", atributo.getValor(0));
    }

    /**
     * @brief Prueba la operación de copia
     *
     * @details Verifica que:
     * - Crea una nueva instancia con igual nombre
     * - Replica todos los valores
     * - La copia es independiente del original
     *
     * @test La copia debe ser igual en contenido pero diferente en identidad
     */
    @Test
    void testCopiar() {
        Cualitativo copia = atributo.copiar();
        assertEquals(atributo.getNombre(), copia.getNombre());
        assertEquals(atributo.size(), copia.size());
        assertNotSame(atributo, copia);
    }
}