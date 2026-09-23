package mx.tecnm.culiacan;

import java.util.List;

/**
 * Vista de consola de "El Juego de la Vida".
 *
 * Se encarga exclusivamente de mostrar informacion al usuario mediante la
 * consola. Presenta mensajes de entrada, datos generales de la partida,
 * generaciones, mensajes de fin del juego e historial de movimientos.
 *
 * Esta clase no contiene logica del juego ni modifica el estado del tablero.
 * Su responsabilidad es solamente la presentacion de la informacion.
 *
 * @see ElJuegoDeLaVida
 * @see Tablero
 * @see HistorialGeneracion
 * @see RegistroCelda
 */
public class VistaConsola {

    /**
     * Borde superior utilizado para imprimir las tablas del historial.
     */
    private static final String BORDE_SUPERIOR =
            "+" + "-".repeat(45) + "+";

    /**
     * Separador utilizado entre las filas de las tablas del historial.
     */
    private static final String SEPARADOR =
            "+" + "-".repeat(12)
                    + "+" + "-".repeat(10)
                    + "+" + "-".repeat(9)
                    + "+" + "-".repeat(11)
                    + "+";

    /**
     * Muestra el mensaje para solicitar al usuario el numero de filas
     * del tablero.
     */
    public void pedirNumeroFilas() {
        System.out.print("Escriba numero de filas: ");
    }

    /**
     * Muestra el mensaje para solicitar al usuario el numero de columnas
     * del tablero.
     */
    public void pedirNumeroColumnas() {
        System.out.print("Escriba numero de columnas: ");
    }

    /**
     * Muestra el mensaje para solicitar al usuario el numero de generaciones
     * que desea simular.
     */
    public void pedirNumeroGeneraciones() {
        System.out.print("Escriba numero de generaciones: ");
    }

    /**
     * Muestra las instrucciones y el formato esperado para introducir
     * los organismos iniciales del tablero.
     *
     * El formato esperado es
     * {@code numeroOrganismos, fila1,columna1, fila2,columna2, ...}.
     */
    public void pedirDatosIniciales() {
        System.out.println();
        System.out.println("Escriba los datos iniciales");
        System.out.println("Formato: numeroOrganismos, x1,y1, x2,y2, ...");
        System.out.println("Ejemplo: 2, 0,1, 3,4 ");
        System.out.print("-> ");
    }

    /**
     * Muestra en consola el numero de la generacion y el estado actual
     * del tablero.
     *
     * @param numeroGeneracion numero de la generacion que se esta mostrando
     * @param tablero tablero correspondiente a la generacion actual
     */
    public void mostrarGeneracion(int numeroGeneracion, Tablero tablero) {
        System.out.println("Generacion #" + numeroGeneracion);
        System.out.println(tablero);
    }

    /**
     * Muestra el mensaje que indica que el juego termino porque todos
     * los organismos murieron.
     */
    public void mostrarOrganismosMuertos() {
        System.out.println("Todos los organismos murieron. Fin del juego.");
    }

    /**
     * Muestra el mensaje que indica que el juego termino porque la
     * generacion actual es igual a la anterior.
     */
    public void mostrarGeneracionRepetida() {
        System.out.println(
                "La generacion es igual a la anterior. Fin del juego."
        );
    }

    /**
     * Muestra el mensaje que solicita al usuario presionar Enter
     * para continuar con la siguiente generacion.
     */
    public void pedirEnter() {
        System.out.print("Presione Enter para continuar...");
    }

    /**
     * Muestra un resumen de los datos iniciales de la partida.
     *
     * Incluye las dimensiones del tablero, el numero de generaciones
     * solicitado y las coordenadas de los organismos vivos iniciales.
     *
     * @param tablero tablero inicializado de la partida
     * @param numeroGeneraciones numero de generaciones solicitado
     * @param datosIniciales texto introducido por el usuario con las
     *                       coordenadas iniciales
     */
    public void mostrarDatosJuego(Tablero tablero, int numeroGeneraciones, String datosIniciales) {
        System.out.println();

        System.out.println(
                "Datos del juego: Numero De Filas: "
                        + tablero.getNumeroFilas()
                        + " , Numero De Columnas: "
                        + tablero.getNumeroColumnas()
                        + " , Numero De Generaciones: "
                        + numeroGeneraciones
                        + "\n"
        );

        System.out.println("Organismo vivos en celdas");
        System.out.println(formatearCoordenadas(datosIniciales) + "\n");
    }

    /**
     * Muestra el historial completo de generaciones de la partida.
     *
     * Cada generacion se imprime como una tabla que contiene la posicion
     * de cada celda, su estado actual, el numero de vecinos vivos y el
     * estado resultante.
     *
     * @param historial lista de generaciones registradas durante la partida
     */
    public void mostrarHistorial(
            List<HistorialGeneracion> historial) {

        System.out.println(
                "========== HISTORIAL DE MOVIMIENTOS =========="
        );

        for (HistorialGeneracion generacion : historial) {
            imprimirTablaGeneracion(generacion);
        }
    }

    /**
     * Convierte los datos iniciales introducidos por el usuario en una
     * representacion legible de las coordenadas de cada organismo.
     *
     * Se omite el primer dato, ya que representa la cantidad total de
     * organismos.
     *
     * @param datosIniciales datos con el formato
     *                       {@code numeroOrganismos, fila1,columna1, ...}
     * @return cadena con las coordenadas de cada organismo numeradas
     */
    private String formatearCoordenadas(String datosIniciales) {
        StringBuilder cadenaCoordenadas = new StringBuilder();

        String[] datos = datosIniciales.split(",\\s+");

        for (int i = 1; i < datos.length; i++) {
            cadenaCoordenadas
                    .append("Organismo # ")
                    .append(i)
                    .append(": ")
                    .append("(")
                    .append(datos[i])
                    .append(") ");
        }

        return cadenaCoordenadas.toString();
    }

    /**
     * Imprime una tabla con los registros de todas las celdas de una
     * generacion.
     *
     * Para cada celda muestra su posicion, estado actual, cantidad de
     * vecinos vivos y estado resultante para la siguiente generacion.
     *
     * @param generacion generacion cuyos registros se van a mostrar
     */
    private void imprimirTablaGeneracion(
            HistorialGeneracion generacion) {

        System.out.println();
        System.out.println(BORDE_SUPERIOR);

        System.out.printf(
                "| %-43s |%n",
                "Generacion #" + generacion.numero()
        );

        System.out.println(SEPARADOR);

        System.out.printf(
                "| %-10s | %-8s | %7s | %-9s |%n",
                "Posicion",
                "Estado",
                "Vecinos",
                "Resultado"
        );

        System.out.println(SEPARADOR);

        for (RegistroCelda registro
                : generacion.historialMovimientos()) {

            System.out.printf(
                    "| %-10s | %-8s | %7d | %-9s |%n",
                    registro.fila() + "," + registro.columna(),
                    registro.estado(),
                    registro.vecinos(),
                    registro.resultado()
            );
        }

        System.out.println(SEPARADOR);
    }

    public void mostrarError(String mensaje) {
        System.out.println(mensaje);
        System.out.println();
    }
}