package vectores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @brief Clase que representa un vector matemático y sus operaciones básicas
 *
 * Esta clase implementa un vector matemático con operaciones como suma, producto escalar,
 * normalización, lectura/escritura de archivos, entre otras.
 */
public class Vector {
    private ArrayList<Double> coef;
    public static final String MENSAJE_TAMANO_VECTOR = "Los vectores deben tener el mismo tamaño";

    /**
     * @brief Constructor vacío que inicializa un vector sin elementos
     */
    public Vector() {
        coef = new ArrayList<>();
    }

    /**
     * @brief Constructor que crea un vector a partir de un array de doubles
     * @param array Array de valores double para inicializar el vector
     */
    public Vector(double[] array) {
        this();
        for (double value : array) {
            coef.add(value);
        }
    }

    /**
     * @brief Constructor que crea un vector a partir de una lista de doubles
     * @param coef Lista de valores double para inicializar el vector
     */
    public Vector(List<Double> coef) {
        this.coef = new ArrayList<>(coef);
    }

    /**
     * @brief Constructor que crea un vector de tamaño específico inicializado a ceros
     * @param size Tamaño del vector a crear
     */
    public Vector(int size) {
        this();
        for (int i = 0; i < size; ++i) {
            coef.add(0.0);
        }
    }

    /**
     * @brief Constructor que crea un vector leyendo valores desde un archivo usando Scanner
     * @param file Archivo del cual leer los valores del vector
     * @throws FileNotFoundException Si el archivo no se encuentra
     */
    public Vector(File file) throws FileNotFoundException {
        coef = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                if (scanner.hasNextDouble()) {
                    coef.add(scanner.nextDouble());
                } else {
                    scanner.next(); // Saltar tokens no doubles
                }
            }
        }
    }

    /**
     * @brief Constructor que crea un vector a partir de una cadena de valores separados por comas
     * @param str Cadena con valores separados por comas
     */
    public Vector(String str) {
        coef = new ArrayList<>();
        String[] values = str.split(",");
        for (String value : values) {
            coef.add(Double.parseDouble(value.trim()));
        }
    }

    /**
     * @brief Crea una copia del vector actual
     * @return Nueva instancia de Vector con los mismos valores que el original
     */
    public Vector copiar() {
        return new Vector(this.coef);
    }

    /**
     * @brief Obtiene el tamaño (dimensión) del vector
     * @return Entero que representa la dimensión del vector
     */
    public int size() {
        return coef.size();
    }

    /**
     * @brief Elimina todos los elementos del vector
     */
    public void clear() {
        coef.clear();
    }

    /**
     * @brief Representación en cadena del vector
     * @return Cadena que representa los valores del vector
     */
    public String toString() {
        return coef.toString();
    }

    /**
     * @brief Imprime el vector usando el logger
     */
    public void print() {
        Logger logger = LoggerFactory.getLogger(Vector.class);
        if (logger.isInfoEnabled()) {
            logger.info(this.toString());
        }
    }

    /**
     * @brief Obtiene el valor en una posición específica del vector
     * @param index Índice de la posición a obtener
     * @return Valor double en la posición especificada
     */
    public double get(int index) {
        return coef.get(index);
    }

    /**
     * @brief Establece un valor en una posición específica del vector
     * @param index Índice de la posición a modificar
     * @param value Nuevo valor a establecer
     */
    public void set(int index, double value) {
        coef.set(index, value);
    }

    /**
     * @brief Añade un valor al final del vector
     * @param value Valor a añadir
     */
    public void add(double value) {
        coef.add(value);
    }

    /**
     * @brief Suma otro vector al actual (operación in-place)
     * @param other Vector a sumar
     * @throws IllegalArgumentException Si los vectores tienen dimensiones diferentes
     */
    public void add(Vector other) {
        if (this.size() != other.size()) throw new IllegalArgumentException(MENSAJE_TAMANO_VECTOR);
        for (int i = 0; i < this.size(); i++) {
            coef.set(i, coef.get(i) + other.get(i));
        }
    }

    /**
     * @brief Elimina el valor en una posición específica del vector
     * @param index Índice de la posición a eliminar
     */
    public void remove(int index) {
        coef.remove(index);
    }

    /**
     * @brief Obtiene el valor máximo del vector
     * @return Valor máximo del vector
     */
    public double getMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (double value : coef) {
            if (value > max) max = value;
        }
        return max;
    }

    /**
     * @brief Obtiene el índice del valor máximo del vector
     * @return Índice del valor máximo
     */
    public int getMaxInt() {
        double max = Double.NEGATIVE_INFINITY;
        int maxint = -1;
        for (int i = 0; i < coef.size(); ++i) {
            if (coef.get(i) > max) {
                max = coef.get(i);
                maxint = i;
            }
        }
        return maxint;
    }

    /**
     * @brief Obtiene el valor mínimo del vector
     * @return Valor mínimo del vector
     */
    public double getMin() {
        double min = Double.POSITIVE_INFINITY;
        for (double value : coef) {
            if (value < min) min = value;
        }
        return min;
    }

    /**
     * @brief Calcula el producto escalar con otro vector
     * @param other Vector con el que calcular el producto escalar
     * @return Resultado del producto escalar
     * @throws IllegalArgumentException Si los vectores tienen dimensiones diferentes
     */
    public double productoEscalar(Vector other) {
        if (this.size() != other.size()) throw new IllegalArgumentException(MENSAJE_TAMANO_VECTOR);
        double result = 0;
        for (int i = 0; i < this.size(); i++) result += this.get(i) * other.get(i);
        return result;
    }

    /**
     * @brief Suma un escalar a cada componente del vector
     * @param value Valor escalar a sumar
     * @return Nuevo vector resultante de la suma
     */
    public Vector sum(double value) {
        Vector suma = new Vector();
        for (int i = 0; i < coef.size(); i++) {
            suma.add(coef.get(i) + value);
        }
        return suma;
    }

    /**
     * @brief Suma otro vector al actual
     * @param other Vector a sumar
     * @return Nuevo vector resultante de la suma
     * @throws IllegalArgumentException Si los vectores tienen dimensiones diferentes
     */
    public Vector sum(Vector other) {
        if (this.size() != other.size()) throw new IllegalArgumentException(MENSAJE_TAMANO_VECTOR);
        Vector suma = new Vector();
        for (int i = 0; i < this.size(); i++) {
            suma.add(coef.get(i) + other.get(i));
        }
        return suma;
    }

    /**
     * @brief Compara si dos vectores son iguales
     * @param other Vector a comparar
     * @return true si los vectores son iguales, false en caso contrario
     */
    public boolean equals(Vector other) {
        return this.coef.equals(other.coef);
    }

    /**
     * @brief Verifica si dos vectores tienen la misma dimensión
     * @param other Vector a comparar
     * @return true si tienen la misma dimensión, false en caso contrario
     */
    public boolean equalDimension(Vector other) {
        return this.size() == other.size();
    }

    /**
     * @brief Verifica si el vector contiene un valor específico
     * @param value Valor a buscar
     * @return true si el valor está presente, false en caso contrario
     */
    public boolean isContent(double value) {
        return coef.contains(value);
    }

    /**
     * @brief Concatena otro vector al final del actual
     * @param other Vector a concatenar
     */
    public void concat(Vector other) {
        coef.addAll(other.coef);
    }

    /**
     * @brief Escribe el vector en un archivo
     * @param filename Nombre del archivo donde escribir
     * @throws IOException Si ocurre un error de escritura
     */
    public void write(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(this.toString());
        }
    }

    /**
     * @brief Escribe el vector en un archivo con formato específico
     * @param file Archivo donde escribir
     * @throws IOException Si ocurre un error de escritura
     */
    public void write(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(this.toString().replace("[", "").replace("]", "").replace(",", " "));
        }
    }

    /**
     * @brief Lee valores de un archivo y los carga en el vector
     * @param filename Nombre del archivo a leer
     * @throws IOException Si ocurre un error de lectura
     */
    public void read(String filename) throws IOException {
        coef.clear();
        readFile(filename);
    }

    /**
     * @brief Lee valores de un archivo y los carga en el vector
     * @param file Archivo a leer
     * @throws FileNotFoundException Si el archivo no se encuentra
     */
    public void read(File file) throws FileNotFoundException {
        coef.clear();
        readFileWithScanner(file);
    }

    /**
     * @brief Lee valores de un Scanner y los carga en el vector
     * @param scanner Scanner de donde leer los valores
     */
    public void read(Scanner scanner) {
        coef.clear();
        while (scanner.hasNextDouble()) {
            coef.add(scanner.nextDouble());
        }
    }

    /**
     * @brief Calcula el módulo (norma euclídea) del vector
     * @return Módulo del vector
     */
    public double module() {
        double sum = 0;
        for (double value : coef) {
            sum += Math.pow(value, 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * @brief Multiplica el vector por un escalar (operación in-place)
     * @param scalar Escalar por el que multiplicar
     */
    public void multiply(double scalar) {
        for (int i = 0; i < coef.size(); i++) {
            coef.set(i, coef.get(i) * scalar);
        }
    }

    /**
     * @brief Normaliza el vector en el rango [0, 1] (operación in-place)
     */
    public void normalize() {
        double min = this.getMin();
        double max = this.getMax();
        for (int i = 0; i < coef.size(); ++i) coef.set(i, (coef.get(i) - min) / (max - min));
    }

    /**
     * @brief Calcula el promedio de los valores del vector
     * @return Promedio de los valores del vector
     */
    public double avg() {
        double sum = 0;
        for (double value : coef) {
            sum += value;
        }
        return sum / coef.size();
    }

    /**
     * @brief Lee valores de un archivo línea por línea
     * @param filename Nombre del archivo a leer
     * @throws IOException Si ocurre un error de lectura
     */
    void readFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                coef.add(Double.parseDouble(line));
            }
        }
    }

    /**
     * @brief Lee valores de un archivo usando Scanner
     * @param file Archivo a leer
     * @throws FileNotFoundException Si el archivo no se encuentra
     */
    void readFileWithScanner(File file) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextDouble()) {
                coef.add(scanner.nextDouble());
            }
        }
    }

    /**
     * @brief Obtiene la lista de valores del vector
     * @return Lista de valores double del vector
     */
    public List<Double> getValores() {
        return this.coef;
    }
}