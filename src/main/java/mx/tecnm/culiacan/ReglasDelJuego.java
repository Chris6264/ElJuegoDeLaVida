package mx.tecnm.culiacan;

import java.util.Arrays;

/**
 * Implementacion de las reglas clasicas de "El Juego de la Vida".
 *
 * Limites que aplica esta version:
 * - El numero de generaciones debe estar entre 1 y 50.
 * - La cantidad de organismos iniciales no puede superar la mitad de las
 *   celdas del tablero.
 * - Cada coordenada debe estar dentro del tablero.
 *
 * Reglas de evolucion de un organismo:
 * - Vivo con menos de 2 vecinos: muere por soledad.
 * - Vivo con 2 o 3 vecinos: sobrevive.
 * - Vivo con mas de 3 vecinos: muere por sobrepoblacion.
 * - Muerto con exactamente 3 vecinos: nace.
 *
 * Esta clase no guarda estado, por lo que una misma instancia se puede
 * compartir entre ElJuegoDeLaVida y Tablero.
 *
 * @see Reglas
 * @see ReglasException
 */
public class ReglasDelJuego implements Reglas{

    /**
     * Verifica que el numero de generaciones este entre 1 y 50.
     *
     * @param numeroGeneraciones numero de generaciones solicitado por el usuario
     * @throws ReglasException si el numero es menor que 1 o mayor que 50
     */
    @Override
    public void validarNumeroGeneraciones(int numeroGeneraciones) {
        if(numeroGeneraciones > 50 || numeroGeneraciones < 1) throw new ReglasException("El numero de generaciones " +
                "debe estar entre 1 y 50");
    }

    /**
     * Verifica los datos iniciales ya separados por organismo.
     *
     * El primer elemento es la cantidad de organismos y cada elemento siguiente
     * es una coordenada con el formato fila,columna. Se comprueba, en este
     * orden, que:
     * 1. La cantidad no sea negativa ni supere la mitad de las celdas del tablero.
     * 2. La cantidad indicada coincida con el numero de coordenadas recibidas.
     * 3. Cada coordenada este dentro del tablero.
     *
     * @param datosOrganismos cantidad de organismos seguida de sus coordenadas, por ejemplo ["2", "0,1", "3,4"]
     * @param numeroFilas numero de filas del tablero
     * @param numeroColumnas numero de columnas del tablero
     * @throws ReglasException si la cantidad, su coincidencia con las coordenadas o alguna coordenada no es valida
     */
    @Override
    public void validarDatosIniciales(String[] datosOrganismos, int numeroFilas, int numeroColumnas) {
        int cantidadOrganismo = Integer.parseInt(datosOrganismos[0]) , capacidadTablero = (numeroFilas * numeroColumnas), fila , columna;

        if(cantidadOrganismo > (capacidadTablero / 2) || cantidadOrganismo < 0){
            throw new ReglasException("La cantidad de organismos debe ser de 0 a " + capacidadTablero / 2);
        }

        if (datosOrganismos.length - 1 != cantidadOrganismo) {
            throw new ReglasException("Se indicaron " + cantidadOrganismo + " organismos pero se dieron " + (datosOrganismos.length - 1));
        }

        for (int i = 1; i < datosOrganismos.length; i++) {
            String[] coordenadas = datosOrganismos[i].split(",");
            fila = Integer.parseInt(coordenadas[0]);
            columna = Integer.parseInt(coordenadas[1]);
            validarCoordenadas(fila,columna,numeroFilas,numeroColumnas);
        }
    }

    /**
     * Verifica que una coordenada este dentro del tablero.
     *
     * Los indices validos van de 0 a numeroFilas - 1 para la fila y de
     * 0 a numeroColumnas - 1 para la columna.
     *
     * @param fila fila de la coordenada, empezando en 0
     * @param columna columna de la coordenada, empezando en 0
     * @param numeroFilas numero de filas del tablero
     * @param numeroColumnas numero de columnas del tablero
     * @throws ReglasException si la fila o la columna estan fuera del tablero
     */
    @Override
    public void validarCoordenadas(int fila, int columna, int numeroFilas, int numeroColumnas) {
        int maxFila = (numeroFilas - 1), maxColumna = (numeroColumnas - 1);
        if(fila < 0 || fila > maxFila) throw new ReglasException("La posicion de la fila debe ser entre 0 y " + maxFila);
        if(columna < 0 || columna > maxColumna) throw new ReglasException("La posicion de la columna debe ser entre 0 y " + maxColumna);
    }

    /**
     * Calcula el estado que tendra un organismo en la siguiente generacion
     * segun las reglas de Conway:
     * - Vivo con menos de 2 vecinos: muere por soledad.
     * - Vivo con mas de 3 vecinos: muere por sobrepoblacion.
     * - Muerto con exactamente 3 vecinos: nace.
     * - En cualquier otro caso conserva su estado (un vivo con 2 o 3 vecinos sobrevive).
     *
     * @param estadoOrganismo estado actual de la celda
     * @param numeroVecinos cantidad de vecinos vivos que rodean a la celda
     * @return estado de la celda en la siguiente generacion
     */
    @Override
    public EstadoOrganismo establecerNuevoEstadoOrganismo(EstadoOrganismo estadoOrganismo, int numeroVecinos){
        if(estadoOrganismo == EstadoOrganismo.VIVO && numeroVecinos < 2) return EstadoOrganismo.MUERTO;
        if(estadoOrganismo == EstadoOrganismo.VIVO && numeroVecinos > 3) return EstadoOrganismo.MUERTO;
        if(estadoOrganismo == EstadoOrganismo.MUERTO && numeroVecinos == 3) return EstadoOrganismo.VIVO;
        return estadoOrganismo;
    }

    /**
     * Indica si dos generaciones tienen el mismo estado en todas sus celdas.
     *
     * Usa Arrays.deepEquals, que compara el contenido de las matrices celda
     * por celda. Un equals normal en arreglos solo compara referencias.
     *
     * @param generacionAnterior matriz de la generacion anterior; puede ser null si no existe
     * @param generacionActual matriz de la generacion actual
     * @return true si ambas matrices son iguales; false si difieren o si no hay generacion anterior
     */
    @Override
    public boolean validarGeneracionesIguales(EstadoOrganismo[][] generacionAnterior, EstadoOrganismo[][] generacionActual) {
        return Arrays.deepEquals(generacionAnterior, generacionActual);
    }

    /**
     * Indica si en la generacion no queda ningun organismo vivo.
     *
     * Recorre la matriz y termina en cuanto encuentra el primer organismo vivo.
     *
     * @param generacionActual matriz de la generacion actual
     * @return true si todas las celdas estan muertas; false si hay al menos un organismo vivo
     */
    @Override
    public boolean validarTodosOrganismosMuertos(EstadoOrganismo[][] generacionActual) {
        for (EstadoOrganismo[] fila : generacionActual) {
            for (EstadoOrganismo estado : fila) {
                if (estado == EstadoOrganismo.VIVO) return false;
            }
        }
        return true;
    }
}