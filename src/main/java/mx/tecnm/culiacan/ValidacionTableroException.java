package mx.tecnm.culiacan;

/**
 * Excepcion que se lanza cuando las dimensiones del tablero no son validas.
 *
 * Se usa cuando el numero de filas o de columnas esta fuera del rango
 * permitido (entre 2 y 20).
 *
 * Es una excepcion no verificada (extiende RuntimeException), por lo que los
 * metodos que la lanzan no necesitan declararla con throws. Quien inicia el
 * juego puede capturarla y mostrar solo el mensaje con getMessage() en vez del
 * stack trace completo.
 *
 * @see Tablero
 */
public class ValidacionTableroException extends RuntimeException {

    /**
     * Crea la excepcion con un mensaje que explica que dimension es invalida.
     *
     * @param message descripcion del error, pensada para mostrarse al usuario
     */
    public ValidacionTableroException(String message) {
        super(message);
    }
}