package procesamiento;

import datos.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Pruebas unitarias para la clase DatosCrudos
 *
 * @details Verifica el comportamiento básico del procesador DatosCrudos,
 * que actúa como una transformación de datos nula (identity transform).
 * Confirma que los datos de entrada se devuelven sin modificaciones.
 */
class DatosCrudosTest {

    /**
     * @brief Prueba el método procesar() de DatosCrudos
     *
     * @details Verifica que el procesador:
     * - Mantiene intactos los atributos originales
     * - Conserva los valores numéricos sin cambios
     * - Preserva la estructura completa del Dataset
     * - No aplica ninguna transformación a los datos
     *
     * @test Caso con un solo atributo cuantitativo ("edad" con valor 25.0)
     * @test Comprueba tamaño de lista resultante
     * @test Verifica conservación de valores numéricos
     */
    @Test
    void testProcesar() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cuantitativo("edad", 25.0));

        DatosCrudos proc = new DatosCrudos();
        List<Atributo> resultado = proc.procesar(datos);

        assertEquals(1, resultado.size());
        assertEquals(25.0, resultado.get(0).getValor(0));
    }
}