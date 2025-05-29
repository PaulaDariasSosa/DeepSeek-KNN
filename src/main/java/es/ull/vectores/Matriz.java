package vectores;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Clase que representa una matriz matemática y sus operaciones básicas
 *
 * Esta clase implementa una matriz matemática con operaciones como multiplicación,
 * transposición, normalización, lectura/escritura de archivos, entre otras.
 */
public class Matriz {
    private List<Vector> matrix;
    private int numRows;
    private int numCols;
    private boolean isTransposed;

    /**
     * @brief Constructor que crea una matriz 1x1 inicializada a cero
     */
    public Matriz() {
        this(1, 1);
        matrix = new ArrayList<Vector>();
        matrix.add(new Vector(1));
        isTransposed = false;
    }

    /**
     * @brief Constructor que crea una matriz mxn inicializada a ceros
     * @param m Número de filas
     * @param n Número de columnas
     */
    public Matriz(int m, int n) {
        this.numRows = m;
        this.numCols = n;
        matrix = new ArrayList<Vector>(m);
        for (int i = 0; i < m; i++) {
            matrix.add(i, (new Vector(n)));
        }
        isTransposed = false;
    }

    /**
     * @brief Constructor que crea una matriz mxn a partir de un array bidimensional
     * @param m Número de filas
     * @param n Número de columnas
     * @param coef Array bidimensional con los valores de la matriz
     */
    public Matriz(int m, int n, double[][] coef) {
        this(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                Vector aux = matrix.get(i);
                aux.set(j, coef[i][j]);
                matrix.set(i, aux);
            }
        }
        isTransposed = false;
    }

    /**
     * @brief Constructor que crea una matriz a partir de una lista de vectores
     * @param vectors Lista de vectores que formarán las filas de la matriz
     * @throws IllegalArgumentException Si la lista de vectores está vacía
     */
    public Matriz(List<Vector> vectors) {
        this(vectors.size(), vectors.get(0).size());
        if (vectors == null || vectors.isEmpty()) {
            throw new IllegalArgumentException("ArrayList<Vector> no puede estar vacío");
        }
        matrix = vectors;
    }

    /**
     * @brief Obtiene el número de filas de la matriz
     * @return Número de filas (considerando si está transpuesta)
     */
    public int getNumRows() {
        return isTransposed ? numCols : numRows;
    }

    /**
     * @brief Obtiene el número de columnas de la matriz
     * @return Número de columnas (considerando si está transpuesta)
     */
    public int getNumCols() {
        return isTransposed ? numRows : numCols;
    }

    /**
     * @brief Imprime la matriz en la consola
     */
    public void print() {
        for (int i = 0; i < numRows; i++) {
            matrix.get(i).print();
        }
    }

    /**
     * @brief Multiplica dos matrices
     * @param a Primera matriz
     * @param b Segunda matriz
     * @return Matriz resultante del producto
     * @throws IllegalArgumentException Si las dimensiones no son compatibles
     */
    public static Matriz multiply(Matriz a, Matriz b) {
        if (a.getNumCols() != b.getNumRows())  throw new IllegalArgumentException("Número de columnas de A no coincide con el número de filas de B");
        Matriz result = new Matriz(a.getNumRows(), b.getNumCols());
        for (int i = 0; i < a.getNumRows(); i++) {
            for (int j = 0; j < b.getNumCols(); j++) {
                double value = a.matrix.get(i).productoEscalar(getColumn(b.matrix, j));
                Vector aux = result.matrix.get(i);
                aux.set(j, value);
                result.matrix.set(i, aux);
            }
        }
        return result;
    }

    /**
     * @brief Método auxiliar para obtener una columna de una matriz
     * @param matrix Matriz de donde extraer la columna
     * @param colIndex Índice de la columna a obtener
     * @return Vector con los valores de la columna
     */
    private static Vector getColumn(List<Vector> matrix, int colIndex) {
        Vector column = new Vector();
        for (int i = 0; i < matrix.size(); i++) {
            column.add(matrix.get(i).get(colIndex));
        }
        return column;
    }

    /**
     * @brief Lee una matriz desde un archivo
     * @param filename Nombre del archivo a leer
     * @return Matriz leída desde el archivo
     * @throws IOException Si ocurre un error de lectura
     */
    public Matriz read(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int m = Integer.parseInt(reader.readLine());
            int n = Integer.parseInt(reader.readLine());
            double[][] coef = new double[m][n];
            for (int i = 0; i < m; i++) {
                String[] lineValues = reader.readLine().split(" ");
                for (int j = 0; j < n; j++) {
                    coef[i][j] = Double.parseDouble(lineValues[j]);
                }
            }
            return new Matriz (m, n, coef);
        }
    }

    /**
     * @brief Escribe la matriz en un archivo de texto
     * @param filename Nombre del archivo donde escribir
     * @throws IOException Si ocurre un error de escritura
     */
    public void write(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(numRows + "\n");
            writer.write(numCols + "\n");
            for (int i = 0; i < numRows; i++) {
                writer.write(matrix.get(i).toString().replace("[", "").replace("]", "").replace(",", "") + "\n");
            }
        }
    }

    /**
     * @brief Obtiene el valor en una posición específica de la matriz
     * @param x Índice de fila
     * @param y Índice de columna
     * @return Valor en la posición (x,y)
     * @throws IndexOutOfBoundsException Si los índices están fuera de rango
     */
    public double get(int x, int y) {
        if (x < 0 || x >= numRows || y < 0 || y >= numCols) {
            throw new IndexOutOfBoundsException("Los índices están fuera del rango de la matriz.");
        }
        return matrix.get(x).get(y);
    }

    /**
     * @brief Compara si dos matrices son iguales
     * @param other Matriz a comparar
     * @return true si las matrices son iguales, false en caso contrario
     */
    public boolean equals(Matriz other) {
        if (numRows != other.numRows || numCols != other.numCols) {
            return false;
        }
        for (int i = 0; i < numRows; i++) {
            if (!this.matrix.get(i).equals(other.matrix.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @brief Transpone la matriz (operación in-place)
     */
    public void transpose() {
        ArrayList<Vector> transposedMatrix = new ArrayList<>();
        for (int j = 0; j < numCols; j++) {
            Vector newRow = new Vector(numRows);
            for (int i = 0; i < numRows; i++) {
                newRow.set(i, matrix.get(i).get(j));
            }
            transposedMatrix.add(newRow);
        }

        // Actualizar dimensiones y matriz
        int temp = numRows;
        numRows = numCols;
        numCols = temp;
        matrix = transposedMatrix;
        isTransposed = !isTransposed;
    }

    /**
     * @brief Elimina una fila de la matriz
     * @param indice Índice de la fila a eliminar
     */
    public void deleteRows(int indice) {
        matrix.remove(indice);
        numRows--;
    }

    /**
     * @brief Elimina una columna de la matriz
     * @param indice Índice de la columna a eliminar
     */
    public void deleteCols(int indice) {
        this.transpose();
        this.deleteRows(indice);
        this.transpose();
    }

    /**
     * @brief Añade una fila vacía a la matriz
     */
    public void addRows() {
        this.numRows += 1;
    }

    /**
     * @brief Añade una columna vacía a la matriz
     */
    public void addCols() {
        this.numCols += 1;
    }

    /**
     * @brief Normaliza las columnas de la matriz al rango [0,1]
     * @return Lista de vectores normalizados
     */
    public List<Vector> normalizar(){
        this.transpose();
        List<Vector> nueva = new ArrayList<>(this.matrix);
        for (Vector fila: nueva) {
            if (fila.getMax() != fila.getMin()) {
                fila.normalize();
            } else {
                for (int i = 0; i < fila.size(); i++) {
                    fila.set(i, 0.5);
                }
            }
        }
        this.transpose();
        return nueva;
    }

    /**
     * @brief Establece un valor en una posición específica de la matriz
     * @param i Índice de fila
     * @param j Índice de columna
     * @param valor Valor a establecer
     */
    public void set(int i, int j, double valor) {
        Vector fila = matrix.get(i);
        fila.set(j, valor);
        matrix.set(i, fila);
    }
}