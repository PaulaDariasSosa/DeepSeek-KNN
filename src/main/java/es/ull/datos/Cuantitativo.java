package datos;

import vectores.Vector;

/**
 * @brief Clase que representa un atributo cuantitativo (numérico)
 *
 * Hereda de la clase Atributo y almacena valores numéricos en un Vector.
 * Proporciona operaciones estadísticas básicas como cálculo de mínimo, máximo,
 * media, desviación estándar y estandarización de valores.
 */
public class Cuantitativo extends Atributo {
	private Vector valores;

	/**
	 * @brief Constructor por defecto que crea un atributo cuantitativo vacío
	 */
	public Cuantitativo() {
		this.nombre = "";
		this.valores = new Vector();
	}

	/**
	 * @brief Constructor que crea un atributo cuantitativo con nombre
	 * @param name Nombre del atributo
	 */
	public Cuantitativo(String name) {
		this();
		this.nombre = name;
	}

	/**
	 * @brief Constructor que crea un atributo con nombre y un valor inicial
	 * @param name Nombre del atributo
	 * @param valor Valor numérico inicial
	 */
	public Cuantitativo(String name, Double valor) {
		this();
		this.nombre = name;
		valores.add(valor);
	}

	/**
	 * @brief Constructor que crea un atributo con nombre y un Vector de valores
	 * @param name Nombre del atributo
	 * @param valor Vector con valores iniciales
	 */
	public Cuantitativo(String name, Vector valor) {
		this();
		this.nombre = name;
		this.valores = valor;
	}

	/**
	 * @brief Obtiene los valores del atributo como Vector
	 * @return Vector con los valores numéricos del atributo
	 */
	public Vector getValores() {
		return this.valores;
	}

	/**
	 * @brief Establece nuevos valores para el atributo
	 * @param nuevos Vector con los nuevos valores numéricos
	 */
	public void setValores(Vector nuevos) {
		this.valores = nuevos;
	}

	/**
	 * @brief Calcula el valor mínimo de los valores del atributo
	 * @return Valor mínimo del conjunto de valores
	 */
	public double minimo() {
		return valores.getMin();
	}

	/**
	 * @brief Calcula el valor máximo de los valores del atributo
	 * @return Valor máximo del conjunto de valores
	 */
	public double maximo() {
		return valores.getMax();
	}

	/**
	 * @brief Calcula la media aritmética de los valores
	 * @return Media de los valores del atributo
	 */
	public double media() {
		return valores.avg();
	}

	/**
	 * @brief Calcula la desviación estándar muestral de los valores
	 * @return Desviación estándar de los valores
	 */
	public double desviacion() {
		if (valores.size() <= 1) return 0.0;

		double media = this.media();
		double sumaCuadrados = 0.0;

		for (int i = 0; i < valores.size(); i++) {
			sumaCuadrados += Math.pow(valores.get(i) - media, 2);
		}

		return Math.sqrt(sumaCuadrados / (valores.size() - 1));
	}

	/**
	 * @brief Estandariza los valores (transformación z-score)
	 *
	 * Transforma los valores restando la media y dividiendo por la desviación estándar.
	 * El resultado son valores con media 0 y desviación estándar 1.
	 */
	public void estandarizacion() {
		if (valores.size() <= 1) return;

		double media = this.media();
		double desviacion = this.desviacion();

		for (int i = 0; i < valores.size(); i++) {
			double valorEstandarizado = (valores.get(i) - media) / desviacion;
			valores.set(i, valorEstandarizado);
		}
	}

	/**
	 * @brief Añade un nuevo valor numérico al atributo
	 * @param valor Valor a añadir (debe ser Number)
	 * @throws IllegalArgumentException Si el valor no es numérico
	 */
	@Override
	public void add(Object valor) {
		if (valor instanceof Number) {
			valores.add(((Number)valor).doubleValue());
		} else {
			throw new IllegalArgumentException("El valor debe ser numérico");
		}
	}

	/**
	 * @brief Obtiene el número de valores del atributo
	 * @return Cantidad de valores almacenados
	 */
	public int size() {
		return this.valores.size();
	}

	/**
	 * @brief Obtiene un valor específico del atributo
	 * @param i Índice del valor a obtener
	 * @return Valor numérico en la posición especificada
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
	 * @return String con la representación de los valores
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
	 * @return Nueva instancia de Cuantitativo con los mismos valores
	 */
	@Override
	public Cuantitativo copiar() {
		return new Cuantitativo(this.nombre, this.valores.copiar());
	}
}