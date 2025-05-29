package datos;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Clase que representa un atributo cualitativo (categórico)
 *
 * Hereda de la clase Atributo y almacena valores de tipo String.
 * Proporciona funcionalidades para manejar valores categóricos como
 * cálculo de frecuencias, clases únicas, entre otros.
 */
public class Cualitativo extends Atributo {
	private List<String> valores;

	/**
	 * @brief Constructor por defecto que crea un atributo cualitativo vacío
	 */
	public Cualitativo() {
		this.nombre = "";
		this.valores = new ArrayList<String>();
	}

	/**
	 * @brief Constructor que crea un atributo cualitativo con nombre
	 * @param name Nombre del atributo
	 */
	public Cualitativo(String name) {
		this();
		this.nombre = name;
	}

	/**
	 * @brief Constructor que crea un atributo con nombre y un valor inicial
	 * @param name Nombre del atributo
	 * @param valor Valor inicial del atributo
	 */
	public Cualitativo(String name, String valor) {
		this();
		this.nombre = name;
		valores.add(valor);
	}

	/**
	 * @brief Constructor que crea un atributo con nombre y una lista de valores
	 * @param name Nombre del atributo
	 * @param valor Lista de valores iniciales
	 */
	public Cualitativo(String name, List<String> valor) {
		this();
		this.nombre = name;
		this.valores = valor;
	}

	/**
	 * @brief Obtiene la lista de valores del atributo
	 * @return Lista de valores del atributo
	 */
	public List<String> getValores() {
		return this.valores;
	}

	/**
	 * @brief Establece una nueva lista de valores para el atributo
	 * @param nuevos Nueva lista de valores
	 */
	public void setValores(List<String> nuevos) {
		this.valores = nuevos;
	}

	/**
	 * @brief Obtiene las clases únicas (valores distintos) del atributo
	 * @return Lista de clases únicas presentes en los valores
	 */
	public List<String> clases() {
		ArrayList<String> clases = new ArrayList<>();
		for(int i = 0; i < this.valores.size(); ++i) {
			if(!clases.contains(this.valores.get(i))) clases.add(this.valores.get(i));
		}
		return clases;
	}

	/**
	 * @brief Obtiene el número de clases únicas del atributo
	 * @return Número de clases únicas
	 */
	public int nClases() {
		return this.clases().size();
	}

	/**
	 * @brief Calcula las frecuencias relativas de cada clase
	 * @return Lista de frecuencias relativas para cada clase única
	 */
	public List<Double> frecuencia() {
		List<String> clases = this.clases();
		ArrayList<Double> frecuencias = new ArrayList<>();
		for (int j = 0; j < this.nClases(); ++j) {
			double auxiliar = 0;
			for(int i = 0; i < this.valores.size(); ++i) {
				if(clases.get(j).equals(this.valores.get(i))) auxiliar++;
			}
			frecuencias.add(auxiliar/this.valores.size());
		}
		return frecuencias;
	}

	/**
	 * @brief Obtiene el número de valores del atributo
	 * @return Número de valores almacenados
	 */
	public int size() {
		return this.valores.size();
	}

	/**
	 * @brief Añade un nuevo valor al atributo
	 * @param valor Valor a añadir (debe ser String)
	 */
	@Override
	public void add(Object valor) {
		valores.add((String) valor);
	}

	/**
	 * @brief Obtiene un valor específico del atributo
	 * @param i Índice del valor a obtener
	 * @return Valor en la posición especificada
	 */
	@Override
	public Object getValor(int i) {
		return valores.get(i);
	}

	/**
	 * @brief Elimina un valor del atributo
	 * @param index Índice del valor a eliminar
	 */
	@Override
	public void delete(int index) {
		valores.remove(index);
	}

	/**
	 * @brief Representación en cadena de los valores del atributo
	 * @return Cadena que representa los valores del atributo
	 */
	@Override
	public String toString() {
		return valores.toString();
	}

	/**
	 * @brief Elimina todos los valores del atributo
	 */
	@Override
	public void clear() {
		valores.clear();
	}

	/**
	 * @brief Crea una copia independiente del atributo
	 * @return Nueva instancia de Cualitativo con los mismos valores
	 */
	@Override
	public Cualitativo copiar() {
		return new Cualitativo(this.nombre, new ArrayList<String>(this.valores));
	}
}