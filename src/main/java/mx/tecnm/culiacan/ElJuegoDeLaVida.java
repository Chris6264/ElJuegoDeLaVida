package mx.tecnm.culiacan;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal de "El Juego de la Vida".
 *
 * Se encarga de coordinar el flujo de una partida: solicita los datos
 * necesarios, crea el tablero, inicia la simulacion, controla el avance de
 * las generaciones y determina cuando debe terminar el juego.
 *
 * La presentacion de informacion al usuario se delega en {@link VistaConsola},
 * mientras que las validaciones y reglas de evolucion de los organismos se
 * delegan en {@link Reglas}. El estado de la simulacion es administrado por
 * {@link Tablero}.
 *
 * El juego termina cuando ocurre alguno de los siguientes casos:
 *
 * - Todos los organismos mueren.
 * - Una generacion es igual a la anterior.
 * - Se alcanza el numero maximo de generaciones solicitado.
 *
 * @see Reglas
 * @see VistaConsola
 * @see Tablero
 * @see HistorialGeneracion
 */
public class ElJuegoDeLaVida {
    private final Reglas reglas;
    private final VistaConsola vista;

    /**
     * Crea el controlador de una partida con las reglas y la vista indicadas.
     *
     * @param reglas reglas que se aplicaran durante la simulacion
     * @param vista vista utilizada para mostrar informacion al usuario
     */
    public ElJuegoDeLaVida(Reglas reglas, VistaConsola vista) {
        this.reglas = reglas;
        this.vista = vista;
    }

    /**
     * Ejecuta una partida completa de "El Juego de la Vida".
     *
     * Solicita las dimensiones del tablero, el numero de generaciones y
     * los organismos iniciales. Despues inicializa el tablero, ejecuta la
     * simulacion y finalmente muestra el historial de generaciones.
     *
     * @throws ReglasException si las dimensiones del tablero estan fuera del rango permitido,
     * si el numero de generaciones o los datos iniciales no cumplen las reglas establecidas
     */
    public void jugar() {
        Tablero tablero = crearTablero();
        int numeroGeneraciones = pedirNumeroGeneraciones();
        String datosIniciales = iniciarTablero(tablero);

        vista.mostrarDatosJuego(tablero, numeroGeneraciones, datosIniciales);

        List<HistorialGeneracion> historial = simularGeneraciones(tablero, numeroGeneraciones);
        vista.mostrarHistorial(historial);
    }



    /**
     * Solicita las dimensiones del tablero y crea una nueva instancia.
     *
     * La vista muestra los mensajes correspondientes y los valores son
     * leidos desde la entrada estandar mediante {@link Keyboard}.
     *
     * @return tablero vacio con las dimensiones indicadas por el usuario
     * @throws ReglasException si las dimensiones estan fuera
     *         del rango permitido
     */
    private Tablero crearTablero() {
        int numeroFilas;
        int numeroColumnas;

        while (true) {
            try {
                vista.pedirNumeroFilas();
                numeroFilas = Keyboard.readInt();

                vista.pedirNumeroColumnas();
                numeroColumnas = Keyboard.readInt();

                return new Tablero(numeroFilas, numeroColumnas, reglas);
            } catch (ReglasException e) {
                vista.mostrarError(e.getMessage());
            }
        }
    }

    /**
     * Solicita y valida el numero de generaciones que se van a simular.
     *
     * @return numero de generaciones validado
     * @throws ReglasException si el numero de generaciones esta fuera del
     *         rango permitido
     */
    private int pedirNumeroGeneraciones() {
        while (true) {
            try {
                vista.pedirNumeroGeneraciones();
                int numeroGeneraciones = Keyboard.readInt();
                reglas.validarNumeroGeneraciones(numeroGeneraciones);
                return numeroGeneraciones;
            } catch (ReglasException e) {
                vista.mostrarError(e.getMessage());
            }
        }
    }

    /**
     * Solicita al usuario los organismos y coordenadas iniciales, y los aplica
     * al tablero.
     *
     * La vista muestra el formato esperado y este metodo obtiene la cadena
     * introducida mediante {@link Keyboard}. Si los datos no son validos (por
     * formato o porque incumplen alguna regla), se muestra el error y se vuelve
     * a pedir hasta que el tablero se inicie correctamente.
     *
     * @param tablero tablero ya creado, al que se le colocaran los organismos iniciales
     * @return datos iniciales introducidos por el usuario, ya validados
     */
    private String iniciarTablero(Tablero tablero) {
        while (true) {
            try {
                vista.pedirDatosIniciales();
                String datosIniciales = Keyboard.readString();
                tablero.iniciar(datosIniciales);
                return datosIniciales;
            } catch (ReglasException e) {
                vista.mostrarError(e.getMessage());
            }
        }
    }

    /**
     * Ejecuta las generaciones de la simulacion y registra el historial.
     *
     * En cada iteracion muestra la generacion actual, guarda sus registros
     * y comprueba si el juego debe terminar. Si puede continuar, espera que
     * el usuario presione Enter y avanza el tablero a la siguiente generacion.
     *
     * @param tablero tablero inicializado que se va a simular
     * @param numeroGeneraciones numero maximo de generaciones a ejecutar
     * @return lista con las generaciones ejecutadas durante la partida
     */
    private List<HistorialGeneracion> simularGeneraciones(Tablero tablero, int numeroGeneraciones) {
        List<HistorialGeneracion> historial = new ArrayList<>();

        for (int i = 0; i < numeroGeneraciones; i++) {

            vista.mostrarGeneracion(i + 1, tablero);

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
     * Determina si la partida debe terminar.
     *
     * El juego finaliza si todos los organismos han muerto o si la
     * generacion actual es igual a la anterior. La vista muestra el
     * motivo correspondiente.
     *
     * @param tablero tablero con la generacion actual
     * @return {@code true} si el juego debe terminar;
     *         {@code false} si puede continuar
     */
    private boolean juegoTerminado(Tablero tablero) {
        if (tablero.sinOrganismosVivos()) {
            vista.mostrarOrganismosMuertos();
            return true;
        }

        if (tablero.generacionRepetida()) {
            vista.mostrarGeneracionRepetida();
            return true;
        }
        return false;
    }

    /**
     * Pausa la simulacion hasta que el usuario presione Enter.
     *
     * La vista muestra el mensaje correspondiente y {@link Keyboard}
     * espera la entrada del usuario.
     */
    private void esperarEnter() {
        vista.pedirEnter();
        Keyboard.readLine();
    }
}