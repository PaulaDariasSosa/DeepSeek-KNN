package datos;

/**
 * @brief Excepción personalizada para operaciones con datasets
 *
 * Esta excepción se lanza cuando ocurren errores durante operaciones
 * específicas de manipulación de datasets, proporcionando mayor contexto
 * sobre el tipo de error ocurrido.
 */
public class DatasetOperationException extends RuntimeException {

    /**
     * @brief Constructor con mensaje de error
     * @param message Mensaje descriptivo del error
     */
    public DatasetOperationException(String message) {
        super(message);
    }

    /**
     * @brief Constructor con mensaje y causa del error
     * @param message Mensaje descriptivo del error
     * @param cause Excepción original que causó el error
     */
    public DatasetOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}