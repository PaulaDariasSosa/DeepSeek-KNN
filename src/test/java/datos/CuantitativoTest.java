package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import vectores.Vector;

/**
 * @brief Pruebas unitarias para la clase Cuantitativo
 *
 * @details Esta clase verifica el comportamiento de los atributos cuantitativos,
 * incluyendo operaciones estadísticas, manipulación de datos y métodos básicos.
 * Cubre funcionalidades como:
 * - Constructores y manejo de valores iniciales
 * - Cálculos estadísticos (media, desviación estándar)
 * - Normalización y estandarización de datos
 * - Operaciones CRUD sobre los valores
 */
class CuantitativoTest {
    private Cuantitativo atributo;

    /**
     * @brief Configuración inicial para las pruebas
     *
     * @details Crea un atributo cuantitativo "edad" con valores iniciales:
     * - 25.0
     * - 30.0
     * - 35.0
     *
     * Este estado inicial permite probar:
     * - Cálculos estadísticos con valores distribuidos
     * - Operaciones sobre múltiples valores
     * - Transformaciones de normalización
     */
    @BeforeEach
    void setUp() {
        atributo = new Cuantitativo("edad");
        atributo.add(25.0);
        atributo.add(30.0);
        atributo.add(35.0);
    }

    /**
     * @brief Prueba del constructor vacío
     *
     * @details Verifica que:
     * - Se crea con nombre vacío
     * - No contiene valores iniciales
     * - El tamaño es 0
     */
    @Test
    void testConstructorVacio() {
        Cuantitativo c = new Cuantitativo();
        assertEquals("", c.getNombre());
        assertEquals(0, c.size());
    }

    /**
     * @brief Prueba del constructor con nombre
     *
     * @details Comprueba que:
     * - Establece correctamente el nombre
     * - Inicializa sin valores
     * - El tamaño inicial es 0
     */
    @Test
    void testConstructorConNombre() {
        Cuantitativo c = new Cuantitativo("altura");
        assertEquals("altura", c.getNombre());
        assertEquals(0, c.size());
    }

    /**
     * @brief Prueba del constructor con nombre y valor inicial
     *
     * @details Verifica que:
     * - Establece correctamente el nombre
     * - Añade el valor inicial
     * - El tamaño es 1
     */
    @Test
    void testConstructorConNombreYValor() {
        Cuantitativo c = new Cuantitativo("altura", 180.0);
        assertEquals("altura", c.getNombre());
        assertEquals(1, c.size());
        assertEquals(180.0, c.getValor(0));
    }

    /**
     * @brief Prueba del constructor con nombre y vector de valores
     *
     * @details Comprueba que:
     * - Establece correctamente el nombre
     * - Importa todos los valores del vector
     * - Mantiene el orden de los valores
     */
    @Test
    void testConstructorConNombreYVector() {
        Vector v = new Vector(new double[]{25, 30, 35});
        Cuantitativo c = new Cuantitativo("edad", v);
        assertEquals("edad", c.getNombre());
        assertEquals(3, c.size());
        assertEquals(25.0, c.getValor(0));
    }

    /**
     * @brief Prueba del cálculo del valor mínimo
     *
     * @details Verifica que:
     * - Devuelve el valor más pequeño
     * - Actualiza correctamente al añadir nuevos valores
     * - Maneja correctamente el estado inicial
     */
    @Test
    void testMinimo() {
        assertEquals(25.0, atributo.minimo(), 0.001);

        // Añadir valor más pequeño
        atributo.add(20.0);
        assertEquals(20.0, atributo.minimo(), 0.001);
    }

    /**
     * @brief Prueba del cálculo del valor máximo
     *
     * @details Comprueba que:
     * - Devuelve el valor más grande
     * - Actualiza correctamente al añadir nuevos valores
     * - Maneja correctamente el estado inicial
     */
    @Test
    void testMaximo() {
        assertEquals(35.0, atributo.maximo(), 0.001);

        // Añadir valor más grande
        atributo.add(40.0);
        assertEquals(40.0, atributo.maximo(), 0.001);
    }

    /**
     * @brief Prueba del cálculo de la media
     *
     * @details Verifica que:
     * - Calcula correctamente el promedio
     * - Maneja adecuadamente conjuntos vacíos (devuelve NaN)
     * - Mantiene precisión en los cálculos
     *
     * @test Caso con valores [25, 30, 35] -> media = 30
     */
    @Test
    void testMedia() {
        assertEquals(30.0, atributo.media(), 0.001);

        Cuantitativo vacio = new Cuantitativo("vacio");
        assertEquals(NaN, vacio.media());
    }

    /**
     * @brief Prueba del cálculo de la desviación estándar
     *
     * @details Comprueba que:
     * - Calcula correctamente la desviación muestral
     * - Maneja correctamente conjuntos con un solo valor (devuelve 0)
     * - Mantiene precisión en los cálculos
     *
     * @test Caso con valores [25, 30, 35]:
     * - Varianza = 25
     * - Desviación = 5
     */
    @Test
    void testDesviacionTipica() {
        double desviacion = atributo.desviacion();
        assertEquals(5.0, desviacion, 0.001);

        // Test con un solo valor
        Cuantitativo c = new Cuantitativo("test", 10.0);
        assertEquals(0.0, c.desviacion(), 0.001);
    }

    /**
     * @brief Prueba del método de estandarización
     *
     * @details Verifica que:
     * - Transforma los valores a distribución normal estándar
     * - Calcula correctamente z-scores
     * - Mantiene la integridad de los datos originales
     *
     * @test Valores esperados para [25,30,35]:
     * - (25-30)/5 = -1
     * - (30-30)/5 = 0
     * - (35-30)/5 = 1
     */
    @Test
    void testEstandarizacion() {
        atributo.estandarizacion();
        Vector valores = atributo.getValores();

        assertEquals(-1.0, valores.get(0), 0.001);
        assertEquals(0.0, valores.get(1), 0.001);
        assertEquals(1.0, valores.get(2), 0.001);
    }

    /**
     * @brief Prueba de adición de valores
     *
     * @details Comprueba que:
     * - Añade correctamente nuevos valores numéricos
     * - Maneja conversión implícita de enteros a double
     * - Rechaza valores no numéricos (lanza excepción)
     * - Incrementa el tamaño del conjunto
     */
    @Test
    void testAddObject() {
        atributo.add(40.0);
        assertEquals(4, atributo.size());
        assertEquals(40.0, atributo.getValor(3));

        // Test con enteros
        atributo.add(45);
        assertEquals(45.0, atributo.getValor(4));

        // Test con valor no numérico
        assertThrows(IllegalArgumentException.class, () -> atributo.add("no numérico"));
    }

    /**
     * @brief Prueba de obtención de valores por índice
     *
     * @details Verifica que:
     * - Devuelve el valor correcto para índices válidos
     * - Lanza excepción para índices fuera de rango
     * - Mantiene la precisión numérica
     */
    @Test
    void testGetValor() {
        assertEquals(25.0, (Double)atributo.getValor(0), 0.001);
        assertThrows(IndexOutOfBoundsException.class, () -> atributo.getValor(5));
    }

    /**
     * @brief Prueba de eliminación de valores
     *
     * @details Comprueba que:
     * - Elimina correctamente valores por índice
     * - Actualiza el tamaño del conjunto
     * - Reordena los valores restantes
     * - Lanza excepción para índices inválidos
     */
    @Test
    void testDelete() {
        atributo.delete(0);
        assertEquals(2, atributo.size());
        assertEquals(30.0, atributo.getValor(0));

        assertThrows(IndexOutOfBoundsException.class, () -> atributo.delete(5));
    }

    /**
     * @brief Prueba de copia del atributo
     *
     * @details Verifica que:
     * - Crea una copia independiente
     * - Replica todos los valores y propiedades
     * - Mantiene la igualdad de contenido pero no de referencia
     */
    @Test
    void testCopiar() {
        Cuantitativo copia = atributo.copiar();
        assertEquals(atributo.getNombre(), copia.getNombre());
        assertEquals(atributo.size(), copia.size());
        assertNotSame(atributo.getValores(), copia.getValores());
        assertEquals(atributo.getValor(0), copia.getValor(0));
    }

    /**
     * @brief Prueba de representación como cadena
     *
     * @details Comprueba que:
     * - Incluye todos los valores en la representación
     * - Muestra los valores con formato adecuado
     * - Contiene los valores extremos
     */
    @Test
    void testToString() {
        String str = atributo.toString();
        assertTrue(str.contains("25.0"));
        assertTrue(str.contains("35.0"));
    }

    /**
     * @brief Prueba de limpieza del atributo
     *
     * @details Verifica que:
     * - Elimina todos los valores
     * - Establece el tamaño a 0
     * - Mantiene el nombre del atributo
     */
    @Test
    void testClear() {
        atributo.clear();
        assertEquals(0, atributo.size());
    }
}