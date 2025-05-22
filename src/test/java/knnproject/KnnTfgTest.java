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

class KnnTfgTest {

    @Test
    void testProcesarOpcionSalir() {
        KnnTfg.AppContext context = new KnnTfg.AppContext();
        String input = "7\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        KnnTfg.procesarOpcion(context, scanner);
        assertTrue(context.salida);
    }

    @Test
    void testProcesarOpcionInvalida() {
        KnnTfg.AppContext context = new KnnTfg.AppContext();
        String input = "99\n7\n"; // Opción inválida luego salir
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        // Primera llamada con opción inválida
        KnnTfg.procesarOpcion(context, scanner);
        assertFalse(context.salida); // No debería salir aún

        // Segunda llamada con opción de salida
        KnnTfg.procesarOpcion(context, scanner);
        assertTrue(context.salida); // Ahora debería salir
    }

    @Test
    void testModifyAddInstance() throws Exception {
        // Configurar entrada simulada
        String input = "1\n25.0,rojo\n5\n"; // Añadir instancia luego salir
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        Dataset data = new Dataset();
        data.getAtributos().add(new Cuantitativo("edad"));
        data.getAtributos().add(new Cualitativo("color"));

        // Redirigir System.in temporalmente
        InputStream originalIn = System.in;
        System.setIn(in);

        try {
            Dataset result = KnnTfg.modify(data);
            assertEquals(1, result.numeroCasos());
            assertEquals(25.0, result.getAtributos().get(0).getValor(0));
            assertEquals("rojo", result.getAtributos().get(1).getValor(0));
        } finally {
            System.setIn(originalIn); // Restaurar System.in
        }
    }

    @Test
    void testModifyDeleteInstance() throws Exception {
        // Configurar dataset de prueba
        Dataset data = new Dataset();
        data.getAtributos().add(new Cuantitativo("edad"));
        data.getAtributos().add(new Cualitativo("color"));
        data.add(new Instancia(Arrays.asList(25.0, "rojo")));
        data.add(new Instancia(Arrays.asList(30.0, "azul")));

        // Simular entrada: eliminar primera instancia (índice 0) luego salir
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

    @Test
    void testModifyInvalidOption() throws Exception {
        // Configurar entrada simulada: opción inválida luego salir
        String input = "99\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());

        Dataset data = new Dataset();
        data.getAtributos().add(new Cuantitativo("edad"));

        InputStream originalIn = System.in;
        System.setIn(in);

        try {
            Dataset result = KnnTfg.modify(data);
            assertEquals(0, result.numeroCasos()); // No debería cambiar
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void testModifyInvalidInput() throws Exception {
        // Configurar entrada simulada: no numérica luego salir
        String input = "no_numérico\n5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());

        Dataset data = new Dataset();
        data.getAtributos().add(new Cuantitativo("edad"));

        InputStream originalIn = System.in;
        System.setIn(in);

        try {
            Dataset result = KnnTfg.modify(data);
            assertEquals(0, result.numeroCasos()); // No debería cambiar
        } finally {
            System.setIn(originalIn);
        }
    }

}