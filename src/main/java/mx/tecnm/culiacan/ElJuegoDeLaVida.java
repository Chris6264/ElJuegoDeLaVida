package mx.tecnm.culiacan;

import java.util.ArrayList;
import java.util.List;

public class ElJuegoDeLaVida {
    private static final String BORDE_SUPERIOR = "+" + "-".repeat(45) + "+";
    private static final String SEPARADOR = "+" + "-".repeat(12) + "+" + "-".repeat(10) + "+"
            + "-".repeat(9) + "+" + "-".repeat(11) + "+";

    private final Reglas reglas;

    public ElJuegoDeLaVida(Reglas reglas) {
        this.reglas = reglas;
    }

    public void jugar() {
        Tablero tablero = crearTablero();
        int numeroGeneraciones = pedirNumeroGeneraciones();
        String datosIniciales = pedirDatosIniciales();

        tablero.iniciar(datosIniciales);
        imprimirSalida(tablero, numeroGeneraciones, datosIniciales);

        List<HistorialMovimientos> historial = simularGeneraciones(tablero, numeroGeneraciones);
        imprimirHistorial(historial);
    }

    private Tablero crearTablero() {
        System.out.print("Escriba numero de filas: ");
        int numeroFilas = Keyboard.readInt();
        System.out.print("Escriba numero de columnas: ");
        int numeroColumnas = Keyboard.readInt();
        return new Tablero(numeroFilas, numeroColumnas, reglas);
    }

    private int pedirNumeroGeneraciones() {
        System.out.print("Escriba numero de generaciones: ");
        int numeroGeneraciones = Keyboard.readInt();
        reglas.validarNumeroGeneraciones(numeroGeneraciones);
        return numeroGeneraciones;
    }

    private String pedirDatosIniciales() {
        System.out.println();
        System.out.println("Escriba los datos iniciales");
        System.out.println("Formato: numeroOrganismos, x1,y1, x2,y2, ...");
        System.out.println("Ejemplo: 2, 0,1, 3,4 ");
        System.out.print("-> ");
        return Keyboard.readString();
    }

    private List<HistorialMovimientos> simularGeneraciones(Tablero tablero, int numeroGeneraciones) {
        List<HistorialMovimientos> historial = new ArrayList<>();

        for (int i = 0; i < numeroGeneraciones; i++) {
            System.out.println("Generacion #" + (i + 1));
            System.out.println(tablero);
            historial.add(new HistorialMovimientos(i + 1, tablero.getRegistrosGeneracion()));

            if (juegoTerminado(tablero)) break;

            if (i < numeroGeneraciones - 1) {
                esperarEnter();
                tablero.nextGeneration();
            }
        }
        return historial;
    }

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

    private void esperarEnter() {
        System.out.print("Presione Enter para continuar...");
        while (Boolean.parseBoolean(Keyboard.readLine()));
    }

    private void imprimirSalida(Tablero tablero, int numeroGeneraciones, String datosIniciales) {
        System.out.println();
        System.out.println("Datos del juego: Numero De Filas: " + tablero.getNumeroFilas() + " , "
                + "Numero De Columnas: " + tablero.getNumeroColumnas() + " , "
                + "Numero De Generaciones: " + numeroGeneraciones + "\n");
        System.out.println("Organismo vivos en celdas");
        System.out.println(formatearCoordenadas(datosIniciales) + "\n");
    }

    private String formatearCoordenadas(String datosIniciales) {
        StringBuilder cadenaCoordenadas = new StringBuilder();
        String[] datos = datosIniciales.split(",\\s+");

        for (int i = 1; i < datos.length; i++) {
            cadenaCoordenadas.append("Organismo # ").append(i).append(": ")
                    .append("(").append(datos[i]).append(") ");
        }
        return cadenaCoordenadas.toString();
    }

    private void imprimirHistorial(List<HistorialMovimientos> historial) {
        System.out.println("========== HISTORIAL DE MOVIMIENTOS ==========");
        for (HistorialMovimientos generacion : historial) {
            imprimirTablaGeneracion(generacion);
        }
    }

    private void imprimirTablaGeneracion(HistorialMovimientos generacion) {
        System.out.println();
        System.out.println(BORDE_SUPERIOR);
        System.out.printf("| %-43s |%n", "Generacion #" + generacion.numero());
        System.out.println(SEPARADOR);
        System.out.printf("| %-10s | %-8s | %7s | %-9s |%n", "Posicion", "Estado", "Vecinos", "Resultado");
        System.out.println(SEPARADOR);

        for (RegistroGeneracion r : generacion.historialMovimientos()) {
            System.out.printf("| %-10s | %-8s | %7d | %-9s |%n",
                    r.fila() + "," + r.columna(), r.estado(), r.vecinos(), r.resultado());
        }
        System.out.println(SEPARADOR);
    }
}