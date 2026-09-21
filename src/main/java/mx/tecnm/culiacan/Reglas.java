package mx.tecnm.culiacan;

public interface Reglas {
    void validarNumeroGeneraciones(int numeroGeneraciones);
    void validarDatosIniciales(String[] datosOrganismos, int numeroFilas, int numeroColumnas);
    void validarCoordenadas(int fila, int columna, int numeroFilas, int numeroColumnas);
    EstadoOrganismo establecerNuevoEstadoOrganismo(EstadoOrganismo estadoOrganismo, int numeroVecinos);
    boolean validarGeneracionesIguales(EstadoOrganismo[][] generacionAnterior, EstadoOrganismo[][] generacionActual);
    boolean validarTodosOrganismosMuertos(EstadoOrganismo[][] generacionActual);
}