package datos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @brief Clase base abstracta para pruebas de la jerarquía de Atributo
 *
 * @details Define los tests comunes para todas las subclases de Atributo,
 * implementando el patrón Template Method para permitir pruebas específicas
 * de cada implementación concreta.
 *
 * Las pruebas incluyen:
 * - Manejo básico de nombres
 * - Asignación y recuperación de pesos
 * - Formateo de salida
 *
 * @note Las clases concretas deben implementar el método factory crearAtributo()
 */
public abstract class AtributoTest {

    /**
     * @brief Método factory para crear instancias de Atributo
     *
     * @param nombre Nombre inicial para el atributo
     * @return Instancia de una subclase concreta de Atributo
     *
     * @note Las subclases deben implementar este método para proporcionar
     *       su versión específica de Atributo
     */
    protected abstract Atributo crearAtributo(String nombre);

    /**
     * @brief Prueba el manejo básico del nombre del atributo
     *
     * @details Verifica que:
     * - El nombre se establece correctamente en el constructor
     * - El método getNombre() devuelve el valor esperado
     * - El método setNombre() actualiza correctamente el valor
     *
     * @test Se comprueba tanto el valor inicial como uno modificado
     */
    @Test
    @DisplayName("Debería obtener y establecer correctamente el nombre")
    void testGetSetNombre() {
        Atributo atributo = crearAtributo("test");
        assertEquals("test", atributo.getNombre());

        atributo.setNombre("nuevo");
        assertEquals("nuevo", atributo.getNombre());
    }

    /**
     * @brief Prueba parametrizada del manejo de pesos
     *
     * @details Verifica para múltiples valores que:
     * - El peso se establece correctamente
     * - El método getPeso() devuelve el valor asignado
     * - Se mantiene la precisión adecuada
     *
     * @param peso Valor de peso a probar (proporcionado por ValueSource)
     *
     * @note Usa ParameterizedTest para verificar diferentes casos límite
     */
    @ParameterizedTest
    @ValueSource(doubles = {0.1, 0.5, 1.0})
    @DisplayName("Debería establecer y obtener correctamente el peso")
    void testGetSetPeso(double peso) {
        Atributo atributo = crearAtributo("test");
        atributo.setPeso(peso);
        assertEquals(peso, atributo.getPeso(), 0.001);
    }

    /**
     * @brief Prueba el formateo de salida del atributo
     *
     * @details Verifica que:
     * - El formato de salida sigue el patrón "nombre: peso"
     * - Los valores se incorporan correctamente
     * - El peso se muestra con la precisión adecuada
     */
    @Test
    @DisplayName("Debería formatear correctamente la salida get()")
    void testGet() {
        Atributo atributo = crearAtributo("edad");
        atributo.setPeso(0.5);
        assertEquals("edad: 0.5", atributo.get());
    }
}

/**
 * @brief Pruebas específicas para Atributo Cuantitativo
 *
 * @details Implementa los tests definidos en AtributoTest
 * para la variante Cuantitativo de Atributo
 */
class CuantitativoAtributoTest extends AtributoTest {
    /**
     * @brief Implementación del método factory para Cuantitativo
     *
     * @param nombre Nombre para el atributo
     * @return Nueva instancia de Cuantitativo
     */
    @Override
    protected Atributo crearAtributo(String nombre) {
        return new Cuantitativo(nombre);
    }
}

/**
 * @brief Pruebas específicas para Atributo Cualitativo
 *
 * @details Implementa los tests definidos en AtributoTest
 * para la variante Cualitativo de Atributo
 */
class CualitativoAtributoTest extends AtributoTest {
    /**
     * @brief Implementación del método factory para Cualitativo
     *
     * @param nombre Nombre para el atributo
     * @return Nueva instancia de Cualitativo
     */
    @Override
    protected Atributo crearAtributo(String nombre) {
        return new Cualitativo(nombre);
    }
}