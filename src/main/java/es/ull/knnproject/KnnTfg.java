package knnproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clasificacion.KNN;
import datos.*;
import procesamiento.*;
import entrenamiento.*;

/**
 * @brief Clase principal que implementa la interfaz de usuario para el sistema KNN
 *
 * Proporciona un menú interactivo para:
 * - Cargar/guardar datasets
 * - Preprocesar datos
 * - Realizar clasificaciones
 * - Ejecutar experimentos
 * - Visualizar resultados
 */
public class KnnTfg  {
	/** @brief Logger para registro de eventos */
	private static final Logger logger = LoggerFactory.getLogger(KnnTfg.class);

	/** @brief Mensaje para solicitar valor de k */
	public static final String MENSAJE_INTRODUCIR_K = "Introduce el valor de k: ";

	/** @brief Mensaje para solicitar valores de instancia */
	public static final String MENSAJE_INTRODUCIR_VALORES = "Introduce los valores: ";

	/** @brief Mensaje para solicitar porcentaje de entrenamiento */
	public static final String MENSAJE_CONJUNTO_ENTRENAMIENTO = "Introduzca el porcentaje para el conjunto de entrenamiento";

	/** @brief Nombre del archivo de resultados por defecto */
	public static final String MENSAJE_RESULTADOS_TXT = "resultados.txt";

	/** @brief Mensaje genérico para selección de opciones */
	public static final String MENSAJE_SELECCIONE_OPCION =  "Seleccione una opción: ";

	/** @brief Mensaje para opciones inválidas */
	public static final String MENSAJE_OPCION_NO_VALIDA = "Opción no válida";

	/** @brief Mensaje para índices fuera de rango */
	public static final String MENSAJE_INDICE_FUERA_RANGO = "Índice fuera de rango. Debe estar entre 0 y {}";

	/** @brief Mensaje para entrada numérica inválida */
	public static final String MENSAJE_INGRESAR_NUMERO = "Debe ingresar un número entero válido";

	/**
	 * @brief Punto de entrada principal de la aplicación
	 * @param args Argumentos de línea de comandos (no utilizados)
	 */
	public static void main(String[] args) {
		AppContext context = new AppContext();
		Scanner scanner = new Scanner(System.in);

		while (!context.salida) {
			mostrarMenu();
			procesarOpcion(context, scanner);
		}

		scanner.close();
	}

	/**
	 * @brief Procesa la opción seleccionada del menú principal
	 * @param context Contexto de la aplicación
	 * @param scanner Objeto Scanner para entrada de usuario
	 * @throws IllegalArgumentException Si context o scanner son nulos
	 */
	static void procesarOpcion(AppContext context, Scanner scanner) {
		if (context == null || scanner == null) {
			throw new IllegalArgumentException("Contexto y scanner no pueden ser nulos");
		}

		try {
			if (!scanner.hasNextInt()) {
				logger.error("Entrada inválida. Debe ingresar un número.");
				scanner.nextLine(); // Limpiar entrada incorrecta
				return;
			}

			int opcion = scanner.nextInt();
			scanner.nextLine(); // Limpiar buffer

			switch (opcion) {
				case 1:
					cargarDataset(context, scanner);
					break;
				case 2:
					guardarDataset(context.datos, context.ruta);
					break;
				case 3:
					context.datos = modify(context.datos);
					break;
				case 4:
					info(context.datos, scanner);
					break;
				case 5:
					experimentar(context.datos);
					break;
				case 6:
					ejecutarKNN(context.datosCrudos, context.datos, scanner);
					break;
				case 7:
					context.salida = true;
					break;
				default:
					logger.warn("Opción no válida seleccionada: {}", opcion);
					// No salimos del programa, solo mostramos advertencia
			}
		} catch (InputMismatchException e) {
			logger.error("Entrada inválida. Debe ingresar un número.");
			scanner.nextLine(); // Limpiar entrada incorrecta
		} catch (IOException e) {
			logger.error("Error al guardar el dataset: {}", e.getMessage());
		} catch (Exception e) {
			logger.error("Error inesperado: {}", e.getMessage());
		}
	}

	/**
	 * @brief Carga un dataset desde archivo
	 * @param context Contexto de la aplicación
	 * @param scanner Scanner para entrada de usuario
	 */
	private static void cargarDataset(AppContext context, Scanner scanner) {
		logger.info("Introduzca la ruta completa del archivo: ");
		String filePath = scanner.nextLine();

		if (!validarArchivo(filePath)) {
			return;
		}

		try {
			Dataset[] datasets = cargarDataset(filePath);
			context.datosCrudos = datasets[0];
			context.datos = datasets[1];
			mostrarFeedback("Dataset cargado correctamente con " + context.datos.numeroCasos() + " instancias", true);
		} catch (IOException e) {
			mostrarFeedback("Error al cargar el archivo: " + e.getMessage(), false);
		} catch (Exception e) {
			logger.error("Formato de archivo incorrecto: {}", e.getMessage());
		}
	}

	/**
	 * @brief Valida que un archivo exista y sea legible
	 * @param filePath Ruta del archivo a validar
	 * @return true si el archivo es válido, false en caso contrario
	 */
	static boolean validarArchivo(String filePath) {
		File file = new File(filePath);

		if (!file.exists()) {
			logger.error("El archivo no existe: {}", filePath);
			return false;
		}

		if (!file.canRead()) {
			logger.error("No se puede leer el archivo: {}", filePath);
			return false;
		}

		if (!filePath.toLowerCase().endsWith(".csv")) {
			logger.warn("El archivo no tiene extensión .csv, puede que no funcione correctamente");
		}

		return true;
	}

	/**
	 * @brief Clase interna para mantener el estado global de la aplicación
	 */
	static class AppContext {
		String ruta = "";
		boolean salida = false;
		Dataset datosCrudos = new Dataset();
		Dataset datos = new Dataset();
	}

	/**
	 * @brief Muestra el menú principal en consola
	 */
	private static void mostrarMenu() {
		logger.info("\n=== MENÚ PRINCIPAL ===");
		logger.info(" 1. Cargar dataset");
		logger.info(" 2. Guardar dataset");
		logger.info(" 3. Modificar dataset");
		logger.info(" 4. Mostrar información");
		logger.info(" 5. Realizar experimentación");
		logger.info(" 6. Clasificar instancia");
		logger.info(" 7. Salir");
		logger.info(MENSAJE_SELECCIONE_OPCION);
	}

	/**
	 * @brief Muestra mensajes de feedback con colores
	 * @param mensaje Texto a mostrar
	 * @param exito true para mensaje exitoso (verde), false para error (rojo)
	 */
	private static void mostrarFeedback(String mensaje, boolean exito) {
		String color = exito ? "\033[32m" : "\033[31m"; // Verde o rojo
		if (logger.isInfoEnabled()) {
			logger.info("{}> {}\u001B[0m", color, mensaje);
		}
	}

	/**
	 * @brief Carga y preprocesa un dataset desde archivo
	 * @param filePath Ruta del archivo CSV
	 * @return Array con [dataset_crudo, dataset_preprocesado]
	 * @throws IOException Si hay errores de lectura
	 * @throws IllegalArgumentException Si el archivo está vacío
	 */
	private static Dataset[] cargarDataset(String filePath) throws IOException {
		File file = new File(filePath);

		// Verificación adicional
		if (file.length() == 0) {
			throw new IOException("El archivo está vacío");
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			// Verificar que tenga contenido válido
			String firstLine = reader.readLine();
			if (firstLine == null || firstLine.trim().isEmpty()) {
				throw new IOException("El archivo no contiene datos válidos");
			}

			Dataset datosCrudos = new Dataset(filePath);
			Dataset datos = new Dataset(filePath);
			datos = preprocesar(datos);
			return new Dataset[]{datosCrudos, datos};
		}
	}

	/**
	 * @brief Guarda el dataset en un archivo
	 * @param datos Dataset a guardar
	 * @param ruta Directorio de destino
	 * @throws IOException Si hay errores de escritura
	 */
	private static void guardarDataset(Dataset datos, String ruta) throws IOException {
		String archivo = readFile(ruta);
		datos.write(ruta + archivo);
	}

	/**
	 * @brief Ejecuta el algoritmo KNN para clasificar una instancia
	 * @param datosCrudos Dataset sin procesar
	 * @param datos Dataset preprocesado
	 * @param scanner Scanner para entrada de usuario
	 */
	static void ejecutarKNN(Dataset datosCrudos, Dataset datos, Scanner scanner) {
		if (datosCrudos.numeroCasos() == 0) {
			logger.error("No se puede clasificar: el dataset de entrenamiento está vacío");
			return;
		}
		logger.info(MENSAJE_INTRODUCIR_K);
		int k = scanner.nextInt();
		KNN intento = new KNN(k);

		logger.info(MENSAJE_INTRODUCIR_VALORES);
		Scanner scanner1 = new Scanner(System.in);
		String valoresString = scanner1.nextLine();
		String[] subcadenas = valoresString.split(",");
		ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(subcadenas));
		Instancia instance = new Instancia(valoresString);

		Dataset copiaCrudos = new Dataset(datosCrudos.copiar());
		if (datos.getPreprocesado() != 1) {
			arrayList.add("clase");
			copiaCrudos.add(arrayList);
			Preprocesado intento1 = new Normalizacion();
			if (datos.getPreprocesado() == 2) intento1 = new Normalizacion();
			if (datos.getPreprocesado() == 3) intento1 = new Estandarizacion();
			copiaCrudos = new Dataset(intento1.procesar(copiaCrudos));
			instance = copiaCrudos.getInstance(copiaCrudos.numeroCasos() - 1);
			copiaCrudos.delete(copiaCrudos.numeroCasos() - 1);
			instance.deleteClase();
		}

		if (logger.isInfoEnabled()) {
			logger.info("La clase elegida es: {}", intento.clasificar(copiaCrudos, instance));
		}
	}

	/**
	 * @brief Lee un archivo y maneja la interacción de usuario para rutas
	 * @param ruta Ruta base inicial
	 * @return Nombre del archivo seleccionado
	 */
	public static String readFile(String ruta) {
		FileReaderContext context = new FileReaderContext(ruta);
		Scanner scanner = new Scanner(System.in);

		while (!context.shouldExit()) {
			mostrarMenuArchivo();
			procesarOpcionArchivo(context, scanner);
		}

		return context.getArchivo();
	}

	/**
	 * @brief Clase de contexto para manejar estado durante la lectura de archivos
	 */
	private static class FileReaderContext {
		private String ruta;
		private String archivo = "";
		private boolean shouldExit = false;

		public FileReaderContext(String ruta) {
			this.ruta = ruta;
		}

		public boolean shouldExit() {
			return shouldExit;
		}

		public String getArchivo() {
			return archivo;
		}

		public void setArchivo(String archivo) {
			this.archivo = archivo;
		}

		public void setRuta(String ruta) {
			this.ruta = ruta;
		}

		public String getRuta() {
			return ruta;
		}

		public void exit() {
			this.shouldExit = true;
		}
	}

	/**
	 * @brief Muestra el menú de opciones para manejo de archivos
	 */
	private static void mostrarMenuArchivo() {
		logger.info("Se debe especificar la ruta y nombre del archivo: ");
		logger.info("       [1] Introducir nombre");
		logger.info("       [2] Mostrar ruta ");
		logger.info("       [3] Cambiar ruta ");
		logger.info("       [4] Salir ");
	}

	/**
	 * @brief Procesa la opción seleccionada en el menú de archivos
	 * @param context Contexto actual de lectura
	 * @param scanner Scanner para entrada de usuario
	 */
	private static void procesarOpcionArchivo(FileReaderContext context, Scanner scanner) {
		try {
			int opcion = scanner.nextInt();
			scanner.nextLine(); // Limpiar buffer

			switch(opcion) {
				case 1: procesarOpcionNombreArchivo(context, scanner); break;
				case 2: mostrarRutaActual(context); break;
				case 3: cambiarRuta(context, scanner); break;
				case 4: context.exit(); break;
				default: logger.info(MENSAJE_OPCION_NO_VALIDA);
			}
		} catch (InputMismatchException e) {
			logger.error("Entrada inválida. Debe ingresar un número.");
			scanner.nextLine(); // Limpiar entrada incorrecta
		}
	}

	/**
	 * @brief Procesa la entrada del nombre de archivo
	 * @param context Contexto actual
	 * @param scanner Scanner para entrada
	 */
	private static void procesarOpcionNombreArchivo(FileReaderContext context, Scanner scanner) {
		logger.info("Introduzca el nombre del archivo: ");
		String nombreArchivo = scanner.nextLine();
		validarArchivoRead(context.getRuta() + nombreArchivo);
		context.setArchivo(nombreArchivo);
	}

	/**
	 * @brief Valida que un archivo sea accesible para lectura
	 * @param pathCompleto Ruta completa al archivo
	 */
	private static void validarArchivoRead(String pathCompleto) {
		try {
			Path filePath = Paths.get(pathCompleto);

			if (!Files.exists(filePath)) {
				logger.error("El archivo no existe");
				return;
			}
			if (!Files.isReadable(filePath)) {
				logger.error("No se tienen permisos de lectura");
				return;
			}
			if (Files.size(filePath) == 0) {
				logger.error("El archivo está vacío");
			}
		} catch (IOException e) {
			logger.error("Error al validar el archivo: {}", e.getMessage());
		}
	}

	/**
	 * @brief Muestra la ruta actual configurada
	 * @param context Contexto con la ruta actual
	 */
	private static void mostrarRutaActual(FileReaderContext context) {
		if (logger.isInfoEnabled()) {
			logger.info(context.getRuta());
		}
	}

	/**
	 * @brief Cambia la ruta base para operaciones con archivos
	 * @param context Contexto a modificar
	 * @param scanner Scanner para entrada
	 */
	private static void cambiarRuta(FileReaderContext context, Scanner scanner) {
		logger.info("Introduzca la nueva ruta: ");
		String nuevaRuta = scanner.nextLine();
		if (!nuevaRuta.endsWith(File.separator)) {
			nuevaRuta += File.separator;
		}
		context.setRuta(nuevaRuta);
	}

	/**
	 * @brief Muestra el menú de modificación de datasets
	 */
	private static void mostrarMenuModificacion() {
		logger.info("\n=== MENÚ DE MODIFICACIÓN ===");
		logger.info("1. Añadir instancia");
		logger.info("2. Eliminar instancia");
		logger.info("3. Modificar instancia");
		logger.info("4. Cambiar peso de atributos");
		logger.info("5. Volver al menú principal");
		logger.info(MENSAJE_SELECCIONE_OPCION);
	}

	/**
	 * @brief Menú principal para modificación de datasets
	 * @param data Dataset a modificar
	 * @return Dataset modificado
	 */
	public static Dataset modify(Dataset data) {
		int opcion = 0;
		Scanner scanner = new Scanner(System.in);

		while (opcion != 5) {
			mostrarMenuModificacion();

			try {
				if (scanner.hasNextInt()) {
					opcion = scanner.nextInt();
					scanner.nextLine(); // Limpiar buffer
				} else {
					logger.error("Entrada inválida. Debe ingresar un número.");
					scanner.nextLine(); // Limpiar entrada incorrecta
					continue;
				}

				switch(opcion) {
					case 1:
						agregarInstancia(data, scanner);
						break;
					case 2:
						eliminarInstancia(data, scanner);
						break;
					case 3:
						logger.info("Añadiendo nueva instancia:");
						agregarInstancia(data, scanner);
						logger.info("Eliminando instancia antigua:");
						eliminarInstancia(data, scanner);
						break;
					case 4:
						data = cambiarPesos(data);
						break;
					case 5:
						logger.info("Saliendo del menú de modificación");
						break;
					default:
						logger.warn("Opción no válida. Por favor ingrese un número entre 1 y 5");
				}
			} catch (InputMismatchException e) {
				logger.error("Error: Debe ingresar un número válido");
				scanner.nextLine(); // Limpiar buffer
			}
		}
		return data;
	}

	/**
	 * @brief Añade una nueva instancia al dataset
	 * @param data Dataset destino
	 * @param scanner Scanner para entrada
	 */
	private static void agregarInstancia(Dataset data, Scanner scanner) {
		logger.info("Introduce los valores separados por comas ({} atributos):", data.numeroAtributos());
		String valores = scanner.nextLine();
		String[] subcadenas = valores.split(",");

		if (subcadenas.length != data.numeroAtributos()) {
			logger.error("Debe ingresar exactamente {} valores", data.numeroAtributos());
			return;
		}

		data.add(Arrays.asList(subcadenas));
	}

	/**
	 * @brief Elimina una instancia del dataset
	 * @param data Dataset a modificar
	 * @param scanner Scanner para entrada
	 */
	private static void eliminarInstancia(Dataset data, Scanner scanner) {
		logger.info("Introduce el índice a eliminar: ");
		try {
			int valor = scanner.nextInt();
			scanner.nextLine(); // Limpiar buffer
			if (valor < 0 || valor >= data.numeroCasos()) {
				logger.error(MENSAJE_INDICE_FUERA_RANGO, data.numeroCasos()-1);
				return;
			}
			data.delete(valor);
		} catch (InputMismatchException e) {
			logger.error(MENSAJE_INGRESAR_NUMERO);
			scanner.nextLine(); // Limpiar buffer
		}
	}

	/**
	 * @brief Aplica preprocesamiento al dataset
	 * @param data Dataset a procesar
	 * @return Dataset preprocesado
	 */
	public static Dataset preprocesar(Dataset data) {
		logger.info("Seleccione la opción de preprocesado: ");
		logger.info("       [1] Datos crudos ");
		logger.info("       [2] Rango 0-1 "); // por defecto
		logger.info("       [3] Estandarización ");
		logger.info("       [4] Salir ");
		int opcion = 1;
		Scanner scanner = new Scanner(System.in);
		opcion = scanner.nextInt();
		switch(opcion) {
		case(1):
			data.setPreprocesado(1);
			return data;
		case(2):
			Normalizacion intento1 = new Normalizacion();
			data = new Dataset (intento1.procesar(data));
			data.setPreprocesado(2);
			break;
		case(3):
			Estandarizacion intento2 = new Estandarizacion();
			data = new Dataset (intento2.procesar(data));
			data.setPreprocesado(3);
			break;
		default:
			intento1 = new Normalizacion();
			data = new Dataset (intento1.procesar(data));
			data.setPreprocesado(2);
		}
		return data;
	}

	/**
	 * @brief Muestra el menú de gestión de pesos
	 */
	private static void mostrarMenuPesos() {
		logger.info("=== MENÚ DE PESOS ===");
		logger.info("1. Asignar pesos distintos a todos los atributos");
		logger.info("2. Mismo peso para todos los atributos");
		logger.info("3. Cambiar peso de un atributo específico");
	}

	/**
	 * @brief Menú principal para modificación de pesos
	 * @param data Dataset a modificar
	 * @return Dataset con pesos actualizados
	 */
	public static Dataset cambiarPesos(Dataset data) {
		mostrarMenuPesos();
		Scanner scanner = new Scanner(System.in);

		try {
			int opcion = scanner.nextInt();
			scanner.nextLine(); // Limpiar buffer

			switch(opcion) {
				case 1: return cambiarPesosIndividuales(data, scanner);
				case 2: return cambiarPesoGlobal(data, scanner);
				case 3: return cambiarPesoAtributo(data, scanner);
				default: logger.error(MENSAJE_OPCION_NO_VALIDA);
			}
		} catch (InputMismatchException e) {
			logger.error(MENSAJE_OPCION_NO_VALIDA);
			scanner.nextLine(); // Limpiar buffer
		}
		return data;
	}

	/**
	 * @brief Cambia pesos individuales para cada atributo
	 * @param data Dataset a modificar
	 * @param scanner Scanner para entrada
	 * @return Dataset modificado
	 */
	private static Dataset cambiarPesosIndividuales(Dataset data, Scanner scanner) {
		logger.info("Introduce los pesos separados por comas ({} valores entre 0 y 1):", data.numeroAtributos());
		String valores = scanner.nextLine();

		List<String> pesosValidos = validarPesos(data, valores);
		if (!pesosValidos.isEmpty()) {
			data.cambiarPeso(pesosValidos);
		}
		return data;
	}

	/**
	 * @brief Valida una lista de pesos ingresados
	 * @param data Dataset de referencia
	 * @param valores Cadena con pesos separados por comas
	 * @return Lista de pesos validados
	 */
	private static List<String> validarPesos(Dataset data, String valores) {
		String[] subcadenas = valores.split(",");
		List<String> pesosValidos = new ArrayList<>();

		if (subcadenas.length != data.numeroAtributos()) {
			logger.error("Debe ingresar exactamente {} pesos", data.numeroAtributos());
			return pesosValidos;
		}

		for (String pesoStr : subcadenas) {
			try {
				double peso = Double.parseDouble(pesoStr.trim());
				if (peso < 0 || peso > 1) {
					logger.error("Los pesos deben estar entre 0 y 1. Valor inválido: {}", pesoStr);
					return new ArrayList<>();
				}
				pesosValidos.add(pesoStr.trim());
			} catch (NumberFormatException e) {
				logger.error("Valor no numérico detectado: {}", pesoStr);
				return new ArrayList<>();
			}
		}

		return pesosValidos;
	}

	/**
	 * @brief Asigna un mismo peso a todos los atributos
	 * @param data Dataset a modificar
	 * @param scanner Scanner para entrada
	 * @return Dataset modificado
	 */
	private static Dataset cambiarPesoGlobal(Dataset data, Scanner scanner) {
		try {
			double pesoGlobal = leerPesoValido(scanner);
			if (pesoGlobal >= 0) {
				data.cambiarPeso(pesoGlobal);
			}
		} catch (InputMismatchException e) {
			logger.error("Debe ingresar un número válido entre 0 y 1");
			scanner.nextLine();
		}

		return data;
	}

	/**
	 * @brief Cambia el peso de un atributo específico
	 * @param data Dataset a modificar
	 * @param scanner Scanner para entrada
	 * @return Dataset modificado
	 */
	private static Dataset cambiarPesoAtributo(Dataset data, Scanner scanner) {
		try {
			logger.info("Índice del atributo a modificar (0-{}):", data.numeroAtributos()-1);
			int indice = scanner.nextInt();

			if (indice < 0 || indice >= data.numeroAtributos()) {
				logger.error("Índice fuera de rango válido");
				return data;
			}

			logger.info("Nuevo peso (0-1):");
			double nuevoPeso = leerPesoValido(scanner);
			if (nuevoPeso >= 0) {
				data.cambiarPeso(indice, nuevoPeso);
			}
		} catch (InputMismatchException e) {
			logger.error("Debe ingresar valores numéricos válidos");
			scanner.nextLine();
		}

		return data;
	}

	/**
	 * @brief Convierte cadena a valor numérico de peso
	 * @param pesoStr Cadena con el peso
	 * @return Valor numérico validado
	 * @throws NumberFormatException Si el formato es inválido
	 */
	private static double parsearPeso(String pesoStr) throws NumberFormatException {
		double peso = Double.parseDouble(pesoStr);
		if (peso < 0 || peso > 1) {
			logger.error("Los pesos deben estar entre 0 y 1. Valor inválido: {}", pesoStr);
			return -1;
		}
		return peso;
	}

	/**
	 * @brief Lee y valida un peso desde consola
	 * @param scanner Scanner para entrada
	 * @return Peso validado
	 */
	private static double leerPesoValido(Scanner scanner) {
		double peso = scanner.nextDouble();
		return parsearPeso(String.valueOf(peso));
	}

	/**
	 * @brief Menú de visualización de información del dataset
	 * @param data Dataset a analizar
	 * @param scanner Scanner para entrada
	 */
	public static void info(Dataset data, Scanner scanner) {
		if (isDatasetInvalid(data)) {
			logger.error("No hay dataset cargado o está vacío");
			return;
		}

		try {
			int opcion;
			do {
				mostrarMenuInformacion();
				opcion = procesarOpcionInfo(data, scanner);
			} while (opcion != 6);
		} catch (InputMismatchException e) {
			logger.error("Error: Debe ingresar un número válido");
			scanner.nextLine(); // Limpiar entrada inválida
		}
    }

	/**
	 * @brief Verifica si un dataset es inválido o vacío
	 * @param data Dataset a verificar
	 * @return true si es inválido, false si es válido
	 */
	private static boolean isDatasetInvalid(Dataset data) {
		return data == null || data.numeroCasos() == 0;
	}

	/**
	 * @brief Procesa opciones del menú de información
	 * @param data Dataset actual
	 * @param scanner Scanner para entrada
	 * @return Opción seleccionada
	 */
	private static int procesarOpcionInfo(Dataset data, Scanner scanner) {
		try {
			int opcion = scanner.nextInt();
			scanner.nextLine(); // Limpiar buffer

			switch(opcion) {
				case 1: handlePrintDataset(data); break;
				case 2: handleShowInstance(data, scanner); break;
				case 3: infoCuantitativo(data, scanner); break;
				case 4: infoCualitativo(data); break;
				case 5: handleShowWeights(data); break;
				case 6: logger.info("Volviendo al menú principal"); break;
				default: logger.warn("Opción {} no reconocida. Ingrese 1-6", opcion);
			}
			return opcion;
		} catch (InputMismatchException e) {
			logger.error("Entrada inválida: debe ser un número");
			scanner.nextLine(); // Limpiar buffer
			return -1;
		}
	}

	/**
	 * @brief Muestra el dataset completo
	 * @param data Dataset a imprimir
	 */
	private static void handlePrintDataset(Dataset data) {
		data.print();
	}

	/**
	 * @brief Muestra una instancia específica
	 * @param data Dataset fuente
	 * @param scanner Scanner para entrada
	 */
	private static void handleShowInstance(Dataset data, Scanner scanner) {
		logger.info("Introduce el índice de la instancia a mostrar: ");
		try {
			int valor = scanner.nextInt();
			if (valor < 0 || valor >= data.numeroCasos()) {
				logger.error(MENSAJE_INDICE_FUERA_RANGO, data.numeroCasos()-1);
				return;
			}
			if (logger.isInfoEnabled()) {
				logger.info(data.getInstance(valor).toString());
			}
		} catch (InputMismatchException e) {
			logger.error(MENSAJE_INGRESAR_NUMERO);
			scanner.nextLine(); // Limpiar buffer
		}
	}

	/**
	 * @brief Muestra los pesos actuales de los atributos
	 * @param data Dataset a analizar
	 */

	private static void handleShowWeights(Dataset data) {
		StringBuilder nombre = new StringBuilder();
		for(String peso: data.getPesos()) {
			nombre.append(peso).append(" ");
		}
		if (logger.isInfoEnabled()) {
			logger.info(nombre.toString());
		}
	}

	/**
	 * @brief Muestra el menú de información
	 */
	private static void mostrarMenuInformacion() {
		logger.info("\n=== MENÚ DE INFORMACIÓN ===");
		logger.info("1. Mostrar dataset completo");
		logger.info("2. Mostrar instancia específica");
		logger.info("3. Información atributos cuantitativos");
		logger.info("4. Información atributos cualitativos");
		logger.info("5. Mostrar pesos de atributos");
		logger.info("6. Volver al menú principal");
		logger.info(MENSAJE_SELECCIONE_OPCION);
	}

	/**
	 * @brief Menú para información de atributos cuantitativos
	 * @param data Dataset a analizar
	 * @param mainScanner Scanner para entrada
	 */
	public static void infoCuantitativo(Dataset data, Scanner mainScanner) {
		mostrarMenuCuantitativo();

		try {
			int opcion = mainScanner.nextInt();
			mainScanner.nextLine(); // Limpiar buffer

			if (opcion < 1 || opcion > 5) {
				logger.warn("Opción no válida: {}. Por favor seleccione 1-5", opcion);
				return;
			}

			if (opcion == 5) {
				procesarDesviacionTipica(data, mainScanner);
			} else {
				procesarOpcionGeneral(data, mainScanner, opcion);
			}
		} catch (InputMismatchException e) {
			logger.error("Error: Debe ingresar un número válido");
			mainScanner.nextLine(); // Limpiar entrada inválida
		}
	}

	/**
	 * @brief Muestra opciones para atributos cuantitativos
	 */
	private static void mostrarMenuCuantitativo() {
		logger.info("\n=== INFORMACIÓN CUANTITATIVA ===");
		logger.info("1. Mostrar nombre");
		logger.info("2. Mostrar media");
		logger.info("3. Mostrar máximo");
		logger.info("4. Mostrar mínimo");
		logger.info("5. Mostrar desviación típica");
		logger.info("Seleccione una opción (1-5): ");
	}

	/**
	 * @brief Procesa cálculo de desviación típica
	 * @param data Dataset fuente
	 * @param scanner Scanner para entrada
	 */
	private static void procesarDesviacionTipica(Dataset data, Scanner scanner) {
		logger.info("Introduce el índice del atributo cuantitativo: ");
		try {
			int valor = scanner.nextInt();
			scanner.nextLine(); // Limpiar buffer
			if (valor < 0 || valor >= data.numeroAtributos()) {
				logger.error(MENSAJE_INDICE_FUERA_RANGO, data.numeroAtributos()-1);
				return;
			}
			procesarOpcionCuantitativa(data, valor, 5);
		} catch (InputMismatchException e) {
			logger.error(MENSAJE_INGRESAR_NUMERO);
			scanner.nextLine();
		}
	}

	/**
	 * @brief Procesa opciones generales de atributos cuantitativos
	 * @param data Dataset fuente
	 * @param scanner Scanner para entrada
	 * @param opcion Opción seleccionada
	 */
	private static void procesarOpcionGeneral(Dataset data, Scanner scanner, int opcion) {
		logger.info("Introduce el índice del atributo cuantitativo: ");
		try {
			int valor = scanner.nextInt();
			scanner.nextLine(); // Limpiar buffer
			mostrarInfoAtributo(data, valor, opcion);
		} catch (InputMismatchException e) {
			logger.error(MENSAJE_INGRESAR_NUMERO);
			scanner.nextLine();
		}
	}

	/**
	 * @brief Muestra información de un atributo específico
	 * @param data Dataset fuente
	 * @param index Índice del atributo
	 * @param option Opción de visualización
	 */
	private static void mostrarInfoAtributo(Dataset data, int index, int option) {
		try {
			Cuantitativo atributo = (Cuantitativo) data.get(index);
			switch(option) {
				case 1: logger.info(atributo.getNombre()); break;
				case 2: logDoubleValue(atributo.media()); break;
				case 3: logDoubleValue(atributo.maximo()); break;
				case 4: logDoubleValue(atributo.minimo()); break;
				default:
					logger.warn("Opción no válida: {}. Las opciones disponibles son 1-4", option);
					break;
			}
		} catch (ClassCastException e) {
			logger.error("El atributo en el índice {} no es cuantitativo", index);
		} catch (IndexOutOfBoundsException e) {
			logger.error("Índice inválido: {}", index);
		}
	}

	/**
	 * @brief Muestra un valor double formateado
	 * @param value Valor a mostrar
	 */
	private static void logDoubleValue(double value) {
		if (logger.isInfoEnabled()) {
			logger.info(Double.toString(value));
		}
	}

	/**
	 * @brief Procesa opciones específicas para atributos cuantitativos
	 * @param data Dataset fuente
	 * @param valor Índice del atributo
	 * @param opcion Opción seleccionada
	 */
	private static void procesarOpcionCuantitativa(Dataset data, int valor, int opcion) {
		try {
			Cuantitativo auxiliar = (Cuantitativo) data.get(valor);
			mostrarInformacionCuantitativa(auxiliar, opcion);
		} catch (ClassCastException e) {
			logger.error("El atributo en el índice {} no es cuantitativo", valor);
		}
	}

	/**
	 * @brief Muestra información detallada de atributos cuantitativos
	 * @param auxiliar Atributo a analizar
	 * @param opcion Tipo de información a mostrar
	 */
	private static void mostrarInformacionCuantitativa(Cuantitativo auxiliar, int opcion) {
		switch(opcion) {
			case 1:
				if (logger.isInfoEnabled()) logger.info(auxiliar.getNombre());
				break;
			case 2:
				if (logger.isInfoEnabled()) logger.info(Double.toString(auxiliar.media()));
				break;
			case 3:
				if (logger.isInfoEnabled()) logger.info(Double.toString(auxiliar.maximo()));
				break;
			case 4:
				if (logger.isInfoEnabled()) logger.info(Double.toString(auxiliar.minimo()));
				break;
			case 5:
				if (logger.isInfoEnabled()) logger.info(Double.toString(auxiliar.desviacion()));
				break;
			default:
				logger.warn("Opción no válida: {}. Las opciones disponibles son 1-5", opcion);
				break;
		}
	}

	/**
	 * @brief Menú para información de atributos cualitativos
	 * @param data Dataset a analizar
	 */
	public static void infoCualitativo(Dataset data) {
		logger.info("               [1] Mostrar nombre ");
		logger.info("               [2] Mostrar número de clases ");
		logger.info("               [3] Mostrar clases");
		logger.info("               [4] Mostrar frecuencia");
		int opcion = 1;
		Scanner scanner = new Scanner(System.in);
		opcion = scanner.nextInt();
		switch(opcion) {
		case(1):
			int valor = 0;
			Scanner scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			try {
				Cualitativo auxiliar = (Cualitativo) data.get(valor);
				logger.info(auxiliar.getNombre());
			} catch (ClassCastException e) {
				logger.info("Ese atributo no es cualitativo");
			}
			
			break;
		case(2):
			valor = 0;
			scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			Cualitativo auxiliar = (Cualitativo) data.get(valor);
			if (logger.isInfoEnabled()) {
				logger.info(Integer.toString(auxiliar.nClases()));
			}
			break;
		case(3):
				valor = 0;
			scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			auxiliar = (Cualitativo) data.get(valor);
			StringBuilder nombre = new StringBuilder();
			for(String clase: auxiliar.clases()) {
				nombre.append(clase);
				nombre.append(" ");
			}
			if (logger.isInfoEnabled()) {
				logger.info(nombre.toString());
			}
			break;
		case(4):
			valor = 0;
			scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			auxiliar = (Cualitativo) data.get(valor);
			StringBuilder nombre1 = new StringBuilder();
			for(String clase: auxiliar.clases()) {
				nombre1.append(clase);
				nombre1.append(" ");
			}
			nombre1.append("\n");
			for(Double clase: auxiliar.frecuencia()) {
				nombre1.append(clase);
				nombre1.append(" ");
			}
			if (logger.isInfoEnabled()) {
				logger.info(nombre1.toString());
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @brief Realiza experimentos de clasificación con el dataset
	 * @param datos Dataset a utilizar
	 * @throws IOException Si hay errores al guardar resultados
	 */
	public static void experimentar(Dataset datos) throws IOException {
		if (datos.numeroCasos() == 0) {
			logger.error("No se puede realizar experimentación: el dataset está vacío");
			return;
		}
		int opcion = 1;
		Scanner scanner = new Scanner(System.in);
		Entrenamiento nuevo = new Entrenamiento();
		while (opcion != 5) {
			logger.info("               [1] Generacion experimentación normal");
			logger.info("               [2] Generacion experimentación aleatoria");
			logger.info("               [3] Guardar Dataset ");
			logger.info("               [4] Cargar Dataset ");
			logger.info("               [5] Salir");
			opcion = scanner.nextInt();
			switch(opcion) {
			case(1):
				int valor = 0;
				Scanner scanner1 = new Scanner(System.in);
				logger.info(MENSAJE_CONJUNTO_ENTRENAMIENTO);
				valor = scanner1.nextInt();
				nuevo = new Entrenamiento(datos, (double)valor/100);
				logger.info(MENSAJE_INTRODUCIR_K);
				int k = scanner.nextInt();
				nuevo.generarPrediccion(k, MENSAJE_RESULTADOS_TXT);
				nuevo.generarMatriz(k);
				break;
			case(2):
				nuevo = experimentacionAleatoria(datos);
				break;
			case(3):
				logger.info("Introduzca el nombre para el archivo de entrenamiento: ");
				scanner1 = new Scanner(System.in);
				String archivo1 = scanner1.nextLine();
				logger.info("Introduzca el nombre para el archivo de pruebas: ");
				scanner1 = new Scanner(System.in);
				String archivo2 = scanner1.nextLine();
				nuevo.write(archivo1, archivo2);
				break;
			case(4):
				logger.info("Introduzca el nombre del archivo de entrenamiento: ");
				scanner1 = new Scanner(System.in);
				archivo1 = scanner1.nextLine();
				logger.info("Introduzca el nombre del archivo de pruebas: ");
				scanner1 = new Scanner(System.in);
				archivo2 = scanner1.nextLine();
				nuevo.read(archivo1, archivo2);
				logger.info(MENSAJE_INTRODUCIR_K);
				k = scanner.nextInt();
				nuevo.generarPrediccion(k, MENSAJE_RESULTADOS_TXT);
				nuevo.generarMatriz(k);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * @brief Realiza experimentación con división aleatoria del dataset
	 * @param datos Dataset completo
	 * @return Objeto Entrenamiento con resultados
	 */
	public static Entrenamiento experimentacionAleatoria(Dataset datos) {
		logger.info("               [1] Semilla(Seed) por defecto");
		logger.info("               [2] Semilla(Seed) manual");
		int opcion = 1;
		Scanner scanner = new Scanner(System.in);
		opcion = scanner.nextInt();
		Entrenamiento nuevo = new Entrenamiento();
		switch(opcion) {
		case(1):
			int valor = 0;
			Scanner scanner1 = new Scanner(System.in);
			logger.info(MENSAJE_CONJUNTO_ENTRENAMIENTO);
			valor = scanner1.nextInt();
			nuevo = new Entrenamiento(datos, (double)valor/100, 1234);
			logger.info(MENSAJE_INTRODUCIR_K);
			int k = scanner.nextInt();
			nuevo.generarPrediccion(k, MENSAJE_RESULTADOS_TXT);
			nuevo.generarMatriz(k);
			return nuevo;
		case(2):
			valor = 0;
			scanner1 = new Scanner(System.in);
			logger.info(MENSAJE_CONJUNTO_ENTRENAMIENTO);
			valor = scanner1.nextInt();
			scanner1 = new Scanner(System.in);
			logger.info("Introduzca la semilla para la generacion aleatoria");
			int valor2 = scanner1.nextInt();
			nuevo = new Entrenamiento(datos, (double)valor/100, valor2);
			logger.info(MENSAJE_INTRODUCIR_K);
			k = scanner.nextInt();
			nuevo.generarPrediccion(k, MENSAJE_RESULTADOS_TXT);
			nuevo.generarMatriz(k);
			return nuevo;
		default:
			break;
		}
		return nuevo;
	}
}

