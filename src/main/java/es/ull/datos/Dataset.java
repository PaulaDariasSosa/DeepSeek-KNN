package datos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @brief Clase que representa un conjunto de datos para aprendizaje automático
 *
 * Esta clase almacena y gestiona un conjunto de atributos (características)
 * que pueden ser cualitativos o cuantitativos, permitiendo diversas operaciones
 * como lectura/escritura de archivos, manipulación de instancias y preprocesamiento.
 */
public class Dataset {
	private List<Atributo> atributos;
	int preprocesado;

	/**
	 * @brief Constructor por defecto que crea un dataset vacío
	 */
	public Dataset() {
		this.atributos = new ArrayList<Atributo>();
	}

	/**
	 * @brief Constructor que crea un dataset a partir de una lista de atributos
	 * @param nuevos Lista de atributos para inicializar el dataset
	 */
	public Dataset(List<Atributo> nuevos) {
		this();
		this.atributos = nuevos;
	}

	/**
	 * @brief Constructor que lee un dataset desde un archivo
	 * @param filename Ruta del archivo a leer
	 * @throws IOException Si ocurre un error de lectura del archivo
	 */
	public Dataset(String filename) throws IOException {
		this();
		this.read(filename);
	}

	/**
	 * @brief Constructor copia
	 * @param datos Dataset a copiar
	 */
	public Dataset(Dataset datos) {
		this();
		this.atributos = new ArrayList<>(datos.atributos);
	}

	/**
	 * @brief Cambia los pesos de todos los atributos
	 * @param pesos Lista de nuevos pesos (como String)
	 * @throws IllegalArgumentException Si el número de pesos no coincide con los atributos
	 */
	public void cambiarPeso(List<String> pesos) {
		if (pesos.size() != atributos.size()) {
			throw new IllegalArgumentException("El número de pesos para asignar debe ser igual al número de atributos");
		}
		for (int i = 0; i < pesos.size(); i++) {
			Atributo aux = atributos.get(i);
			aux.setPeso(Double.parseDouble(pesos.get(i)));
			this.atributos.set(i, aux);
		}
	}

	/**
	 * @brief Cambia el peso de un atributo específico
	 * @param index Índice del atributo a modificar
	 * @param peso Nuevo peso a asignar
	 */
	public void cambiarPeso(int index, double peso) {
		Atributo aux = this.atributos.get(index);
		aux.setPeso(peso);
		this.atributos.set(index, aux);
	}

	/**
	 * @brief Cambia todos los pesos al mismo valor
	 * @param peso Nuevo peso para todos los atributos
	 * @throws IllegalArgumentException Si el peso no está entre 0 y 1
	 */
	public void cambiarPeso(double peso) {
		if (peso < 0 || peso > 1) {
			throw new IllegalArgumentException("El peso debe estar entre 0 y 1.");
		}

		for (Atributo atributo : atributos) {
			atributo.setPeso(peso);
		}
	}

	/**
	 * @brief Imprime el dataset usando el logger
	 */
	public void print() {
		Logger logger = LoggerFactory.getLogger(Dataset.class.getName());
		if (logger.isInfoEnabled()) {
			logger.info(this.toString());
		}
	}

	/**
	 * @brief Representación en cadena del dataset
	 * @return String con los valores del dataset en formato CSV
	 */
	public String toString() {
		StringBuilder data = new StringBuilder();
		List<String> valores = this.nombreAtributos();
		valores.addAll(this.getValores());
		int contador = 1;
		for (int i = 0; i < valores.size(); ++i) {
			data.append(valores.get(i));
			if (contador == this.numeroAtributos()) {
				data.append("\n");
				contador = 1;
			} else {
				data.append(",");
				++contador;
			}
		}
		return data.toString();
	}

	/**
	 * @brief Añade una nueva instancia al dataset
	 * @param nueva Instancia a añadir
	 */
	public void add(Instancia nueva) {
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux =  atributos.get(i);
			aux.add(nueva.getValores().get(i));
			atributos.set(i, aux);
		}
	}

	/**
	 * @brief Añade una nueva instancia representada como lista de Strings
	 * @param nueva Lista de valores de la instancia
	 * @throws IllegalArgumentException Si la instancia no es válida
	 */
	public void add(List<String> nueva) {
		validateNewInstance(nueva);

		try {
			addAttributes(nueva);
		} catch (Exception e) {
			rollbackChanges();
			throw e;
		}
	}

	private void validateNewInstance(List<String> nueva) {
		if (nueva == null || nueva.isEmpty()) {
			throw new IllegalArgumentException("La instancia no puede ser nula o vacía");
		}
		if (nueva.size() != numeroAtributos()) {
			throw new IllegalArgumentException(
					String.format("Se esperaban %d atributos, se recibieron %d",
							numeroAtributos(), nueva.size())
			);
		}
	}

	private void addAttributes(List<String> nueva) {
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux = atributos.get(i);
			String valor = nueva.get(i);
			addAttributeValue(aux, valor);
		}
	}

	private void addAttributeValue(Atributo atributo, String valor) {
		if (atributo instanceof Cuantitativo) {
			addQuantitativeValue((Cuantitativo) atributo, valor);
		} else {
			atributo.add(valor);
		}
	}

	private void addQuantitativeValue(Cuantitativo atributo, String valor) {
		try {
			atributo.add(Double.parseDouble(valor));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					String.format("Valor no numérico para atributo cuantitativo %s: %s",
							atributo.getNombre(), valor), e);
		}
	}

	private void rollbackChanges() {
		for (Atributo attr : atributos) {
			if (attr.size() > 0) {
				attr.delete(attr.size() - 1);
			}
		}
	}

	/**
	 * @brief Elimina una instancia del dataset
	 * @param index Índice de la instancia a eliminar
	 * @throws DatasetOperationException Si ocurre un error durante la operación
	 */
	public void delete(int index) throws DatasetOperationException {
		validateDeleteOperation(index);

		try {
			deleteInstanceFromAttributes(index);
		} catch (Exception e) {
			throw new DatasetOperationException("Error al eliminar instancia en índice: " + index, e);
		}
	}

	private void validateDeleteOperation(int index) {
		if (atributos.isEmpty()) {
			throw new DatasetOperationException("No se puede eliminar: dataset vacío");
		}
		if (index < 0 || index >= numeroCasos()) {
			throw new DatasetOperationException(
					String.format("Índice %d fuera de rango válido (0-%d)",
							index, numeroCasos()-1)
			);
		}
	}

	private void deleteInstanceFromAttributes(int index) {
		for (Atributo atributo : atributos) {
			atributo.delete(index);
		}
	}

	/**
	 * @brief Clase de excepción para operaciones con datasets
	 */
	public class DatasetOperationException extends RuntimeException {
		public DatasetOperationException(String message) {
			super(message);
		}

		public DatasetOperationException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * @brief Escribe el dataset en un archivo CSV
	 * @param filename Ruta del archivo a escribir
	 * @throws IOException Si ocurre un error de escritura
	 */
	public void write(String filename) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write(this.toString());
		}
	}

	/**
	 * @brief Lee un dataset desde un archivo CSV
	 * @param filename Ruta del archivo a leer
	 * @throws IOException Si ocurre un error de lectura
	 */
	public void read(String filename) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String[] attributeNamesArray = reader.readLine().split(",");
			String line;
			if ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				for (int i = 0; i < attributeNamesArray.length ; ++i) {
					try {
						this.atributos.add(new Cuantitativo(attributeNamesArray[i], Double.parseDouble(values[i])));
					} catch (NumberFormatException e) {
						this.atributos.add(new Cualitativo(attributeNamesArray[i], values[i]));
					}
				}
			}
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				for (int i = 0; i < attributeNamesArray.length ; ++i) {
					Atributo nuevo = this.atributos.get(i);
					try {
						nuevo.add(Double.parseDouble(values[i]));
					} catch (NumberFormatException e) {
						nuevo.add(values[i]);
					}
					this.atributos.set(i, nuevo);
				}
			}
		}
	}

	/**
	 * @brief Obtiene el número de atributos del dataset
	 * @return Número de atributos
	 */
	public int numeroAtributos() {
		return atributos.size();
	}

	/**
	 * @brief Obtiene los nombres de los atributos
	 * @return Lista de nombres de atributos
	 */
	public List<String> nombreAtributos(){
		ArrayList<String> nombres = new ArrayList<>();
		for(int i = 0; i < atributos.size(); ++i) nombres.add(atributos.get(i).getNombre());
		return nombres;
	}

	/**
	 * @brief Obtiene la lista de atributos
	 * @return Lista de atributos
	 */
	public List<Atributo> getAtributos(){
		return atributos;
	}

	/**
	 * @brief Obtiene una copia vacía de los atributos (sin valores)
	 * @return Lista de atributos vacíos
	 */
	public List<Atributo> getAtributosEmpty() {
		List<Atributo> aux = new ArrayList<Atributo> (atributos.size());
		for (int i = 0; i < atributos.size(); ++i) {
			try {
				Cualitativo auxiliar = (Cualitativo) atributos.get(i);
				aux.add(new Cualitativo(auxiliar.getNombre()));
			} catch (ClassCastException e) {
				Cuantitativo auxiliar = (Cuantitativo) atributos.get(i);
				aux.add(new Cuantitativo(auxiliar.getNombre()));
			}
		}
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo prov = aux.get(i);
			prov.setPeso(atributos.get(i).getPeso());
			aux.set(i, prov);
		}
		return aux;
	}

	/**
	 * @brief Obtiene el número de instancias en el dataset
	 * @return Número de instancias/casos
	 */
	public int numeroCasos() {
		if (atributos.isEmpty()) {
			return 0;
		}
		return atributos.get(0).size();
	}

	/**
	 * @brief Obtiene todos los valores del dataset como Strings
	 * @return Lista de valores del dataset
	 */
	public List<String> getValores() {
		if (atributos.isEmpty()) {
			return Collections.emptyList();
		}
		ArrayList<String> valores = new ArrayList<String>();
		for (int i = 0; i < atributos.get(0).size(); ++i) {
			for (int j = 0; j < atributos.size(); ++j) valores.add(String.valueOf(atributos.get(j).getValor(i)));
		}
		return valores;
	}

	/**
	 * @brief Obtiene un atributo por su índice
	 * @param index Índice del atributo
	 * @return Atributo solicitado
	 */
	public Atributo get(int index) {
		return atributos.get(index);
	}

	/**
	 * @brief Obtiene una instancia específica del dataset
	 * @param index Índice de la instancia
	 * @return Instancia solicitada
	 * @throws IllegalStateException Si el dataset está vacío
	 * @throws IndexOutOfBoundsException Si el índice está fuera de rango
	 */
	public Instancia getInstance(int index) {
		if (atributos.isEmpty()) {
			throw new IllegalStateException("El dataset está vacío. No se puede obtener instancias.");
		}

		if (index < 0 || index >= numeroCasos()) {
			throw new IndexOutOfBoundsException(
					String.format("Índice %d fuera de rango. El dataset contiene %d instancias.", index, numeroCasos())
			);
		}

		ArrayList<Object> auxiliar = new ArrayList<>();
		for (Atributo atributo : atributos) {
			auxiliar.add(atributo.getValor(index));
		}
		return new Instancia(auxiliar);
	}

	/**
	 * @brief Obtiene los pesos de los atributos como Strings
	 * @return Lista de pesos en formato String
	 */
	public List<String> getPesos() {
		ArrayList<String> valores = new ArrayList<String>();
		for (Atributo valor : this.atributos) valores.add(valor.get());
		return valores;
	}

	/**
	 * @brief Obtiene las clases únicas del dataset (del último atributo)
	 * @return Lista de clases únicas
	 */
	public List<String> getClases() {
		return ((Cualitativo) this.atributos.get(atributos.size()-1)).clases();
	}

	/**
	 * @brief Obtiene el tipo de preprocesado aplicado
	 * @return Entero que indica el tipo de preprocesado
	 */
	public int getPreprocesado() {
		return preprocesado;
	}

	/**
	 * @brief Establece el tipo de preprocesado
	 * @param opcion Tipo de preprocesado a aplicar
	 */
	public void setPreprocesado(int opcion) {
		this.preprocesado = opcion;
	}

	/**
	 * @brief Establece la lista de atributos
	 * @param nuevos Nueva lista de atributos
	 */
	public void setAtributos(List<Atributo> nuevos) {
		this.atributos = nuevos;
	}

	/**
	 * @brief Crea una copia profunda del dataset
	 * @return Nueva instancia de Dataset con los mismos datos
	 */
	public Dataset copiar() {
		Dataset copia = new Dataset();
		ArrayList<Atributo> copiaAtributos = new ArrayList<>();
		for (Atributo atributo : this.atributos) {
			copiaAtributos.add(atributo.copiar());
		}
		copia.setAtributos(copiaAtributos);
		return copia;
	}
}