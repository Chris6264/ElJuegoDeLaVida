package mx.tecnm.culiacan;

/**
 * Excepcion que se lanza cuando los datos del usuario no cumplen las reglas
 * de "El Juego de la Vida".
 *
 * Se usa, por ejemplo, cuando el numero de generaciones esta fuera del rango
 * permitido, cuando la cantidad de organismos iniciales no es valida o cuando
 * una coordenada cae fuera del tablero.
 *
 * Es una excepcion no verificada (extiende RuntimeException), por lo que los
 * metodos que la lanzan no necesitan declararla con throws. Quien inicia el
 * juego puede capturarla y mostrar solo el mensaje con getMessage() en vez del
 * stack trace completo.
 *
 * @see ReglasDelJuego
 */
public class ReglasException extends RuntimeException {

    /**
     * Crea la excepcion con un mensaje que explica que regla se incumplio.
     *
     * @param message descripcion del error, pensada para mostrarse al usuario
     */
    public ReglasException(String message) {
        super(message);
    }
}