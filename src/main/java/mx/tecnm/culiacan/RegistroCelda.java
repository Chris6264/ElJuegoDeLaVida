package mx.tecnm.culiacan;

/**
 * Registro de una celda dentro de una generacion.
 * Cada instancia describe una sola celda:
 * su posicion, el estado que tiene en la generacion actual, cuantos vecinos
 * vivos la rodean y el estado en que se convierte en la generacion siguiente.
 * {@link Tablero} crea uno por cada celda y {@link HistorialGeneracion} los
 * agrupa por generacion.
 *
 * @param fila fila de la celda, empezando en 0
 * @param columna columna de la celda, empezando en 0
 * @param estado estado de la celda en la generacion actual
 * @param vecinos cantidad de vecinos vivos que tiene la celda
 * @param resultado estado que tendra la celda en la generacion siguiente
 */
public record RegistroCelda(int fila, int columna, EstadoOrganismo estado, int vecinos, EstadoOrganismo resultado) { }