package datos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public abstract class AtributoTest {

    protected abstract Atributo crearAtributo(String nombre);

    @Test
    @DisplayName("Debería obtener y establecer correctamente el nombre")
    void testGetSetNombre() {
        Atributo atributo = crearAtributo("test");
        assertEquals("test", atributo.getNombre());

        atributo.setNombre("nuevo");
        assertEquals("nuevo", atributo.getNombre());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.1, 0.5, 1.0})
    @DisplayName("Debería establecer y obtener correctamente el peso")
    void testGetSetPeso(double peso) {
        Atributo atributo = crearAtributo("test");
        atributo.setPeso(peso);
        assertEquals(peso, atributo.getPeso(), 0.001);
    }

    @Test
    @DisplayName("Debería formatear correctamente la salida get()")
    void testGet() {
        Atributo atributo = crearAtributo("edad");
        atributo.setPeso(0.5);
        assertEquals("edad: 0.5", atributo.get());
    }
}

class CuantitativoAtributoTest extends AtributoTest {
    @Override
    protected Atributo crearAtributo(String nombre) {
        return new Cuantitativo(nombre);
    }
}

class CualitativoAtributoTest extends AtributoTest {
    @Override
    protected Atributo crearAtributo(String nombre) {
        return new Cualitativo(nombre);
    }
}
