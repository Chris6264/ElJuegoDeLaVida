package mx.tecnm.culiacan;

import java.util.ArrayList;
import java.util.List;

/**
 * Controla la partida de "El Juego de la Vida".
 *
 * Se encarga de la interaccion con el usuario: pide los datos, muestra cada
 * generacion, espera Enter entre una y otra, decide si el juego termino e
 * imprime el historial de movimientos al final. El estado del tablero lo
 * maneja {@link Tablero} y las validaciones y el calculo de los nuevos estados
 * los hace {@link Reglas}.
 *
 * El juego termina cuando ocurre alguno de estos casos:
 *
 *   - Todos los organismos mueren.
 *   - Una generacion es igual a la anterior.
 *   - Se alcanza el numero de generaciones pedido.
 *
 * @see Tablero
 * @see Reglas
 * @see HistorialGeneracion
 */
public class ElJuegoDeLaVida {
    private static final String BORDE_SUPERIOR = "+" + "-".repeat(45) + "+";

    private static final String SEPARADOR = "+" + "-".repeat(12) + "+" + "-".repeat(10) + "+"
            + "-".repeat(9) + "+" + "-".repeat(11) + "+";

    private final Reglas reglas;

    /**
     * Crea una partida con las reglas indicadas.
     *
     * @param reglas reglas que se aplicaran durante el juego
     */
    public ElJuegoDeLaVida(Reglas reglas) {
        this.reglas = reglas;
    }

    /**
     * Ejecuta una partida completa: pide los datos al usuario, simula las
     * generaciones y al final imprime el historial de movimientos.
     *
     * @throws ValidacionTableroException si las filas o columnas estan fuera del rango permitido
     * @throws ReglasException si el numero de generaciones o los datos iniciales no son validos
     */
    public void jugar() {
        Tablero tablero = crearTablero();
        int numeroGeneraciones = pedirNumeroGeneraciones();
        String datosIniciales = pedirDatosIniciales();

        tablero.iniciar(datosIniciales);
        imprimirSalida(tablero, numeroGeneraciones, datosIniciales);

        List<HistorialGeneracion> historial = simularGeneraciones(tablero, numeroGeneraciones);
        imprimirHistorial(historial);
    }

    /**
     * Pide el numero de filas y columnas y crea el tablero.
     *
     * @return tablero vacio con las dimensiones indicadas
     */
    private Tablero crearTablero() {
        System.out.print("Escriba numero de filas: ");
        int numeroFilas = Keyboard.readInt();
        System.out.print("Escriba numero de columnas: ");
        int numeroColumnas = Keyboard.readInt();
        return new Tablero(numeroFilas, numeroColumnas, reglas);
    }

    /**
     * Pide el numero de generaciones y verifica que este dentro del rango permitido.
     *
     * @return numero de generaciones validado
     */
    private int pedirNumeroGeneraciones() {
        System.out.print("Escriba numero de generaciones: ");
        int numeroGeneraciones = Keyboard.readInt();
        reglas.validarNumeroGeneraciones(numeroGeneraciones);
        return numeroGeneraciones;
    }

    /**
     * Muestra el formato esperado y lee los datos iniciales.
     *
     * El formato es {@code numeroOrganismos, x1,y1, x2,y2, ...}.
     *
     * @return texto con los datos iniciales, tal como lo escribio el usuario
     */
    private String pedirDatosIniciales() {
        System.out.println();
        System.out.println("Escriba los datos iniciales");
        System.out.println("Formato: numeroOrganismos, x1,y1, x2,y2, ...");
        System.out.println("Ejemplo: 2, 0,1, 3,4 ");
        System.out.print("-> ");
        return Keyboard.readString();
    }

    /**
     * Recorre las generaciones: muestra el tablero, guarda sus registros en el
     * historial y avanza a la siguiente despues de que el usuario presione Enter.
     * Se detiene antes si {@link #juegoTerminado(Tablero)} indica que el juego acabo.
     *
     * @param tablero tablero ya inicializado con los organismos
     * @param numeroGeneraciones numero maximo de generaciones a mostrar
     * @return historial con una entrada por cada generacion mostrada
     */
    private List<HistorialGeneracion> simularGeneraciones(Tablero tablero, int numeroGeneraciones) {
        List<HistorialGeneracion> historial = new ArrayList<>();

        for (int i = 0; i < numeroGeneraciones; i++) {
            System.out.println("Generacion #" + (i + 1));
            System.out.println(tablero.toString());
            historial.add(new HistorialGeneracion(i + 1, tablero.getRegistroCeldas()));

            if (juegoTerminado(tablero)) break;

            if (i < numeroGeneraciones - 1) {
                esperarEnter();
                tablero.nextGeneration();
            }
        }
        return historial;
    }

    /**
     * Revisa si el juego debe terminar y en ese caso imprime el motivo.
     * Todos los organismos muertos se revisa primero porque es el motivo mas especifico: un
     * tablero vacio repetido tambien seria igual al anterior.
     *
     * @param tablero tablero con la generacion actual
     * @return {@code true} si todos los organismos murieron o la generacion
     *         es igual a la anterior; {@code false} en caso contrario
     */
    private boolean juegoTerminado(Tablero tablero) {
        if (tablero.sinOrganismosVivos()) {
            System.out.println("Todos los organismos murieron. Fin del juego.");
            return true;
        }
        if (tablero.generacionRepetida()) {
            System.out.println("La generacion es igual a la anterior. Fin del juego.");
            return true;
        }
        return false;
    }

    /**
     * Pausa el juego hasta que el usuario presione Enter.
     */
    private void esperarEnter() {
        System.out.print("Presione Enter para continuar...");
        Keyboard.readLine();
    }

    /**
     * Imprime el resumen de la partida: dimensiones del tablero, numero de
     * generaciones y coordenadas de los organismos iniciales.
     *
     * @param tablero tablero ya inicializado
     * @param numeroGeneraciones numero de generaciones solicitado
     * @param datosIniciales datos iniciales escritos por el usuario
     */
    private void imprimirSalida(Tablero tablero, int numeroGeneraciones, String datosIniciales) {
        System.out.println();
        System.out.println("Datos del juego: Numero De Filas: " + tablero.getNumeroFilas() + " , "
                + "Numero De Columnas: " + tablero.getNumeroColumnas() + " , "
                + "Numero De Generaciones: " + numeroGeneraciones + "\n");
        System.out.println("Organismo vivos en celdas");
        System.out.println(formatearCoordenadas(datosIniciales) + "\n");
    }

    /**
     * Convierte los datos iniciales en una lista legible de organismos.
     * Se omite el primer dato, que es la cantidad de organismos.
     *
     * @param datosIniciales datos con el formato {@code numeroOrganismos, x1,y1, x2,y2, ...}
     * @return texto del estilo {@code Organismo # 1: (0,1) Organismo # 2: (3,4)}
     */
    private String formatearCoordenadas(String datosIniciales) {
        StringBuilder cadenaCoordenadas = new StringBuilder();
        String[] datos = datosIniciales.split(",\\s+");

        for (int i = 1; i < datos.length; i++) {
            cadenaCoordenadas.append("Organismo # ").append(i).append(": ")
                    .append("(").append(datos[i]).append(") ");
        }
        return cadenaCoordenadas.toString();
    }

    /**
     * Imprime al final del juego una tabla por cada generacion guardada.
     *
     * @param historial generaciones registradas durante la partida
     */
    private void imprimirHistorial(List<HistorialGeneracion> historial) {
        System.out.println("========== HISTORIAL DE MOVIMIENTOS ==========");
        for (HistorialGeneracion generacion : historial) {
            imprimirTablaGeneracion(generacion);
        }
    }

    /**
     * Imprime la tabla de una generacion con la posicion, el estado, los
     * vecinos vivos y el resultado de cada celda.
     *
     * @param generacion generacion cuyos registros se van a imprimir
     */
    private void imprimirTablaGeneracion(HistorialGeneracion generacion) {
        System.out.println();
        System.out.println(BORDE_SUPERIOR);
        System.out.printf("| %-43s |%n", "Generacion #" + generacion.numero());
        System.out.println(SEPARADOR);
        System.out.printf("| %-10s | %-8s | %7s | %-9s |%n", "Posicion", "Estado", "Vecinos", "Resultado");
        System.out.println(SEPARADOR);

        for (RegistroCelda r : generacion.historialMovimientos()) {
            System.out.printf("| %-10s | %-8s | %7d | %-9s |%n",
                    r.fila() + "," + r.columna(), r.estado(), r.vecinos(), r.resultado());
        }
        System.out.println(SEPARADOR);
    }
}