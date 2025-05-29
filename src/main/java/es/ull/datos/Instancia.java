package datos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vectores.Vector;

/**
 * @brief Clase que representa una instancia (punto de datos) con múltiples valores y una etiqueta de clase
 *
 * Esta clase almacena una lista de valores donde el último elemento se considera la etiqueta de clase.
 * Proporciona métodos para normalización, estandarización y conversión a formato vectorial.
 */
public class Instancia {
	private List<Object> valores;

	/**
	 * @brief Constructor por defecto que crea una instancia vacía
	 */
	public Instancia(){
		this.valores = new ArrayList<Object>();
	}

	/**
	 * @brief Constructor que crea una instancia a partir de una lista de valores
	 * @param nuevos Lista de valores (el último elemento debe ser la etiqueta de clase)
	 */
	public Instancia(List<Object> nuevos){
		this.valores = new ArrayList<>(nuevos); // Crear nueva lista mutable
	}

	/**
	 * @brief Constructor que crea una instancia a partir de una cadena separada por comas
	 * @param nuevos Cadena separada por comas de valores (el último elemento es la clase)
	 */
	public Instancia(String nuevos){
		String[] subcadenas = nuevos.split(",");
		ArrayList<Object> arrayList = new ArrayList<>(Arrays.asList(subcadenas));
		this.valores = arrayList;
	}

	/**
	 * @brief Obtiene todos los valores incluyendo la etiqueta de clase
	 * @return Lista con todos los valores
	 */
	public List<Object> getValores() {
		return this.valores;
	}

	/**
	 * @brief Representación en cadena de la instancia
	 * @return Cadena que contiene todos los valores
	 */
	public String toString() {
		return valores.toString();
	}

	/**
	 * @brief Convierte la instancia a un Vector (excluyendo la etiqueta de clase)
	 * @return Vector que contiene solo los valores numéricos
	 */
	public Vector getVector() {
		Vector aux = new Vector();
		for (int i = 0; i < valores.size()-1; ++i) {
			Object valor = valores.get(i);
			if (valor instanceof Number) {
				aux.add(((Number)valor).doubleValue());
			}
		}
		return aux;
	}

	/**
	 * @brief Obtiene la etiqueta de clase de la instancia
	 * @return La etiqueta de clase (último elemento en valores)
	 */
	public String getClase() {
		return (String) this.valores.get(valores.size()-1);
	}

	/**
	 * @brief Normaliza los valores numéricos (escalado min-max al rango [0,1])
	 *
	 * Preserva la etiqueta de clase mientras normaliza los demás valores.
	 * Solo afecta a valores numéricos.
	 */
	public void normalizar() {
		Vector aux = this.getVector();
		aux.normalize();
		ArrayList<Object> arrayListObject = new ArrayList<>();
		for (Double d : aux.getValores()) {
			arrayListObject.add(d);
		}
		// Preservar la clase
		if (!valores.isEmpty()) {
			arrayListObject.add(valores.get(valores.size()-1));
		}
		this.valores = arrayListObject;
	}

	/**
	 * @brief Estandariza los valores numéricos (normalización z-score)
	 *
	 * Transforma los valores para tener media=0 y desviación estándar=1.
	 * Preserva la etiqueta de clase mientras estandariza los demás valores.
	 * Solo afecta a valores numéricos.
	 */
	public void estandarizar() {
		Vector aux = this.getVector();
		double media = aux.avg(); // Usar método existente de Vector
		double desviacion = 0.0;

		for (int i = 0; i < aux.size(); ++i) {
			desviacion += Math.pow(aux.get(i) - media, 2);
		}
		desviacion = Math.sqrt(desviacion/aux.size());

		for (int i = 0; i < aux.size(); ++i) {
			aux.set(i, (aux.get(i)-media)/desviacion);
		}

		ArrayList<Object> arrayListObject = new ArrayList<>();
		for (Double d : aux.getValores()) {
			arrayListObject.add(d);
		}
		// Preservar la clase
		if (!valores.isEmpty()) {
			arrayListObject.add(valores.get(valores.size()-1));
		}
		this.valores = arrayListObject;
	}

	/**
	 * @brief Elimina la etiqueta de clase de la instancia
	 */
	public void deleteClase() {
		valores.remove(valores.size() - 1);
	}
}