package procesamiento;

import java.util.List;
import datos.*;

/**
 * @brief Implementación de Preprocesado que no aplica transformaciones
 *
 * Esta clase representa una operación de preprocesamiento nula, donde
 * los datos se devuelven en su forma original sin ninguna modificación.
 * Útil como patrón Null Object o para casos donde no se requiere preprocesamiento.
 */
public class DatosCrudos implements Preprocesado {

	/**
	 * @brief Devuelve los atributos del dataset sin modificaciones
	 * @param datos Dataset de entrada
	 * @return Lista de atributos sin procesar (idénticos a los de entrada)
	 *
	 * Este método implementa la operación de preprocesamiento pero
	 * específicamente no realiza ninguna transformación sobre los datos,
	 * manteniéndolos en su estado original.
	 */
	public List<Atributo> procesar(Dataset datos) {
		return datos.getAtributos();
	}
}