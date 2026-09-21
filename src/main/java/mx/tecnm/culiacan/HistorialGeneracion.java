package mx.tecnm.culiacan;

import java.util.List;

/**
 * Registro de una generacion del juego.
 *
 * Guarda el numero de la generacion y los datos de todas sus celdas: el estado
 * que tenia cada una, sus vecinos vivos y el estado en que se convierte en la
 * siguiente generacion.
 *
 * @param numero numero de la generacion
 * @param historialMovimientos registros de todas las celdas de esa generacion, en orden por fila y columna
 */
public record HistorialGeneracion(int numero, List<RegistroCelda> historialMovimientos) { }