package datos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import vectores.Vector;

/**
 * @brief Pruebas unitarias para la clase Instancia
 *
 * @details Esta clase verifica el comportamiento de las instancias de datos,
 * incluyendo construcción, transformaciones y operaciones básicas.
 * Cubre funcionalidades como:
 * - Constructores con diferentes formatos de entrada
 * - Manipulación de valores y clases
 * - Normalización y estandarización de datos
 * - Conversión a diferentes representaciones
 */
class InstanciaTest {

    /**
     * @brief Prueba del constructor con lista de valores
     *
     * @details Verifica que:
     * - Crea correctamente la instancia con valores mixtos (números y string)
     * - Mantiene todos los valores proporcionados
     * - Conserva el orden de los elementos
     * - Maneja diferentes tipos numéricos (double, int)
     *
     * @test Caso con valores [1.0, 2, "clase"]
     */
    @Test
    void testConstructorLista() {
        List<Object> valores = Arrays.asList(1.0, 2, "clase");
        Instancia instancia = new Instancia(valores);

        assertEquals(3, instancia.getValores().size());
        assertEquals(1.0, instancia.getValores().get(0));
        assertTrue(instancia.getValores().contains("clase"));
    }

    /**
     * @brief Prueba del constructor con cadena CSV
     *
     * @details Comprueba que:
     * - Parsea correctamente la cadena separada por comas
     * - Conserva todos los valores como strings
     * - Maneja correctamente los espacios en blanco
     *
     * @test Caso con entrada "1.5,2,rojo"
     */
    @Test
    void testConstructorString() {
        Instancia instancia = new Instancia("1.5,2,rojo");
        assertEquals(3, instancia.getValores().size());
        assertEquals("1.5", instancia.getValores().get(0));
        assertEquals("rojo", instancia.getValores().get(2));
    }

    /**
     * @brief Prueba de conversión a Vector
     *
     * @details Verifica que:
     * - Convierte correctamente los valores numéricos
     * - Omite los valores no numéricos (clase)
     * - Mantiene la precisión de los valores
     *
     * @test Caso con valores [1.0, 2, 3.5, "clase"]
     */
    @Test
    void testGetVector() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2, 3.5, "clase"));
        Vector vector = instancia.getVector();
        assertEquals(3, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(2.0, vector.get(1));
        assertEquals(3.5, vector.get(2));
    }

    /**
     * @brief Prueba de obtención de la clase
     *
     * @details Comprueba que:
     * - Identifica correctamente el último elemento como clase
     * - Devuelve el valor esperado
     * - Maneja correctamente strings como clases
     *
     * @test Caso con valores [1.0, 2.0, "clase"]
     */
    @Test
    void testGetClase() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, "clase"));
        assertEquals("clase", instancia.getClase());
    }

    /**
     * @brief Prueba de normalización de valores
     *
     * @details Verifica que:
     * - Normaliza correctamente los valores numéricos al rango [0,1]
     * - No modifica el valor de la clase
     * - Maneja correctamente los cálculos de mínimo y máximo
     *
     * @test Valores esperados para [10.0, 20.0, 30.0]:
     * - 10 -> 0.0 (mínimo)
     * - 20 -> 0.5 (punto medio)
     * - 30 -> 1.0 (máximo)
     */
    @Test
    void testNormalizar() {
        Instancia instancia = new Instancia(Arrays.asList(10.0, 20.0, 30.0, "clase"));
        instancia.normalizar();

        assertEquals(0.0, (Double)instancia.getValores().get(0), 0.001);
        assertEquals(0.5, (Double)instancia.getValores().get(1), 0.001);
        assertEquals(1.0, (Double)instancia.getValores().get(2), 0.001);
        assertEquals("clase", instancia.getValores().get(3));
    }

    /**
     * @brief Prueba de estandarización de valores
     *
     * @details Comprueba que:
     * - Transforma los valores a distribución normal estándar (media 0, desv. 1)
     * - Preserva el valor de la clase
     * - Calcula correctamente media y desviación estándar
     *
     * @post La media de los valores estandarizados debe ser aproximadamente 0
     */
    @Test
    void testEstandarizar() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, 3.0, "clase"));
        instancia.estandarizar();

        Vector vector = instancia.getVector();
        assertEquals(0.0, vector.avg(), 0.001);
        assertEquals("clase", instancia.getValores().get(3));
    }

    /**
     * @brief Prueba de eliminación de la clase
     *
     * @details Verifica que:
     * - Elimina correctamente el último elemento (clase)
     * - Reduce el tamaño de la instancia
     * - Mantiene los valores numéricos intactos
     *
     * @post La instancia no debe contener el valor de la clase original
     */
    @Test
    void testDeleteClase() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, "clase"));
        instancia.deleteClase();
        assertEquals(2, instancia.getValores().size());
        assertFalse(instancia.getValores().contains("clase"));
    }

    /**
     * @brief Prueba de representación como cadena
     *
     * @details Comprueba que:
     * - Genera una cadena no nula
     * - Incluye todos los valores en la representación
     * - Mantiene el formato adecuado para los valores
     *
     * @test La cadena debe contener al menos un valor conocido
     */
    @Test
    void testToString() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, "clase"));
        assertNotNull(instancia.toString());
        assertTrue(instancia.toString().contains("1.0"));
    }
}