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

public class Entrenamiento {
	private Dataset train;
	private Dataset test;
	private List<String> clases;
	
	public Entrenamiento() {
	}
	
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
	
	public void generarMatriz(int valorK) {
		Dataset pruebas = new Dataset(test);
		Matriz confusion = new Matriz (clases.size(), clases.size());
		for (int i = 0; i < pruebas.numeroCasos(); ++i) {
			ArrayList<Object> instance = new ArrayList<>();
			for (int j = 0; j < pruebas.numeroAtributos()-1; ++j) {
				instance.add(pruebas.getInstance(i).getValores().get(j));
			}
			Instancia nueva = new Instancia(instance);
			String clase = (new KNN(valorK).clasificar(train, nueva));
			confusion.set( clases.indexOf(test.getInstance(i).getClase()),clases.indexOf(clase),confusion.get(clases.indexOf(test.getInstance(i).getClase()),clases.indexOf(clase))+1);
		}
		Logger logger = LoggerFactory.getLogger(Entrenamiento.class);
		if (logger.isInfoEnabled()) {
			logger.info(String.valueOf(clases));
		}
		confusion.print();
	}
	
	public void write(String filename1, String filename2) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename1))) {
            train.write(filename1);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename2))) {
            test.write(filename2);
        }
    }
	
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
	 * Exporta los resultados de clasificación a un archivo CSV
	 * @param filename Nombre del archivo de salida
	 * @param valorK Número de vecinos usados
	 * @throws IOException Si ocurre un error al escribir el archivo
	 */
	public void exportarResultados(String filename, int valorK) throws IOException {
		if (test == null || test.numeroCasos() == 0) {
			throw new IllegalStateException("No hay datos de prueba para exportar");
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			// Escribir encabezado
			writer.write("Instancia,Clase Real,Clase Predicha,Correcto\n");

			// Escribir cada resultado
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

			// Calcular y escribir precisión global
			double precision = calcularPrecision(valorK);
			writer.write("\nPrecision Global," + precision + "%");
		}
	}

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


	
}
