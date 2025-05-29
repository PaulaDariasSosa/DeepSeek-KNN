package procesamiento;

import java.util.List;
import datos.Atributo;
import datos.Cuantitativo;
import datos.Dataset;

/**
 * @brief Implementación de preprocesamiento para estandarización de datos
 *
 * Esta clase aplica la técnica de estandarización (normalización z-score) a los
 * atributos cuantitativos de un dataset, transformándolos para tener:
 * - Media = 0
 * - Desviación estándar = 1
 *
 * No se aplica a atributos cualitativos o cuando la desviación estándar es cero.
 */
public class Estandarizacion implements Preprocesado {

	/**
	 * @brief Aplica estandarización a los atributos cuantitativos del dataset
	 * @param datos Dataset a procesar
	 * @return Lista de atributos con los valores estandarizados
	 *
	 * El método realiza las siguientes operaciones:
	 * 1. Recorre todos los atributos del dataset
	 * 2. Identifica los atributos cuantitativos (Cuantitativo)
	 * 3. Aplica estandarización solo si:
	 *    - Hay más de un valor
	 *    - La desviación estándar es mayor que cero
	 * 4. Devuelve la lista de atributos modificados
	 */
	public List<Atributo> procesar(Dataset datos) {
		List<Atributo> nuevos = datos.getAtributos();
		for (int i = 0; i < nuevos.size(); i++) {
			if (nuevos.get(i) instanceof Cuantitativo) {
				Cuantitativo atributo = (Cuantitativo) nuevos.get(i);
				// No estandarizar si solo hay un valor o todos iguales
				if (atributo.size() > 1 && atributo.desviacion() > 0) {
					atributo.estandarizacion();
					nuevos.set(i, atributo);
				}
			}
		}
		return nuevos;
	}
}