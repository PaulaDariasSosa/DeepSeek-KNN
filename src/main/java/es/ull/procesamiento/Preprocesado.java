package procesamiento;
import java.util.List;

import datos.Atributo;
import datos.Dataset;

/**
 * @brief Interfaz para operaciones de preprocesamiento de datos
 *
 * Esta interfaz define el contrato para las clases que implementen
 * diferentes algoritmos de preprocesamiento de datasets en el contexto
 * de aprendizaje automático.
 */
public interface Preprocesado {

	/**
	 * @brief Método principal para procesar un dataset
	 * @param datos Dataset a procesar
	 * @return Lista de atributos procesados
	 *
	 * Las clases que implementen esta interfaz deben proporcionar
	 * la lógica concreta de preprocesamiento, que puede incluir:
	 * - Normalización de datos
	 * - Estandarización
	 * - Manejo de valores faltantes
	 * - Reducción de dimensionalidad
	 * - Otros tratamientos específicos
	 */
	public List<Atributo> procesar(Dataset datos);
}