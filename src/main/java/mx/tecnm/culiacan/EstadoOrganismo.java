package mx.tecnm.culiacan;
/**
 * Representa el estado de un organismo en el tablero.
 * Cada estado esta relacionado a un caracter.
 */
public enum EstadoOrganismo {
    /** Indica que el organismo esta vivo. */
    VIVO('V'),

    /** Indica que el organismo esta muerto. */
    MUERTO('.');

    private final char caracter;

    EstadoOrganismo(char caracter) {
        this.caracter = caracter;
    }

    /**
     * Obtiene el caracter que representa el estado.
     *
     * @return El caracter correspondiente ('*' o '.').
     */
    public char getCaracter() { return caracter; }
}