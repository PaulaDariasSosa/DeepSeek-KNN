package entrenamiento;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import clasificacion.KNN;
import datos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vectores.Matriz;

/**
 * @brief Clase para manejar el entrenamiento y evaluación de modelos KNN
 *
 * Esta clase se encarga de:
 * - Dividir datasets en conjuntos de entrenamiento y prueba
 * - Evaluar el rendimiento del clasificador KNN
 * - Generar matrices de confusión
 * - Exportar resultados de clasificación
 */
public class Entrenamiento {
	private Dataset train;
	private Dataset test;
	private List<String> clases;

	/**
	 * @brief Constructor por defecto
	 */
	public Entrenamiento() {
	}

	/**
	 * @brief Constructor que divide el dataset secuencialmente
	 * @param datos Dataset completo a dividir
	 * @param porcentaje Porcentaje para entrenamiento (0-1)
	 */
	public Entrenamiento(Dataset datos, double porcentaje) {
		Dataset trainset = new Dataset(datos.getAtributosEmpty());
		Dataset testset = new Dataset(datos.getAtributosEmpty());
		clases = datos.getClases();
		int indice = 0;
		while(indice < datos.numeroCasos()*porcentaje) {
			trainset.add(datos.getInstance(indice));
			indice += 1;
		}
		for (int i = indice; i < datos.numeroCasos(); ++i) {
			testset.add(datos.getInstance(i));
		}
		this.test = testset;
		this.train = trainset;
		this.test.setPreprocesado(datos.getPreprocesado());
		this.train.setPreprocesado(datos.getPreprocesado());
	}

	/**
	 * @brief Constructor que divide el dataset aleatoriamente
	 * @param datos Dataset completo a dividir
	 * @param porcentaje Porcentaje para entrenamiento (0-1)
	 * @param semilla Semilla para reproducibilidad
	 */
	public Entrenamiento(Dataset datos, double porcentaje, int semilla) {
		Dataset trainset = new Dataset(datos.getAtributosEmpty());
		Dataset testset = new Dataset(datos.getAtributosEmpty());
		clases = datos.getClases();
		ArrayList<Integer> indices = new ArrayList<>();
		@SuppressWarnings("squid:S2245")
		Random random = new Random(semilla);
		while(indices.size() < datos.numeroCasos()*porcentaje) {
			int randomNumber = random.nextInt(datos.numeroCasos());
			if (!indices.contains(randomNumber)) {
				trainset.add(datos.getInstance(randomNumber));
				indices.add(randomNumber);
			}
		}
		for (int i = 0; i < datos.numeroCasos(); ++i) {
			if (!indices.contains(i)) {
				testset.add(datos.getInstance(i));
			}
		}
		this.test = testset;
		this.train =  trainset;
		this.test.setPreprocesado(datos.getPreprocesado());
		this.train.setPreprocesado(datos.getPreprocesado());
	}

	/**
	 * @brief Genera y evalúa predicciones usando KNN
	 * @param valorK Número de vecinos a considerar
	 * @param outputPath Ruta para guardar resultados
	 */
	public void generarPrediccion(int valorK, String outputPath) {
		Dataset pruebas = new Dataset(test);
		Double aciertos = 0.0;
		for (int i = 0; i < pruebas.numeroCasos(); ++i) {
			ArrayList<Object> instance = new ArrayList<>();
			for (int j = 0; j < pruebas.numeroAtributos()-1; ++j) {
				instance.add(pruebas.getInstance(i).getValores().get(j));
			}
			Instancia nueva = new Instancia(instance);
			String clase = (new KNN(valorK).clasificar(train, nueva));
			if (clase.equals(test.getInstance(i).getClase())) aciertos += 1;
		}
		Logger logger = LoggerFactory.getLogger(Entrenamiento.class);
		if (logger.isInfoEnabled()) {
			double precision = (aciertos / test.numeroCasos()) * 100;
			logger.info("La precisión predictiva: {} / {} = {}%", aciertos, test.numeroCasos(), precision);
		}
		try {
			exportarResultados(outputPath, valorK);
			logger.info("Resultados exportados exitosamente a {}", outputPath);
		} catch (IOException e) {
			logger.error("Error al exportar resultados: {}", e.getMessage());
		}
	}

	/**
	 * @brief Genera y muestra una matriz de confusión
	 * @param valorK Número de vecinos a considerar
	 */
	public void generarMatriz(int valorK) {
		Dataset pruebas = new Dataset(test);
		Matriz confusion = new Matriz(clases.size(), clases.size());
		for (int i = 0; i < pruebas.numeroCasos(); ++i) {
			ArrayList<Object> instance = new ArrayList<>();
			for (int j = 0; j < pruebas.numeroAtributos()-1; ++j) {
				instance.add(pruebas.getInstance(i).getValores().get(j));
			}
			Instancia nueva = new Instancia(instance);
			String clase = (new KNN(valorK).clasificar(train, nueva));
			confusion.set(
					clases.indexOf(test.getInstance(i).getClase()),
					clases.indexOf(clase),
					confusion.get(clases.indexOf(test.getInstance(i).getClase()), clases.indexOf(clase))+1
			);
		}
		Logger logger = LoggerFactory.getLogger(Entrenamiento.class);
		if (logger.isInfoEnabled()) {
			logger.info(String.valueOf(clases));
		}
		confusion.print();
	}

	/**
	 * @brief Escribe los datasets a archivos
	 * @param filename1 Archivo para datos de entrenamiento
	 * @param filename2 Archivo para datos de prueba
	 * @throws IOException Si ocurre error de escritura
	 */
	public void write(String filename1, String filename2) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename1))) {
			train.write(filename1);
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename2))) {
			test.write(filename2);
		}
	}

	/**
	 * @brief Lee datasets desde archivos
	 * @param filename1 Archivo con datos de entrenamiento
	 * @param filename2 Archivo con datos de prueba
	 * @throws IOException Si ocurre error de lectura
	 */
	public void read(String filename1, String filename2) throws IOException {
		train = new Dataset(filename1);
		test = new Dataset(filename2);
		List<String> clasesA = train.getClases();
		List<String> clasesB = test.getClases();
		for (int i = 0; i < clasesB.size(); i++) {
			if (!clasesA.contains(clasesB.get(i))) clasesA.add(clasesB.get(i));
		}
		clases = clasesA;
	}

	/**
	 * @brief Exporta resultados de clasificación a archivo CSV
	 * @param filename Ruta del archivo de salida
	 * @param valorK Número de vecinos usados
	 * @throws IOException Si ocurre error de escritura
	 * @throws IllegalStateException Si no hay datos de prueba
	 */
	public void exportarResultados(String filename, int valorK) throws IOException {
		if (test == null || test.numeroCasos() == 0) {
			throw new IllegalStateException("No hay datos de prueba para exportar");
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write("Instancia,Clase Real,Clase Predicha,Correcto\n");

			for (int i = 0; i < test.numeroCasos(); i++) {
				Instancia instancia = test.getInstance(i);
				String claseReal = instancia.getClase();

				ArrayList<Object> valores = new ArrayList<>();
				for (int j = 0; j < test.numeroAtributos()-1; j++) {
					valores.add(instancia.getValores().get(j));
				}
				Instancia sinClase = new Instancia(valores);

				String clasePredicha = new KNN(valorK).clasificar(train, sinClase);
				boolean correcto = claseReal.equals(clasePredicha);

				writer.write(String.format("%d,%s,%s,%b%n",
						i, claseReal, clasePredicha, correcto));
			}

			double precision = calcularPrecision(valorK);
			writer.write("\nPrecision Global," + precision + "%");
		}
	}

	/**
	 * @brief Calcula la precisión del clasificador
	 * @param valorK Número de vecinos usados
	 * @return Precisión en porcentaje
	 */
	private double calcularPrecision(int valorK) {
		int aciertos = 0;
		for (int i = 0; i < test.numeroCasos(); i++) {
			Instancia instancia = test.getInstance(i);
			ArrayList<Object> valores = new ArrayList<>();
			for (int j = 0; j < test.numeroAtributos()-1; j++) {
				valores.add(instancia.getValores().get(j));
			}
			String clasePredicha = new KNN(valorK).clasificar(train, new Instancia(valores));
			if (instancia.getClase().equals(clasePredicha)) {
				aciertos++;
			}
		}
		return (aciertos * 100.0) / test.numeroCasos();
	}

	/**
	 * @brief Obtiene el dataset de entrenamiento
	 * @return Dataset de entrenamiento
	 */
	public Dataset getTrainDataset() {
		return this.train;
	}

	/**
	 * @brief Obtiene el dataset de prueba
	 * @return Dataset de prueba
	 */
	public Dataset getTestDataset() {
		return this.test;
	}

	/**
	 * @brief Obtiene la lista de clases únicas
	 * @return Lista de nombres de clases
	 */
	public List<String> getClases() {
		return this.clases;
	}
}