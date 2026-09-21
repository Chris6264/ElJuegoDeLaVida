package mx.tecnm.culiacan;

public class App {
    public static void main(String[] args){
        Reglas reglas = new ReglasDelJuego();
        ElJuegoDeLaVida elJuegoDeLaVida = new ElJuegoDeLaVida(reglas);
        elJuegoDeLaVida.jugar();
    }
}
