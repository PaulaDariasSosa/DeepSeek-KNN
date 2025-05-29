package datos;

/**
 * @brief Clase abstracta que representa un atributo genérico con nombre y peso
 *
 * Esta clase abstracta define la estructura básica para atributos que pueden ser utilizados
 * en algoritmos de aprendizaje automático, donde cada atributo puede tener un peso asociado
 * y un conjunto de valores. Las clases concretas deben implementar los métodos abstractos
 * para manejar los valores específicos del atributo.
 */
public abstract class Atributo {
	protected double peso = 1;
	protected String nombre;

	/**
	 * @brief Método abstracto para obtener los valores del atributo
	 * @return Objeto que contiene los valores del atributo
	 */
	public abstract Object getValores();

	/**
	 * @brief Obtiene el nombre del atributo
	 * @return Nombre del atributo
	 */
	public String getNombre() {
		return this.nombre;
	}

	/**
	 * @brief Obtiene el peso del atributo
	 * @return Peso del atributo
	 */
	public double getPeso() {
		return this.peso;
	}

	/**
	 * @brief Establece un nuevo nombre para el atributo
	 * @param nuevo Nuevo nombre a asignar
	 */
	public void setNombre(String nuevo) {
		this.nombre = nuevo;
	}

	/**
	 * @brief Establece un nuevo peso para el atributo
	 * @param nuevo Nuevo peso a asignar
	 */
	public void setPeso(double nuevo) {
		this.peso = nuevo;
	}

	/**
	 * @brief Obtiene una representación básica del atributo
	 * @return Cadena con el nombre y peso del atributo
	 */
	public String get() {
		return (this.nombre + ": " + this.peso);
	}

	/**
	 * @brief Método abstracto para obtener el número de valores del atributo
	 * @return Número de valores del atributo
	 */
	public abstract int size();

	/**
	 * @brief Método abstracto para añadir un nuevo valor al atributo
	 * @param valor Valor a añadir
	 */
	public abstract void add(Object valor);

	/**
	 * @brief Método abstracto para eliminar un valor del atributo
	 * @param indice Índice del valor a eliminar
	 */
	public abstract void delete(int indice);

	/**
	 * @brief Método abstracto para obtener un valor específico del atributo
	 * @param i Índice del valor a obtener
	 * @return Valor en la posición especificada
	 */
	public abstract Object getValor(int i);

	/**
	 * @brief Método abstracto para obtener una representación en cadena del atributo
	 * @return Representación en cadena del atributo
	 */
	public abstract String toString();

	/**
	 * @brief Método abstracto para limpiar todos los valores del atributo
	 */
	public abstract void clear();

	/**
	 * @brief Método abstracto para crear una copia del atributo
	 * @return Nueva instancia con los mismos valores que el atributo actual
	 */
	public abstract Atributo copiar();
}