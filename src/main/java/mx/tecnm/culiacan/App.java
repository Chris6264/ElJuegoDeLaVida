package mx.tecnm.culiacan;

/**
 * Punto de entrada del programa "El Juego de la Vida".
 *
 * Se encarga de armar las dependencias y de iniciar la partida.
 *
 */
public class App {
    /**
     * Crea las reglas y la vista del juego se las entrega a ElJuegoDeLaVida y comienza la partida.
     *
     * @param args argumentos de linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        Reglas reglas = new ReglasDelJuego();
        VistaConsola vistaConsola = new VistaConsola();
        ElJuegoDeLaVida elJuegoDeLaVida = new ElJuegoDeLaVida(reglas,vistaConsola);
        elJuegoDeLaVida.jugar();
    }
}