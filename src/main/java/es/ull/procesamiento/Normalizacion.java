package procesamiento;

import java.util.ArrayList;
import java.util.List;
import datos.*;
import vectores.Vector;

/**
 * @brief Implementación de preprocesamiento para normalización min-max de datos
 *
 * Esta clase aplica normalización min-max a los atributos cuantitativos de un dataset,
 * escalando los valores al rango [0, 1] usando la fórmula:
 * valor_normalizado = (valor - min) / (max - min)
 */
public class Normalizacion implements Preprocesado {

	/**
	 * @brief Aplica normalización min-max a los atributos cuantitativos del dataset
	 * @param datos Dataset a procesar
	 * @return Lista de atributos con valores normalizados
	 *
	 * El método realiza las siguientes operaciones:
	 * 1. Crea una nueva lista copia de los atributos originales
	 * 2. Identifica atributos cuantitativos mediante reflexión
	 * 3. Para cada atributo cuantitativo:
	 *    - Obtiene sus valores como Vector
	 *    - Aplica normalización min-max
	 *    - Actualiza los valores del atributo
	 * 4. Devuelve la lista de atributos modificados
	 *
	 * @note Solo afecta a atributos de tipo Cuantitativo
	 * @note Los valores se escalan al rango [0,1] preservando su distribución original
	 */
	public List<Atributo> procesar(Dataset datos) {
		List<Atributo> nuevos = new ArrayList<Atributo>(datos.getAtributos());
		Cuantitativo ejemplo = new Cuantitativo();
		for (int i = 0; i < nuevos.size(); i++) {
			if (nuevos.get(i).getClass() == ejemplo.getClass()) {
				ejemplo = (Cuantitativo) nuevos.get(i);
				Vector valores = ejemplo.getValores();
				valores.normalize();
				ejemplo.setValores(valores);
				nuevos.set(i,ejemplo);
			}
		}
		return nuevos;
	}
}