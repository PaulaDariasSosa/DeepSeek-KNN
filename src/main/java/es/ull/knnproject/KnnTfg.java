package knnproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

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

	public static void main(String[] args) throws IOException {
		String ruta = "";
		boolean salida = false;
		Dataset datosCrudos = new Dataset();
		Dataset datos = new Dataset();
		Scanner scanner = new Scanner(System.in);

		while (!salida) {
			mostrarMenu();

			try {
				int opcion = scanner.nextInt();
				scanner.nextLine(); // Limpiar buffer

				switch (opcion) {
					case 1:
						logger.info("Introduzca la ruta completa del archivo: ");
						String filePath = scanner.nextLine();
						File file = new File(filePath);

						if (!file.exists()) {
							logger.error("El archivo no existe: {}", filePath);
							break;
						}

						if (!file.canRead()) {
							logger.error("No se puede leer el archivo: {}", filePath);
							break;
						}

						// Verificar extensión .csv
						if (!filePath.toLowerCase().endsWith(".csv")) {
							logger.warn("El archivo no tiene extensión .csv, puede que no funcione correctamente");
						}

						try {
							Dataset[] datasets = cargarDataset(filePath);
							datosCrudos = datasets[0];
							datos = datasets[1];
							mostrarFeedback("Dataset cargado correctamente con " + datos.numeroCasos() + " instancias", true);
						} catch (IOException e) {
							mostrarFeedback("Error al cargar el archivo: " + e.getMessage(), false);
						} catch (Exception e) {
							logger.error("Formato de archivo incorrecto: {}", e.getMessage());
						}
						break;

					case 2:
						guardarDataset(datos, ruta);
						break;
					case 3:
						datos = modify(datos);
						break;
					case 4:
						info(datos);
						break;
					case 7:
						salida = true;
						break;
					case 5:
						experimentar(datos);
						break;
					case 6:
						ejecutarKNN(datosCrudos, datos, scanner);
						break;
					default:
						logger.info("Opción no válida.");
				}
			} catch (InputMismatchException e) {
				logger.error("Entrada inválida. Debe ingresar un número.");
				scanner.nextLine(); // Limpiar entrada incorrecta
			}
		}
		scanner.close();
	}

	private static void mostrarMenu() {
		System.out.println("\n=== MENÚ PRINCIPAL ===");
		System.out.println(" 1. Cargar dataset");
		System.out.println(" 2. Guardar dataset");
		System.out.println(" 3. Modificar dataset");
		System.out.println(" 4. Mostrar información");
		System.out.println(" 5. Realizar experimentación");
		System.out.println(" 6. Clasificar instancia");
		System.out.println(" 7. Salir");
		System.out.print("Seleccione una opción: ");
	}

	private static void mostrarFeedback(String mensaje, boolean exito) {
		String color = exito ? "\033[32m" : "\033[31m"; // Verde o rojo
		System.out.println(color + "> " + mensaje + "\033[0m");
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
		int opcion = 2;
		String archivo = "";
		Scanner scanner = new Scanner(System.in);

		while (opcion != 4) {
			logger.info("Se debe especificar la ruta y nombre del archivo: ");
			logger.info("       [1] Introducir nombre");
			logger.info("       [2] Mostrar ruta ");
			logger.info("       [3] Cambiar ruta ");
			logger.info("       [4] Salir ");

			try {
				opcion = scanner.nextInt();
				scanner.nextLine(); // Limpiar buffer

				switch(opcion) {
					case 1:
						logger.info("Introduzca el nombre del archivo: ");
						archivo = scanner.nextLine();
						try {
							Path filePath = Paths.get(ruta + archivo);
							// Validaciones adicionales
							if (!Files.exists(filePath)) {
								logger.error("El archivo no existe");
								continue;
							}
							if (!Files.isReadable(filePath)) {
								logger.error("No se tienen permisos de lectura");
								continue;
							}
							if (Files.size(filePath) == 0) {
								logger.error("El archivo está vacío");
								continue;
							}
						} catch (IOException e) {
							logger.error("Error al validar el archivo: {}", e.getMessage());
							continue;
						}
						break;
					case 2:
						logger.info(ruta);
						break;
					case 3:
						logger.info("Introduzca la nueva ruta: ");
						String nuevaRuta = scanner.nextLine();
						if (!nuevaRuta.endsWith(File.separator)) {
							nuevaRuta += File.separator;
						}
						ruta = nuevaRuta;
						break;
					default:
						logger.info("Opción no válida");
				}
			} catch (InputMismatchException e) {
				logger.error("Entrada inválida. Debe ingresar un número.");
				scanner.nextLine(); // Limpiar entrada incorrecta
			}
		}
		return archivo;
	}

	private static void mostrarMenuModificacion() {
		logger.info("\n=== MENÚ DE MODIFICACIÓN ===");
		logger.info("1. Añadir instancia");
		logger.info("2. Eliminar instancia");
		logger.info("3. Modificar instancia");
		logger.info("4. Cambiar peso de atributos");
		logger.info("5. Volver al menú principal");
		logger.info("Seleccione una opción: ");
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
				logger.error("Opción no válida");
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
				logger.error("Índice fuera de rango. Debe estar entre 0 y {}", data.numeroCasos()-1);
				return;
			}
			data.delete(valor);
		} catch (InputMismatchException e) {
			logger.error("Debe ingresar un número entero válido");
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

	public static Dataset cambiarPesos(Dataset data) {
		logger.info("           [1] Asignar pesos distintos a todos los atributos");
		logger.info("           [2] Mismo peso para todos los atributos");
		logger.info("           [3] Cambiar peso un atributo");

		Scanner scanner = new Scanner(System.in);
		try {
			int opcion = scanner.nextInt();
			scanner.nextLine(); // Limpiar buffer

			switch(opcion) {
				case 1:
					logger.info("Introduce los pesos separados por comas ({} valores entre 0 y 1):", data.numeroAtributos());
					String valores = scanner.nextLine();
					String[] subcadenas = valores.split(",");

					// Validar cantidad de pesos
					if (subcadenas.length != data.numeroAtributos()) {
						logger.error("Debe ingresar exactamente {} pesos", data.numeroAtributos());
						return data;
					}

					// Validar formato y rango de pesos
					ArrayList<String> pesosValidos = new ArrayList<>();
					for (String pesoStr : subcadenas) {
						try {
							double peso = Double.parseDouble(pesoStr.trim());
							if (peso < 0 || peso > 1) {
								logger.error("Los pesos deben estar entre 0 y 1. Valor inválido: {}", pesoStr);
								return data;
							}
							pesosValidos.add(pesoStr.trim());
						} catch (NumberFormatException e) {
							logger.error("Valor no numérico detectado: {}", pesoStr);
							return data;
						}
					}
					data.cambiarPeso(pesosValidos);
					break;

				case 2:
					logger.info("Peso para asignar a todos los atributos (0-1):");
					try {
						double pesoGlobal = scanner.nextDouble();
						if (pesoGlobal < 0 || pesoGlobal > 1) {
							logger.error("El peso debe estar entre 0 y 1");
							return data;
						}
						data.cambiarPeso(pesoGlobal);
					} catch (InputMismatchException e) {
						logger.error("Debe ingresar un número válido entre 0 y 1");
						scanner.nextLine(); // Limpiar buffer
					}
					break;

				case 3:
					logger.info("Índice del atributo a modificar (0-{}):", data.numeroAtributos()-1);
					try {
						int indice = scanner.nextInt();
						if (indice < 0 || indice >= data.numeroAtributos()) {
							logger.error("Índice fuera de rango válido");
							return data;
						}

						logger.info("Nuevo peso (0-1):");
						double nuevoPeso = scanner.nextDouble();
						if (nuevoPeso < 0 || nuevoPeso > 1) {
							logger.error("El peso debe estar entre 0 y 1");
							return data;
						}
						data.cambiarPeso(indice, nuevoPeso);
					} catch (InputMismatchException e) {
						logger.error("Debe ingresar valores numéricos válidos");
						scanner.nextLine(); // Limpiar buffer
					}
					break;
			}
		} catch (InputMismatchException e) {
			logger.error("Opción no válida");
			scanner.nextLine(); // Limpiar buffer
		}
		return data;
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
								logger.error("Índice fuera de rango. Debe estar entre 0 y {}", data.numeroCasos()-1);
								break;
							}
							logger.info(data.getInstance(valor).toString());
						} catch (InputMismatchException e) {
							logger.error("Debe ingresar un número entero válido");
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
		logger.info("Seleccione una opción: ");
	}

	public static void infoCuantitativo(Dataset data) {
		logger.info("               [1] Mostrar nombre ");
		logger.info("               [2] Mostrar media ");
		logger.info("               [3] Mostrar maximo");
		logger.info("               [4] Mostrar minimo");
		logger.info("               [5] Mostrar desviación tipica");
		int opcion = 1;
		Scanner scanner1 = new Scanner(System.in);
		opcion = scanner1.nextInt();

		switch(opcion) {
			case(1):
				int valor = 0;
				valor = scanner1.nextInt();
				Cuantitativo auxiliar = (Cuantitativo) data.get(valor);
				logger.info(auxiliar.getNombre());
				break;
			case(2):
				valor = 0;
				scanner1 = new Scanner(System.in);
				valor = scanner1.nextInt();
				auxiliar = (Cuantitativo) data.get(valor);
				if (logger.isInfoEnabled()) {
					logger.info(Double.toString(auxiliar.media()));
				}
				break;
			case(3):
				valor = 0;
				scanner1 = new Scanner(System.in);
				valor = scanner1.nextInt();
				auxiliar = (Cuantitativo) data.get(valor);
				if (logger.isInfoEnabled()) {
					logger.info(Double.toString(auxiliar.maximo()));
				}
				break;
			case(4):
				valor = 0;
				scanner1 = new Scanner(System.in);
				valor = scanner1.nextInt();
				auxiliar = (Cuantitativo) data.get(valor);
				if (logger.isInfoEnabled()) {
					logger.info(Double.toString(auxiliar.minimo()));
				}
				break;
			case 5:
				logger.info("Introduce el índice del atributo cuantitativo: ");
				try {
					valor = scanner1.nextInt();
					if (valor < 0 || valor >= data.numeroAtributos()) {
						logger.error("Índice fuera de rango. Debe estar entre 0 y {}", data.numeroAtributos()-1);
						break;
					}

					try {
						auxiliar = (Cuantitativo) data.get(valor);
						switch(opcion) {
							case 1: logger.info(auxiliar.getNombre()); break;
							case 2: logger.info(Double.toString(auxiliar.media())); break;
							case 3: logger.info(Double.toString(auxiliar.maximo())); break;
							case 4: logger.info(Double.toString(auxiliar.minimo())); break;
							case 5: logger.info(Double.toString(auxiliar.desviacion())); break;
						}
					} catch (ClassCastException e) {
						logger.error("El atributo en el índice {} no es cuantitativo", valor);
					}
				} catch (InputMismatchException e) {
					logger.error("Debe ingresar un número entero válido");
					scanner1.nextLine(); // Limpiar buffer
				}
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
				nuevo.generarPrediccion(k, "resultados.txt");
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
				nuevo.generarPrediccion(k, "resultados.txt");
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
			nuevo.generarPrediccion(k, "resultados.txt");
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
			nuevo.generarPrediccion(k, "resultados.txt");
			nuevo.generarMatriz(k);
			return nuevo;
		default:
			break;
		}
		return nuevo;
	}
}

