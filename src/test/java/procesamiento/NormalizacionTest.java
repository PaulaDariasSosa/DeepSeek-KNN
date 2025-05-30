package procesamiento;

import datos.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * @brief Pruebas unitarias para la clase Normalizacion
 *
 * @details Esta clase verifica el correcto funcionamiento del procesador de normalización,
 * que escala los valores numéricos al rango [0,1] utilizando la fórmula:
 * (x - min) / (max - min). Atributos cualitativos deben permanecer sin cambios.
 */
class NormalizacionTest {

    /**
     * @brief Prueba el procesamiento básico de normalización
     *
     * @details Verifica que:
     * - Los valores numéricos se escalan correctamente al rango [0,1]
     * - El valor mínimo se transforma a 0
     * - El valor máximo se transforma a 1
     * - Los valores intermedios se escalan proporcionalmente
     *
     * @test Caso con valores [20.0, 40.0, 60.0]:
     * - 20 (min) → 0.0
     * - 40 → 0.5
     * - 60 (max) → 1.0
     */
    @Test
    void testProcesar() {
        Dataset datos = new Dataset();
        Cuantitativo edad = new Cuantitativo("edad");
        edad.add(20.0);
        edad.add(40.0);
        edad.add(60.0);
        datos.getAtributos().add(edad);

        Normalizacion normalizacion = new Normalizacion();
        List<Atributo> resultado = normalizacion.procesar(datos);

        Cuantitativo edadNorm = (Cuantitativo) resultado.get(0);
        assertEquals(0.0, edadNorm.getValores().get(0), 0.001);
        assertEquals(0.5, edadNorm.getValores().get(1), 0.001);
        assertEquals(1.0, edadNorm.getValores().get(2), 0.001);
    }

    /**
     * @brief Prueba el manejo de atributos cualitativos
     *
     * @details Verifica que:
     * - Los atributos cualitativos no se modifican
     * - Conservan sus valores originales
     * - No se aplica transformación alguna
     */
    @Test
    void testProcesarConAtributoCualitativo() {
        Dataset datos = new Dataset();
        datos.getAtributos().add(new Cualitativo("color", "rojo"));

        Normalizacion normalizacion = new Normalizacion();
        List<Atributo> resultado = normalizacion.procesar(datos);

        assertEquals("rojo", resultado.get(0).getValor(0));
    }
}