package mx.tecnm.culiacan;

import java.util.ArrayList;
import java.util.List;

/**
 * Tablero de "El Juego de la Vida".
 *
 * Guarda la matriz con el estado de cada celda (vivo o muerto), calcula los
 * vecinos y el resultado de cada celda, y avanza el juego de una generacion a
 * la siguiente. Tambien conserva la generacion anterior para poder saber si el
 * tablero dejo de cambiar.
 *
 * No interactua con el usuario: quien muestra el tablero y decide cuando
 * avanzar es ElJuegoDeLaVida. Las validaciones y el calculo del nuevo estado de
 * cada organismo los delega en Reglas.
 *
 * @see Reglas
 * @see RegistroCelda
 * @see ElJuegoDeLaVida
 */
public class Tablero {
    private final int numeroFilas;
    private final int numeroColumnas;
    private final Reglas reglas;
    private final EstadoOrganismo[][] matrizTablero;
    private EstadoOrganismo[][] matrizAnterior;
    private List<RegistroCelda> registroCeldas = List.of();

    /**
     * Crea un tablero con todas las celdas muertas.
     *
     * @param numeroFilas numero de filas, entre 2 y 20
     * @param numeroColumnas numero de columnas, entre 2 y 20
     * @param reglas reglas que se aplicaran al tablero
     * @throws ReglasException si las filas o las columnas estan fuera del rango permitido
     */
    public Tablero(int numeroFilas, int numeroColumnas, Reglas reglas) {
        reglas.validacionTablero(numeroFilas, numeroColumnas);
        this.numeroFilas = numeroFilas;
        this.numeroColumnas = numeroColumnas;
        this.reglas = reglas;
        this.matrizTablero = new EstadoOrganismo[numeroFilas][numeroColumnas];

        for (int i = 0; i < numeroFilas; i++) {
            for (int j = 0; j < numeroColumnas; j++) {
                this.matrizTablero[i][j] = EstadoOrganismo.MUERTO;
            }
        }
    }

    /**
     * Valida los datos iniciales, coloca los organismos vivos en el tablero y
     * calcula los registros de la generacion 1.
     *
     * Los datos tienen el formato "numeroOrganismos, fila1,columna1, fila2,columna2, ...".
     * Si son invalidos se lanza una excepcion antes de modificar el tablero.
     *
     * @param datosIniciales texto con la cantidad de organismos y sus coordenadas
     * @throws ReglasException si la cantidad o alguna coordenada no es valida
     */
    public void iniciar(String datosIniciales) {
        String[] datosOrganismos = datosIniciales.split(",\\s+");
        reglas.validarDatosIniciales(datosOrganismos, numeroFilas, numeroColumnas);

        for (int i = 1; i < datosOrganismos.length; i++) {
            String[] coordenadas = datosOrganismos[i].split(",");
            int fila = Integer.parseInt(coordenadas[0]);
            int columna = Integer.parseInt(coordenadas[1]);
            matrizTablero[fila][columna] = EstadoOrganismo.VIVO;
        }
        registroCeldas = calcularRegistros();
    }

    /**
     * Avanza el tablero a la siguiente generacion.
     *
     * Primero guarda una copia del tablero actual, luego aplica el resultado
     * ya calculado de cada celda y por ultimo calcula los registros de la nueva
     * generacion. El orden importa: los resultados se calcularon con el tablero
     * viejo, asi que ningun cambio afecta el conteo de vecinos de otra celda.
     */
    public void nextGeneration() {
        matrizAnterior = copiarMatriz();
        for (RegistroCelda registroCelda : registroCeldas) {
            matrizTablero[registroCelda.fila()][registroCelda.columna()] = registroCelda.resultado();
        }
        registroCeldas = calcularRegistros();
    }

    /**
     * Indica si la generacion actual es igual a la anterior.
     *
     * @return true si el tablero no cambio en el ultimo avance; false si cambio
     *         o si todavia no hay generacion anterior
     */
    public boolean generacionRepetida() {
        return reglas.validarGeneracionesIguales(matrizAnterior, matrizTablero);
    }

    /**
     * Indica si ya no queda ningun organismo vivo en el tablero.
     *
     * @return true si todas las celdas estan muertas; false en caso contrario
     */
    public boolean sinOrganismosVivos() {
        return reglas.validarTodosOrganismosMuertos(matrizTablero);
    }

    /**
     * Devuelve los registros de todas las celdas de la generacion actual, en
     * orden por fila y columna. La lista es inmutable, por lo que se puede
     * guardar en un historial sin riesgo de que cambie despues.
     *
     * @return registros de la generacion actual; lista vacia si no se ha llamado a iniciar
     */
    public List<RegistroCelda> getRegistroCeldas() {
        return registroCeldas;
    }

    /**
     * @return numero de filas del tablero
     */
    public int getNumeroFilas() {
        return numeroFilas;
    }

    /**
     * @return numero de columnas del tablero
     */
    public int getNumeroColumnas() {
        return numeroColumnas;
    }

    /**
     * Devuelve el tablero como una cuadricula de texto, con el numero de cada
     * fila y columna y el simbolo de cada organismo dentro de su celda.
     *
     * @return representacion en texto del tablero actual
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String separador = "   +" + "---+".repeat(numeroColumnas) + "\n";

        stringBuilder.append("    ");
        for (int j = 0; j < numeroColumnas; j++) {
            stringBuilder.append(String.format(" %-3d", j));
        }
        stringBuilder.append("\n").append(separador);

        for (int i = 0; i < numeroFilas; i++) {
            stringBuilder.append(String.format("%2d |", i));
            for (int j = 0; j < numeroColumnas; j++) {
                stringBuilder.append(" ").append(matrizTablero[i][j].getCaracter()).append(" |");
            }
            stringBuilder.append("\n").append(separador);
        }
        return stringBuilder.toString();
    }

    /**
     * Cuenta los organismos vivos alrededor de una celda, revisando las 8
     * direcciones y descartando las que caen fuera del tablero. Sirve igual
     * para esquinas, bordes y celdas del centro. La celda misma no se cuenta.
     *
     * @param fila fila de la celda, empezando en 0
     * @param columna columna de la celda, empezando en 0
     * @return cantidad de vecinos vivos, entre 0 y 8
     */
    private int contarVecinosVivos(int fila, int columna) {
        int vivos = 0;
        for (int definicionFila = -1; definicionFila <= 1; definicionFila++) { // -1, 0, 1
            for (int definicionColumna = -1; definicionColumna <= 1; definicionColumna++) {
                if (definicionFila == 0 && definicionColumna == 0) continue;

                int x = fila + definicionFila;
                int y = columna + definicionColumna;

                if (x >= 0 && x < numeroFilas && y >= 0 && y < numeroColumnas
                        && matrizTablero[x][y] == EstadoOrganismo.VIVO) {
                    vivos++;
                }
            }
        }
        return vivos;
    }

    /**
     * Calcula, sin modificar el tablero, el registro de cada celda: su estado
     * actual, sus vecinos vivos y el estado que tendra en la siguiente
     * generacion segun las reglas.
     *
     * @return lista inmutable con un registro por celda, en orden por fila y columna
     */
    private List<RegistroCelda> calcularRegistros() {
        List<RegistroCelda> registros = new ArrayList<>();
        for (int i = 0; i < numeroFilas; i++) {
            for (int j = 0; j < numeroColumnas; j++) {
                EstadoOrganismo actual = matrizTablero[i][j];
                int vecinos = contarVecinosVivos(i, j);
                EstadoOrganismo resultado = reglas.establecerNuevoEstadoOrganismo(actual, vecinos);
                registros.add(new RegistroCelda(i, j, actual, vecinos, resultado));
            }
        }
        return List.copyOf(registros);
    }

    /**
     * Crea una copia independiente de la matriz actual, fila por fila. Si solo
     * se asignara la referencia, la copia y el tablero apuntarian al mismo
     * arreglo y siempre parecerian iguales.
     *
     * @return copia de la matriz del tablero
     */
    private EstadoOrganismo[][] copiarMatriz() {
        EstadoOrganismo[][] copia = new EstadoOrganismo[numeroFilas][];
        for (int i = 0; i < numeroFilas; i++) {
            copia[i] = matrizTablero[i].clone();
        }
        return copia;
    }
}