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

public class KnnTfg  {
	private static final Logger logger = LoggerFactory.getLogger(KnnTfg.class);
	public static final String MENSAJE_INTRODUCIR_K = "Introduce el valor de k: ";
	public static final String MENSAJE_INTRODUCIR_VALORES = "Introduce los valores: ";
	public static final String MENSAJE_CONJUNTO_ENTRENAMIENTO = "Introduzca el porcentaje para el conjunto de entrenamiento";
	public static final String MENSAJE_RESULTADOS_TXT = "resultados.txt";
	public static final String MENSAJE_SELECCIONE_OPCION =  "Seleccione una opción: ";
	public static final String MENSAJE_OPCION_NO_VALIDA = "Opción no válida";
	public static final String MENSAJE_INDICE_FUERA_RANGO = "Índice fuera de rango. Debe estar entre 0 y {}";
	public static final String MENSAJE_INGRESAR_NUMERO = "Debe ingresar un número entero válido";

	public static void main(String[] args) throws IOException {
		AppContext context = new AppContext();
		Scanner scanner = new Scanner(System.in);

		while (!context.salida) {
			mostrarMenu();
			procesarOpcion(context, scanner);
		}

		scanner.close();
	}

	private static void procesarOpcion(AppContext context, Scanner scanner) {
		try {
			int opcion = scanner.nextInt();
			scanner.nextLine(); // Limpiar buffer

			switch (opcion) {
				case 1: cargarDataset(context, scanner); break;
				case 2: guardarDataset(context.datos, context.ruta); break;
				case 3: context.datos = modify(context.datos); break;
				case 4: info(context.datos); break;
				case 5: experimentar(context.datos); break;
				case 6: ejecutarKNN(context.datosCrudos, context.datos, scanner); break;
				case 7: context.salida = true; break;
				default: logger.info("Opción no válida.");
			}
		} catch (InputMismatchException e) {
			logger.error("Entrada inválida. Debe ingresar un número.");
			scanner.nextLine(); // Limpiar entrada incorrecta
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

	private static boolean validarArchivo(String filePath) {
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

	// Clase para mantener el estado de la aplicación
	private static class AppContext {
		String ruta = "";
		boolean salida = false;
		Dataset datosCrudos = new Dataset();
		Dataset datos = new Dataset();
	}

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

	private static void mostrarFeedback(String mensaje, boolean exito) {
		String color = exito ? "\033[32m" : "\033[31m"; // Verde o rojo
		if (logger.isInfoEnabled()) {
			logger.info("{}> {}\u001B[0m", color, mensaje);
		}
	}

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

	private static void guardarDataset(Dataset datos, String ruta) throws IOException {
		String archivo = readFile(ruta);
		datos.write(ruta + archivo);
	}

	private static void ejecutarKNN(Dataset datosCrudos, Dataset datos, Scanner scanner) {
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

	public static String readFile(String ruta) {
		FileReaderContext context = new FileReaderContext(ruta);
		Scanner scanner = new Scanner(System.in);

		while (!context.shouldExit()) {
			mostrarMenuArchivo();
			procesarOpcionArchivo(context, scanner);
		}

		return context.getArchivo();
	}

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

	private static void mostrarMenuArchivo() {
		logger.info("Se debe especificar la ruta y nombre del archivo: ");
		logger.info("       [1] Introducir nombre");
		logger.info("       [2] Mostrar ruta ");
		logger.info("       [3] Cambiar ruta ");
		logger.info("       [4] Salir ");
	}

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

	private static void procesarOpcionNombreArchivo(FileReaderContext context, Scanner scanner) {
		logger.info("Introduzca el nombre del archivo: ");
		String nombreArchivo = scanner.nextLine();
		validarArchivoRead(context.getRuta() + nombreArchivo);
		context.setArchivo(nombreArchivo);
	}

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

	private static void mostrarRutaActual(FileReaderContext context) {
		if (logger.isInfoEnabled()) {
			logger.info(context.getRuta());
		}
	}

	private static void cambiarRuta(FileReaderContext context, Scanner scanner) {
		logger.info("Introduzca la nueva ruta: ");
		String nuevaRuta = scanner.nextLine();
		if (!nuevaRuta.endsWith(File.separator)) {
			nuevaRuta += File.separator;
		}
		context.setRuta(nuevaRuta);
	}

	private static void mostrarMenuModificacion() {
		logger.info("\n=== MENÚ DE MODIFICACIÓN ===");
		logger.info("1. Añadir instancia");
		logger.info("2. Eliminar instancia");
		logger.info("3. Modificar instancia");
		logger.info("4. Cambiar peso de atributos");
		logger.info("5. Volver al menú principal");
		logger.info(MENSAJE_SELECCIONE_OPCION);
	}

	public static Dataset modify(Dataset data) {
		int opcion = 2;
		Scanner scanner = new Scanner(System.in);

		while (opcion != 5) {
			mostrarMenuModificacion();

			try {
				opcion = scanner.nextInt();
				scanner.nextLine(); // Limpiar buffer

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
						logger.warn("Opción no válida: {}. Por favor ingrese un número entre 1 y 5", opcion);
				}
			} catch (InputMismatchException e) {
				logger.error(MENSAJE_OPCION_NO_VALIDA);
				scanner.nextLine(); // Limpiar buffer
			}
		}
		return data;
	}

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

	private static void mostrarMenuPesos() {
		logger.info("=== MENÚ DE PESOS ===");
		logger.info("1. Asignar pesos distintos a todos los atributos");
		logger.info("2. Mismo peso para todos los atributos");
		logger.info("3. Cambiar peso de un atributo específico");
	}

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

	private static Dataset cambiarPesosIndividuales(Dataset data, Scanner scanner) {
		logger.info("Introduce los pesos separados por comas ({} valores entre 0 y 1):", data.numeroAtributos());
		String valores = scanner.nextLine();

		List<String> pesosValidos = validarPesos(data, valores);
		if (!pesosValidos.isEmpty()) {
			data.cambiarPeso(pesosValidos);
		}

		return data;
	}

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

	private static double parsearPeso(String pesoStr) throws NumberFormatException {
		double peso = Double.parseDouble(pesoStr);
		if (peso < 0 || peso > 1) {
			logger.error("Los pesos deben estar entre 0 y 1. Valor inválido: {}", pesoStr);
			return -1;
		}
		return peso;
	}

	private static double leerPesoValido(Scanner scanner) {
		double peso = scanner.nextDouble();
		return parsearPeso(String.valueOf(peso));
	}

	public static void info(Dataset data) {
		if (data == null || data.numeroCasos() == 0) {
			logger.error("No hay dataset cargado o está vacío");
			return;
		}

		int opcion = -1;
		Scanner scanner = new Scanner(System.in);

		while (opcion != 6) {
			mostrarMenuInformacion();

			try {
				opcion = scanner.nextInt();
				scanner.nextLine(); // Limpiar buffer

				switch(opcion) {
					case 1:
						data.print();
						break;
					case 2:
						logger.info("Introduce el índice de la instancia a mostrar: ");
						try {
							int valor = scanner.nextInt();
							if (valor < 0 || valor >= data.numeroCasos()) {
								logger.error(MENSAJE_INDICE_FUERA_RANGO, data.numeroCasos()-1);
								break;
							}
							if (logger.isInfoEnabled()) {
								logger.info(data.getInstance(valor).toString());
							}
						} catch (InputMismatchException e) {
							logger.error(MENSAJE_INGRESAR_NUMERO);
							scanner.nextLine(); // Limpiar buffer
						}
						break;
					case 3:
						infoCuantitativo(data);
						break;
					case 4:
						infoCualitativo(data);
						break;
					case 5:
						StringBuilder nombre = new StringBuilder();
						for(String peso: data.getPesos()) {
							nombre.append(peso);
							nombre.append(" ");
						}
						if (logger.isInfoEnabled()) {
							logger.info(nombre.toString());
						}
						break;
					case 6:
						logger.info("Volviendo al menú principal");
						break;
					default:
						logger.warn("Opción {} no reconocida. Por favor ingrese un número entre 1 y 6", opcion);
				}
			} catch (InputMismatchException e) {
				logger.error("Entrada inválida: debe ser un número");
				scanner.nextLine(); // Limpiar buffer
			}
		}
	}

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

	public static void infoCuantitativo(Dataset data) {
		logger.info("\n=== INFORMACIÓN CUANTITATIVA ===");
		logger.info("1. Mostrar nombre");
		logger.info("2. Mostrar media");
		logger.info("3. Mostrar máximo");
		logger.info("4. Mostrar mínimo");
		logger.info("5. Mostrar desviación típica");
		logger.info("Seleccione una opción (1-5): ");

		try {
			Scanner scanner = new Scanner(System.in);
			int opcion = scanner.nextInt();

			switch(opcion) {
				case 1:
				case 2:
				case 3:
				case 4:
					logger.info("Introduce el índice del atributo cuantitativo: ");
					int valor = scanner.nextInt();
					mostrarInfoAtributo(data, valor, opcion);
					break;

				case 5:
					logger.info("Introduce el índice del atributo cuantitativo: ");
					try {
						valor = scanner.nextInt();
						if (valor < 0 || valor >= data.numeroAtributos()) {
							logger.error(MENSAJE_INDICE_FUERA_RANGO, data.numeroAtributos()-1);
							break;
						}
						procesarOpcionCuantitativa(data, valor, opcion);
					} catch (InputMismatchException e) {
						logger.error(MENSAJE_INGRESAR_NUMERO);
						scanner.nextLine();
					}
					break;

				default:
					logger.warn("Opción no válida: {}. Por favor seleccione una opción entre 1 y 5", opcion);
					break;
			}
		} catch (InputMismatchException e) {
			logger.error("Error: Debe ingresar un número válido");
		}
	}

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

	private static void logDoubleValue(double value) {
		if (logger.isInfoEnabled()) {
			logger.info(Double.toString(value));
		}
	}

	private static void procesarOpcionCuantitativa(Dataset data, int valor, int opcion) {
		try {
			Cuantitativo auxiliar = (Cuantitativo) data.get(valor);
			mostrarInformacionCuantitativa(auxiliar, opcion);
		} catch (ClassCastException e) {
			logger.error("El atributo en el índice {} no es cuantitativo", valor);
		}
	}

	// Método para mostrar la información (ya existente)
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

