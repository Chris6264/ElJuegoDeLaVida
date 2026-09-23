package mx.tecnm.culiacan;

/**
 * Contrato con las reglas de "El Juego de la Vida".
 *
 * Reune dos tipos de reglas:
 *
 *   - Validaciones de los datos que escribe el usuario: numero de generaciones, datos iniciales y coordenadas.
 *   - Reglas de la simulacion: como cambia el estado de un organismo y cuando termina el juego.
 *
 * {@link ElJuegoDeLaVida} y {@link Tablero} dependen de esta interfaz y no de
 * una implementacion concreta, asi que se pueden usar otras reglas sin
 * modificarlos. La implementacion actual es {@link ReglasDelJuego}.
 *
 */
public interface Reglas {

    /**
     * Verifica que las dimensiones del tablero esten entre 2 y 20.
     * @param numeroFilas numero de filas del tablero
     * @param numeroColumnas numero de columnas del tablero
     * @throws ReglasException si los datos del tablero son incorrectos
     */
    void validacionTablero(int numeroFilas, int numeroColumnas);
    /**
     * Verifica que el numero de generaciones este dentro del rango permitido.
     *
     * @param numeroGeneraciones numero de generaciones solicitado por el usuario
     * @throws ReglasException si el numero esta fuera del rango permitido
     */
    void validarNumeroGeneraciones(int numeroGeneraciones);

    /**
     * Verifica los datos iniciales ya separados por organismo.
     *
     * El primer elemento es la cantidad de organismos y cada elemento
     * siguiente es una coordenada con el formato {@code fila,columna}.
     * Se comprueba que la cantidad sea valida, que coincida con las
     * coordenadas recibidas y que cada coordenada este dentro del tablero.
     *
     * @param datosOrganismos cantidad de organismos seguida de sus coordenadas, por ejemplo {@code ["2", "0,1", "3,4"]}
     * @param numeroFilas numero de filas del tablero
     * @param numeroColumnas numero de columnas del tablero
     * @throws ReglasException si la cantidad de organismos o alguna coordenada no es valida
     */
    void validarDatosIniciales(String[] datosOrganismos, int numeroFilas, int numeroColumnas);

    /**
     * Verifica que una coordenada este dentro del tablero.
     *
     * @param fila fila de la coordenada, empezando en 0
     * @param columna columna de la coordenada, empezando en 0
     * @param numeroFilas numero de filas del tablero
     * @param numeroColumnas numero de columnas del tablero
     * @throws ReglasException si la fila o la columna estan fuera del tablero
     */
    void validarCoordenadas(int fila, int columna, int numeroFilas, int numeroColumnas);

    /**
     * Calcula el estado que tendra un organismo en la siguiente generacion.
     *
     *   - Un organismo vivo con menos de 2 o mas de 3 vecinos muere.
     *   - Un organismo vivo con 2 o 3 vecinos sigue vivo.
     *   - Una celda muerta con exactamente 3 vecinos vivos nace.
     *   - En cualquier otro caso, la celda conserva su estado.
     *
     * @param estadoOrganismo estado actual de la celda
     * @param numeroVecinos cantidad de vecinos vivos que rodean a la celda
     * @return estado de la celda en la siguiente generacion
     */
    EstadoOrganismo establecerNuevoEstadoOrganismo(EstadoOrganismo estadoOrganismo, int numeroVecinos);

    /**
     * Indica si dos generaciones tienen exactamente el mismo estado en todas
     * sus celdas.
     *
     * @param generacionAnterior matriz de la generacion anterior; puede ser {@code null} si no existe
     * @param generacionActual matriz de la generacion actual
     * @return {@code true} si ambas matrices son iguales; {@code false} si difieren o si no hay generacion anterior
     */
    boolean validarGeneracionesIguales(EstadoOrganismo[][] generacionAnterior, EstadoOrganismo[][] generacionActual);

    /**
     * Indica si en la generacion no queda ningun organismo vivo.
     *
     * @param generacionActual matriz de la generacion actual
     * @return {@code true} si todas las celdas estan muertas; {@code false} si hay al menos un organismo vivo
     */
    boolean validarTodosOrganismosMuertos(EstadoOrganismo[][] generacionActual);
}