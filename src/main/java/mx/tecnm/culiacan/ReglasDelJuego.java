package mx.tecnm.culiacan;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ReglasDelJuego implements Reglas{
    @Override
    public void validarNumeroGeneraciones(int numeroGeneraciones) {
        if(numeroGeneraciones > 50 || numeroGeneraciones < 1) throw new ReglasException("El numero de generaciones " +
                "debe estar entre 1 y 50");
    }

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

    @Override
    public void validarCoordenadas(int fila, int columna, int numeroFilas, int numeroColumnas) {
        int maxFila = (numeroFilas - 1), maxColumna = (numeroColumnas - 1);
        if(fila < 0 || fila > maxFila) throw new ReglasException("La posicion de la fila debe ser entre 0 y " + maxFila);
        if(columna < 0 || columna > maxColumna) throw new ReglasException("La posicion de la columna debe ser entre 0 y " + maxColumna);
    }

    @Override
    public EstadoOrganismo establecerNuevoEstadoOrganismo(EstadoOrganismo estadoOrganismo, int numeroVecinos){
        if(estadoOrganismo == EstadoOrganismo.VIVO && numeroVecinos < 2) return EstadoOrganismo.MUERTO;
        if(estadoOrganismo == EstadoOrganismo.VIVO && numeroVecinos > 3) return EstadoOrganismo.MUERTO;
        if(estadoOrganismo == EstadoOrganismo.MUERTO && numeroVecinos == 3) return EstadoOrganismo.VIVO;
        return estadoOrganismo;
    }

    @Override
    public boolean validarGeneracionesIguales(EstadoOrganismo[][] generacionAnterior, EstadoOrganismo[][] generacionActual) {
        return Arrays.deepEquals(generacionAnterior, generacionActual);
    }

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