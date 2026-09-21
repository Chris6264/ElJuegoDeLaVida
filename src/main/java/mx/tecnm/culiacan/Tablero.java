package mx.tecnm.culiacan;

import java.util.ArrayList;
import java.util.List;

public class Tablero {
    private final int numeroFilas;
    private final int numeroColumnas;
    private final Reglas reglas;
    private final EstadoOrganismo[][] matrizTablero;
    private EstadoOrganismo[][] matrizAnterior;
    private List<RegistroGeneracion> registrosGeneracion = List.of();

    public Tablero(int numeroFilas, int numeroColumnas, Reglas reglas) {
        validacionTablero(numeroFilas, numeroColumnas);
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

    public void iniciar(String datosIniciales) {
        String[] datosOrganismos = datosIniciales.split(",\\s+");
        reglas.validarDatosIniciales(datosOrganismos, numeroFilas, numeroColumnas);

        for (int i = 1; i < datosOrganismos.length; i++) {
            String[] coordenadas = datosOrganismos[i].split(",");
            int fila = Integer.parseInt(coordenadas[0]);
            int columna = Integer.parseInt(coordenadas[1]);
            matrizTablero[fila][columna] = EstadoOrganismo.VIVO;
        }
        registrosGeneracion = calcularRegistros();
    }

    public void nextGeneration() {
        matrizAnterior = copiarMatriz();
        for (RegistroGeneracion registroGeneracion : registrosGeneracion) {
            matrizTablero[registroGeneracion.fila()][registroGeneracion.columna()] = registroGeneracion.resultado();
        }
        registrosGeneracion = calcularRegistros();
    }

    public boolean generacionRepetida() {
        return reglas.validarGeneracionesIguales(matrizAnterior, matrizTablero);
    }

    public boolean sinOrganismosVivos() {
        return reglas.validarTodosOrganismosMuertos(matrizTablero);
    }

    public List<RegistroGeneracion> getRegistrosGeneracion() {
        return registrosGeneracion;
    }

    public int getNumeroFilas() {
        return numeroFilas;
    }

    public int getNumeroColumnas() {
        return numeroColumnas;
    }

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

    private int contarVecinosVivos(int fila, int columna) {
        int vivos = 0;
        for (int definicionFila = -1; definicionFila <= 1; definicionFila++) {
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

    private List<RegistroGeneracion> calcularRegistros() {
        List<RegistroGeneracion> registros = new ArrayList<>();
        for (int i = 0; i < numeroFilas; i++) {
            for (int j = 0; j < numeroColumnas; j++) {
                EstadoOrganismo actual = matrizTablero[i][j];
                int vecinos = contarVecinosVivos(i, j);
                EstadoOrganismo resultado = reglas.establecerNuevoEstadoOrganismo(actual, vecinos);
                registros.add(new RegistroGeneracion(i, j, actual, vecinos, resultado));
            }
        }
        return List.copyOf(registros);
    }

    private void validacionTablero(int numeroFilas, int numeroColumnas) {
        if (numeroFilas > 20 || numeroFilas < 2) throw new ValidacionTableroException("El rango de filas debe ser entre 2 y 20.");
        if (numeroColumnas > 20 || numeroColumnas < 2) throw new ValidacionTableroException("El rango de columnas debe ser entre 2 y 20.");
    }

    private EstadoOrganismo[][] copiarMatriz() {
        EstadoOrganismo[][] copia = new EstadoOrganismo[numeroFilas][];
        for (int i = 0; i < numeroFilas; i++) {
            copia[i] = matrizTablero[i].clone();
        }
        return copia;
    }
}