package knnproject;

import datos.Cualitativo;
import datos.Cuantitativo;
import datos.Dataset;
import datos.Instancia;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @brief Pruebas unitarias para la clase KnnTfg
 *
 * @details Esta clase prueba la funcionalidad de la interfaz de usuario
 * y manipulación de datasets en la aplicación KNN.
 */
class KnnTfgTest {

    /**
     * @brief Prueba la opción de salida del menú
     *
     * @details Verifica que:
     * - La opción 7 (salir) marca correctamente el contexto para salir
     * - No se producen errores con la entrada válida
     */
    @Test
    void testProcesarOpcionSalir() {
        KnnTfg.AppContext context = new KnnTfg.AppContext();
        String input = "7\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        KnnTfg.procesarOpcion(context, scanner);
        assertTrue(context.salida);
    }

    /**
     * @brief Prueba una opción inválida en el menú
     *
     * @details Comprueba que:
     * - Las opciones inválidas no activan la salida
     * - El programa sigue funcionando después de entrada inválida
     */
    @Test
    void testProcesarOpcionInvalida() {
        KnnTfg.AppContext context = new KnnTfg.AppContext();
        String input = "99\n7\n"; // Opción inválida luego salir
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        KnnTfg.procesarOpcion(context, scanner);
        assertFalse(context.salida);

        KnnTfg.procesarOpcion(context, scanner);
        assertTrue(context.salida);
    }

    /**
     * @brief Prueba la adición de una instancia
     *
     * @details Verifica que:
     * - Se puede añadir una nueva instancia al dataset
     * - Los valores se asignan correctamente a los atributos
     * - El tamaño del dataset aumenta
     */
    @Test
    void testModifyAddInstance() throws Exception {
        String input = "1\n25.0,rojo\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        Dataset data = new Dataset();
        data.getAtributos().add(new Cuantitativo("edad"));
        data.getAtributos().add(new Cualitativo("color"));

        InputStream originalIn = System.in;
        System.setIn(in);

        try {
            Dataset result = KnnTfg.modify(data);
            assertEquals(1, result.numeroCasos());
            assertEquals(25.0, result.getAtributos().get(0).getValor(0));
            assertEquals("rojo", result.getAtributos().get(1).getValor(0));
        } finally {
            System.setIn(originalIn);
        }
    }

    /**
     * @brief Prueba la eliminación de una instancia
     *
     * @details Comprueba que:
     * - Se puede eliminar una instancia existente
     * - El tamaño del dataset disminuye
     * - Las instancias restantes se mantienen correctamente
     */
    @Test
    void testModifyDeleteInstance() throws Exception {
        Dataset data = new Dataset();
        data.getAtributos().add(new Cuantitativo("edad"));
        data.getAtributos().add(new Cualitativo("color"));
        data.add(new Instancia(Arrays.asList(25.0, "rojo")));
        data.add(new Instancia(Arrays.asList(30.0, "azul")));

        String input = "2\n0\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());

        InputStream originalIn = System.in;
        System.setIn(in);

        try {
            Dataset result = KnnTfg.modify(data);
            assertEquals(1, result.numeroCasos());
            assertEquals(30.0, result.getAtributos().get(0).getValor(0));
            assertEquals("azul", result.getAtributos().get(1).getValor(0));
        } finally {
            System.setIn(originalIn);
        }
    }

    /**
     * @brief Prueba una opción de menú inválida
     *
     * @details Verifica que:
     * - Las opciones inválidas no modifican el dataset
     * - El programa maneja correctamente la entrada inválida
     */
    @Test
    void testModifyInvalidOption() throws Exception {
        String input = "99\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());

        Dataset data = new Dataset();
        data.getAtributos().add(new Cuantitativo("edad"));

        InputStream originalIn = System.in;
        System.setIn(in);

        try {
            Dataset result = KnnTfg.modify(data);
            assertEquals(0, result.numeroCasos());
        } finally {
            System.setIn(originalIn);
        }
    }

    /**
     * @brief Prueba entrada no numérica en el menú
     *
     * @details Comprueba que:
     * - La entrada no numérica no causa errores
     * - El dataset permanece sin cambios
     */
    @Test
    void testModifyInvalidInput() throws Exception {
        String input = "no_numérico\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());

        Dataset data = new Dataset();
        data.getAtributos().add(new Cuantitativo("edad"));

        InputStream originalIn = System.in;
        System.setIn(in);

        try {
            Dataset result = KnnTfg.modify(data);
            assertEquals(0, result.numeroCasos());
        } finally {
            System.setIn(originalIn);
        }
    }
}