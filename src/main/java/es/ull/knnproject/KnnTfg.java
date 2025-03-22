package knnproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clasificacion.KNN;
import datos.*;
import procesamiento.*;
import entrenamiento.*;

public class KnnTfg  {
	private static final Logger logger = LoggerFactory.getLogger(KnnTfg.class);

	public static void main(String[] args) throws IOException {
		String ruta = "";
		boolean salida = false;
		Dataset datosCrudos = new Dataset();
		Dataset datos = new Dataset();
		String archivo;
		while(!salida) {
			logger.info("Seleccione una opción: ");
			logger.info("   [1] Cargar un dataset ");
			logger.info("   [2] Guargar un dataset ");
			logger.info("   [3] Modificar un dataset ");
			logger.info("   [4] Mostrar información ");
			logger.info("   [5] Salir del programa ");
			logger.info("   [6] Realizar experimentación ");
			logger.info("   [7] Algoritmo KNN para una instancia ");
			int opcion = 1;
			Scanner scanner = new Scanner(System.in);
			opcion = scanner.nextInt();
			switch(opcion) {
			case(1):
				archivo = readFile(ruta);
				datosCrudos = new Dataset(ruta+archivo);
				datos = new Dataset(ruta+archivo);
				datos = preprocesar(datos);
				break;
			case(2):
				archivo = readFile(ruta);
				datos.write(ruta+archivo);
				break;
			case(3):
				datos = modify(datos);
				break;
			case(4):
				info(datos);
				break;
			case(5):
				salida = true;
				break;
			case(6):
				experimentar(datos);
				break;
			case(7):
				logger.info("Introduce el valor de k: ");
				int k = scanner.nextInt();
				KNN intento = new KNN(k);
				String valoresString = "";
				logger.info("Introduce los valores: ");
				Scanner scanner1 = new Scanner(System.in);
				valoresString = scanner1.nextLine();
				String[] subcadenas = valoresString.split(",");
				ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(subcadenas));
				Instancia instance = new Instancia (valoresString);
				Dataset copiaCrudos = new Dataset(datosCrudos.clone());
				if (datos.getPreprocesado() != 1) {
					arrayList.add("clase");
					copiaCrudos.add(arrayList);
					Preprocesado intento1 = new Normalizacion();
					if (datos.getPreprocesado() == 2) intento1 = new Normalizacion();
					if (datos.getPreprocesado() == 3) intento1 = new Estandarizacion();
					copiaCrudos = new Dataset (intento1.Procesar(copiaCrudos));
					instance = copiaCrudos.getInstance(copiaCrudos.NumeroCasos()-1);
					copiaCrudos.delete(copiaCrudos.NumeroCasos()-1);
					instance.deleteClase();
				}
				logger.info("La clase elegida es: " + intento.clasificar(copiaCrudos, instance));
				break;
			default:
			}
		}
    }
	
	public static String readFile(String ruta) {
		int opcion = 2;
		String archivo = "";
		while (opcion != 4) {
			logger.info("Se debe especificar la ruta y nombre del archivo: ");
			logger.info("       [1] Introducir nombre");
			logger.info("       [2] Mostrar ruta ");
			logger.info("       [3] Cambiar ruta ");
			logger.info("       [4] Salir ");
			Scanner scanner = new Scanner(System.in);
			opcion = scanner.nextInt();
			switch(opcion) {
			case(1):
				logger.info("Introduzca el nombre del archivo: ");
				Scanner scanner1 = new Scanner(System.in);
				archivo = scanner1.nextLine();
				break;
			case(2):
				logger.info(ruta);
				break;
			case(3):
				Scanner scanner2 = new Scanner(System.in);
				ruta = scanner2.nextLine();
				break;
			default:
				logger.info("Por defecto");
			}
		}
		return archivo;
	}
	
	public static Dataset modify(Dataset data) {
		int opcion = 2;
		String valores = "";
		while (opcion != 5) {
			logger.info("Elija una opción de modificación ");
			logger.info("       [1] Añadir instancia ");
			logger.info("       [2] Eliminar instancia ");
			logger.info("       [3] Modificar instancia ");
			logger.info("       [4] Cambiar peso de los atributos ");
			logger.info("       [5] Salir ");
			Scanner scanner = new Scanner(System.in);
			opcion = scanner.nextInt();
			switch(opcion) {
			case(1):
				valores = "";
				logger.info("Introduce los valores: ");
				Scanner scanner1 = new Scanner(System.in);
				valores = scanner1.nextLine();
				String[] subcadenas = valores.split(",");
				ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(subcadenas));
				logger.info(String.valueOf(arrayList));
				data.add(arrayList);
				return data;
			case(2):
				int valor = 0;
				logger.info("Introduce el indice a eliminar: ");
				scanner1 = new Scanner(System.in);
				valor = scanner1.nextInt();
				data.delete(valor);
				return data;
			case(3):
				valores = "";
				logger.info("Introduce los valores: ");
				scanner1 = new Scanner(System.in);
				valores = scanner1.nextLine();
				subcadenas = valores.split(",");
				arrayList = new ArrayList<>(Arrays.asList(subcadenas));
				data.add(arrayList);
				valor = 0;
				logger.info("Introduce el indice a eliminar: ");
				scanner1 = new Scanner(System.in);
				valor = scanner1.nextInt();
				data.delete(valor);
				return data;
			case(4):
				data = CambiarPesos(data);
			return data;
			case(5):
				break;
			default:
				break;
			}
		}
		return data;
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
			data = new Dataset (intento1.Procesar(data));
			data.setPreprocesado(2);
			break;
		case(3):
			Estandarizacion intento2 = new Estandarizacion();
			data = new Dataset (intento2.Procesar(data));
			data.setPreprocesado(3);
			break;
		default:
			intento1 = new Normalizacion();
			data = new Dataset (intento1.Procesar(data));
			data.setPreprocesado(2);
		}
		return data;
	}
	
	public static Dataset CambiarPesos(Dataset data) {
		logger.info("           [1] Asignar pesos distintos a todos los atributos ");
		logger.info("           [2] Mismo peso para todos los atributos "); // por defecto ( valor 1 )
		logger.info("           [3] Cambiar peso un atributo");
		int opcion = 1;
		Scanner scanner = new Scanner(System.in);
		opcion = scanner.nextInt();
		scanner.nextLine();
		switch(opcion) {
		case(1):
			String valores = "";
			Scanner scanner1 = new Scanner(System.in);
			valores = scanner1.nextLine();
			String[] subcadenas = valores.split(",");
			ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(subcadenas));
			data.CambiarPeso(arrayList);
			return data;
		case(2):
			double valoresD = 1.0;
			scanner1 = new Scanner(System.in);
			valoresD = scanner1.nextDouble();
			data.CambiarPeso(valoresD);
			return data;
		case(3):
			int valorI = 0;
			logger.info("Introduce el indice del atributo a modificar: ");
			scanner1 = new Scanner(System.in);
			valorI = scanner1.nextInt();
			logger.info("Peso para asignar(Debe estar entre 0 y 1): ");
			valoresD = 1.0;
			valoresD = scanner1.nextDouble();
			data.CambiarPeso(valorI, valoresD);
			return data;
		default:
			break;
		}
		return data;
	}
	
	public static void info(Dataset data) {
		logger.info("           [1] Mostrar dataset ");
		logger.info("           [2] Mostrar instancia ");
		logger.info("           [3] Mostrar información atributos cuantitativos");
		logger.info("           [4] Mostrar información atributos cualitativos");
		logger.info("           [5] Mostrar pesos de los atributos");
		int opcion = 1;
		Scanner scanner = new Scanner(System.in);
		opcion = scanner.nextInt();
		switch(opcion) {
		case(1):
			data.print();
			break;
		case(2):
			int valor = 0;
			Scanner scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			logger.info(data.getInstance(valor).toString());
			break;
		case(3):
			infoCuantitativo(data);
			break;
		case(4):
			infoCualitativo(data);
			break;
		case(5):
			String nombre = "";
			for(String peso: data.getPesos()) {
				nombre += peso + " ";
			}
			logger.info(nombre);
			break;
		default:
			break;
		}
	}
	
	public static void infoCuantitativo(Dataset data) {
		logger.info("               [1] Mostrar nombre ");
		logger.info("               [2] Mostrar media ");
		logger.info("               [3] Mostrar maximo");
		logger.info("               [4] Mostrar minimo");
		logger.info("               [5] Mostrar desviación tipica");
		int opcion = 1;
		Scanner scanner = new Scanner(System.in);
		opcion = scanner.nextInt();
		switch(opcion) {
		case(1):
			int valor = 0;
			Scanner scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			Cuantitativo auxiliar = (Cuantitativo) data.get(valor);
			logger.info(auxiliar.getNombre());
			break;
		case(2):
			valor = 0;
			scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			auxiliar = (Cuantitativo) data.get(valor);
			logger.info(Double.toString(auxiliar.media()));
			break;
		case(3):
				valor = 0;
			scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			auxiliar = (Cuantitativo) data.get(valor);
			logger.info(Double.toString(auxiliar.maximo()));
			break;
		case(4):
			valor = 0;
			scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			auxiliar = (Cuantitativo) data.get(valor);
			logger.info(Double.toString(auxiliar.minimo()));
			break;
		case(5):
			valor = 0;
			scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			auxiliar = (Cuantitativo) data.get(valor);
			logger.info(Double.toString(auxiliar.desviacion()));
			break;
		default:
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
			logger.info(Integer.toString(auxiliar.nClases()));
			break;
		case(3):
				valor = 0;
			scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			auxiliar = (Cualitativo) data.get(valor);
			String nombre = "";
			for(String clase: auxiliar.clases()) {
				nombre += clase + " ";
			}
			logger.info(nombre);
			break;
		case(4):
			valor = 0;
			scanner1 = new Scanner(System.in);
			valor = scanner1.nextInt();
			auxiliar = (Cualitativo) data.get(valor);
			String nombre1 = "";
			for(String clase: auxiliar.clases()) {
				nombre1 += clase + " ";
			}
			logger.info(nombre1);
			break;
		default:
			break;
		}
	}
	
	public static void experimentar(Dataset datos) throws IOException {
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
				logger.info("Introduzca el porcentaje para el conjunto de entrenamiento");
				valor = scanner1.nextInt();
				nuevo = new Entrenamiento(datos, (double)valor/100);
				logger.info("Introduce el valor de k: ");
				int k = scanner.nextInt();
				nuevo.generarPrediccion(k);
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
				logger.info("Introduce el valor de k: ");
				k = scanner.nextInt();
				nuevo.generarPrediccion(k);
				nuevo.generarMatriz(k);
				break;
			default:
				break;
			}
		}
	}
	
	public static Entrenamiento experimentacionAleatoria(Dataset datos) throws IOException {
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
			logger.info("Introduzca el porcentaje para el conjunto de entrenamiento");
			valor = scanner1.nextInt();
			nuevo = new Entrenamiento(datos, (double)valor/100, 1234);
			logger.info("Introduce el valor de k: ");
			int k = scanner.nextInt();
			nuevo.generarPrediccion(k);
			nuevo.generarMatriz(k);
			return nuevo;
		case(2):
			valor = 0;
			scanner1 = new Scanner(System.in);
			logger.info("Introduzca el porcentaje para el conjunto de entrenamiento");
			valor = scanner1.nextInt();
			scanner1 = new Scanner(System.in);
			logger.info("Introduzca la semilla para la generacion aleatoria");
			int valor2 = scanner1.nextInt();
			nuevo = new Entrenamiento(datos, (double)valor/100, valor2);
			logger.info("Introduce el valor de k: ");
			k = scanner.nextInt();
			nuevo.generarPrediccion(k);
			nuevo.generarMatriz(k);
			return nuevo;
		default:
			break;
		}
		return nuevo;
	}
}

