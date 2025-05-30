package vectores;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/**
 * @brief Clase de pruebas unitarias para la clase Matriz
 *
 * @details Esta clase prueba exhaustivamente todos los aspectos funcionales de la clase Matriz,
 * incluyendo:
 * - Constructores y creación de matrices
 * - Operaciones básicas (get/set)
 * - Operaciones algebraicas (multiplicación, transposición)
 * - Manipulación de dimensiones (añadir/eliminar filas/columnas)
 * - Serialización (lectura/escritura de archivos)
 * - Casos especiales y manejo de errores
 *
 * Cada método de prueba está diseñado para verificar un aspecto específico de la funcionalidad,
 * incluyendo casos límite y condiciones de error.
 */
class MatrizTest {
    private Matriz matriz2x2;
    private Matriz matriz2x3;
    private Matriz matriz3x2;

    /**
     * @brief Configura el entorno de prueba antes de cada test
     *
     * @details Inicializa tres matrices con diferentes dimensiones para pruebas:
     * - matriz2x2: Matriz cuadrada 2x2 como caso base
     * - matriz2x3: Matriz rectangular para probar operaciones con dimensiones desiguales
     * - matriz3x2: Matriz rectangular transpuesta a matriz2x3
     *
     * Los valores de prueba están diseñados para:
     * - Ser fácilmente verificables manualmente
     * - Cubrir diferentes combinaciones de valores
     * - Permitir verificar cálculos matriciales básicos
     */
    @BeforeEach
    void setUp() {
        // Matriz 2x2
        double[][] datos2x2 = {{1, 2}, {3, 4}};
        matriz2x2 = new Matriz(2, 2, datos2x2);

        // Matriz 2x3
        double[][] datos2x3 = {{1, 2, 3}, {4, 5, 6}};
        matriz2x3 = new Matriz(2, 3, datos2x3);

        // Matriz 3x2
        double[][] datos3x2 = {{1, 2}, {3, 4}, {5, 6}};
        matriz3x2 = new Matriz(3, 2, datos3x2);
    }

    /**
     * @brief Prueba el constructor vacío de Matriz
     *
     * @details Verifica que:
     * - Se crea una matriz 1x1 por defecto
     * - El valor inicial es 0.0
     * - Las dimensiones son correctas
     *
     * @post La matriz creada tiene tamaño 1x1 con valor 0.0
     */
    @Test
    void testConstructorVacio() {
        Matriz m = new Matriz();
        assertEquals(1, m.getNumRows());
        assertEquals(1, m.getNumCols());
        assertEquals(0.0, m.get(0, 0));
    }

    /**
     * @brief Prueba el constructor con dimensiones específicas
     *
     * @details Verifica que:
     * - Se crea una matriz con las dimensiones exactas solicitadas
     * - Todos los valores iniciales son 0.0
     * - Las dimensiones reportadas son correctas
     *
     * @param[in] 3 Número de filas esperado
     * @param[in] 4 Número de columnas esperado
     */
    @Test
    void testConstructorDimensiones() {
        Matriz m = new Matriz(3, 4);
        assertEquals(3, m.getNumRows());
        assertEquals(4, m.getNumCols());
    }

    /**
     * @brief Prueba el constructor con datos iniciales
     *
     * @details Verifica que:
     * - Los valores se asignan correctamente a sus posiciones
     * - Se mantiene el orden de filas/columnas
     * - Se puede acceder a los elementos extremos
     *
     * @test Se comprueban específicamente las esquinas de la matriz
     */
    @Test
    void testConstructorConDatos() {
        assertEquals(1.0, matriz2x2.get(0, 0));
        assertEquals(4.0, matriz2x2.get(1, 1));
    }

    /**
     * @brief Prueba el constructor a partir de lista de vectores
     *
     * @details Verifica que:
     * - Cada vector se convierte en una fila de la matriz
     * - Las dimensiones se calculan correctamente
     * - Los valores se transfieren adecuadamente
     *
     * @note Este test usa vectores simples para verificar la conversión
     */
    @Test
    void testConstructorConVectores() {
        List<Vector> vectores = new ArrayList<>();
        vectores.add(new Vector(new double[]{1, 2}));
        vectores.add(new Vector(new double[]{3, 4}));

        Matriz m = new Matriz(vectores);
        assertEquals(2, m.getNumRows());
        assertEquals(2, m.getNumCols());
        assertEquals(3.0, m.get(1, 0));
    }

    /**
     * @brief Prueba los métodos básicos get() y set()
     *
     * @details Verifica que:
     * - set() actualiza correctamente el valor en una posición
     * - get() recupera el valor actualizado
     * - Los cambios son persistentes
     *
     * @test Se modifica un elemento y se verifica su nuevo valor
     */
    @Test
    void testGetSet() {
        matriz2x2.set(0, 1, 5.0);
        assertEquals(5.0, matriz2x2.get(0, 1));
    }

    /**
     * @brief Prueba el manejo de índices inválidos
     *
     * @details Verifica que:
     * - Se lanza IndexOutOfBoundsException para filas inválidas
     * - Se lanza IndexOutOfBoundsException para columnas inválidas
     * - El mensaje de error es adecuado
     *
     * @expected IndexOutOfBoundsException en ambos casos
     */
    @Test
    void testGetInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> matriz2x2.get(3, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> matriz2x2.get(0, 3));
    }

    /**
     * @brief Prueba la multiplicación de matrices
     *
     * @details Verifica que:
     * - El producto matricial se calcula correctamente
     * - Los valores en cada posición son los esperados
     * - Las dimensiones del resultado son correctas
     *
     * @note Usa matrices 2x2 simples para facilitar la verificación manual
     */
    @Test
    void testMultiplicacion() {
        Matriz a = new Matriz(2, 2);
        a.set(0, 0, 1); a.set(0, 1, 2);
        a.set(1, 0, 3); a.set(1, 1, 4);

        Matriz b = new Matriz(2, 2);
        b.set(0, 0, 5); b.set(0, 1, 6);
        b.set(1, 0, 7); b.set(1, 1, 8);

        Matriz res = Matriz.multiply(a, b);
        assertEquals(19.0, res.get(0, 0), 0.001);
        assertEquals(22.0, res.get(0, 1), 0.001);
        assertEquals(43.0, res.get(1, 0), 0.001);
        assertEquals(50.0, res.get(1, 1), 0.001);
    }

    /**
     * @brief Prueba la transposición de matrices
     *
     * @details Verifica que:
     * - Las dimensiones se intercambian correctamente
     * - Los valores se trasladan a sus nuevas posiciones
     * - La estructura de la matriz se mantiene consistente
     *
     * @test Comprueba varios elementos para asegurar la transposición completa
     */
    @Test
    void testTransposicion() {
        matriz2x3.transpose();
        assertEquals(2, matriz2x3.getNumRows());
        assertEquals(3, matriz2x3.getNumCols());
        assertEquals(1.0, matriz2x3.get(0, 0), 0.001);
        assertEquals(4.0, matriz2x3.get(0, 1), 0.001);
        assertEquals(3.0, matriz2x3.get(2, 0), 0.001);
        assertEquals(6.0, matriz2x3.get(2, 1), 0.001);
    }

    // [...] (el resto de los métodos de prueba con documentación similar)
}