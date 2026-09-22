# El Juego de la Vida

Implementacion en Java del Juego de la Vida de Conway, ejecutado por consola.
El usuario define el tamano del tablero, el numero de generaciones y los
organismos iniciales; el programa simula la evolucion del tablero y muestra
un historial de movimientos al final de la partida.

## Como se juega

Al ejecutar el programa se piden, en este orden:

1. **Numero de filas** del tablero (entre 2 y 20).
2. **Numero de columnas** del tablero (entre 2 y 20).
3. **Numero de generaciones** a simular (entre 1 y 50).
4. **Datos iniciales**: la cantidad de organismos vivos y sus coordenadas.

### Formato de los datos iniciales

```
numeroOrganismos, fila1,columna1, fila2,columna2, ...
```

Ejemplo, para 2 organismos en (0,1) y (3,4):

```
2, 0,1, 3,4
```

Despues de mostrar cada generacion, el programa espera que el usuario
presione Enter para avanzar a la siguiente.

## Reglas de evolucion

Cada celda cambia de estado segun sus vecinos vivos (8 celdas alrededor):

- Un organismo **vivo** con menos de 2 vecinos muere (soledad).
- Un organismo **vivo** con 2 o 3 vecinos sobrevive.
- Un organismo **vivo** con mas de 3 vecinos muere (sobrepoblacion).
- Una celda **muerta** con exactamente 3 vecinos nace.

## Condiciones de fin del juego

La simulacion termina antes de llegar al numero de generaciones pedido si:

- **Todos los organismos mueren** (extincion).
- **Una generacion es igual a la anterior** (el tablero se estabilizo).

Si ninguna de las dos ocurre, el juego termina al alcanzar el numero de
generaciones indicado por el usuario.

> **Nota:** la deteccion de "generacion repetida" solo compara con la
> generacion inmediata anterior. Patrones ciclicos como el *blinker*, que
> alternan entre dos formas indefinidamente, no se detectan como fin del
> juego y la simulacion corre hasta el numero de generaciones solicitado.

## Historial de movimientos

Al finalizar la partida se imprime una tabla por cada generacion mostrada,
con el detalle de todas las celdas: posicion, estado actual, cantidad de
vecinos vivos y el resultado (estado en la siguiente generacion).

## Estructura del proyecto

```
mx.tecnm.culiacan
├── App                          Punto de entrada; arma las dependencias
├── ElJuegoDeLaVida               Controla el flujo de la partida
├── VistaConsola                  Muestra toda la informacion en consola
├── Tablero                       Mantiene el estado del tablero y lo evoluciona
├── Reglas / ReglasDelJuego       Validaciones y reglas de evolucion
├── EstadoOrganismo (enum)        VIVO / MUERTO
├── RegistroCelda (record)        Estado, vecinos y resultado de una celda
├── HistorialGeneracion (record)  Numero de generacion y sus registros
├── ReglasException               Error en datos del usuario
├── ValidacionTableroException    Error en las dimensiones del tablero
└── Keyboard                      Lectura de entrada por consola (Lewis & Loftus)
```

### Diseno

- **`App`** es el unico lugar donde se crean las implementaciones concretas
  (`ReglasDelJuego`, `VistaConsola`) e inyecta las dependencias en
  `ElJuegoDeLaVida`.
- **`ElJuegoDeLaVida`** depende de la interfaz `Reglas`, no de una
  implementacion concreta, y delega toda la impresion en `VistaConsola`.
- **`Tablero`** no interactua con el usuario ni imprime nada: solo mantiene el
  estado y calcula la siguiente generacion.
- **`RegistroCelda`** y **`HistorialGeneracion`** son `record`, ya que
  representan datos inmutables que solo se consultan.

## Limites y validaciones

| Dato | Rango permitido |
|---|---|
| Filas | 2 a 20 |
| Columnas | 2 a 20 |
| Generaciones | 1 a 50 |
| Organismos iniciales | 0 a la mitad de las celdas del tablero |

Si algun dato no cumple estos limites, se lanza `ValidacionTableroException`
(dimensiones del tablero) o `ReglasException` (generaciones o datos
iniciales) con un mensaje que explica el motivo.

## Limitaciones conocidas

- La validacion de los datos iniciales no captura errores de formato: si el
  usuario escribe texto no numerico o una coordenada sin coma, el programa
  finaliza con una excepcion sin capturar.
- No se valida que las coordenadas no se repitan (por ejemplo,
  `2, 0,1, 0,1` indica 2 organismos pero solo coloca 1 con vida).
- Solo se detecta como "fin del juego" la repeticion contra la generacion
  inmediata anterior, no contra generaciones anteriores en general.

## Ejemplos para probar

**Extincion** (organismos aislados, mueren por soledad):
```
2, 1,1, 1,2
```

**Generacion repetida** (bloque estable de 2x2):
```
4, 1,1, 1,2, 2,1, 2,2
```

**Corre hasta el limite de generaciones**
```
3, 1,0, 1,1, 1,2
```

## Requisitos

- JDK 17 o superior (se usan `record`)
