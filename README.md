# El Juego de la Vida

Implementación en Java del **Juego de la Vida** de John Conway, ejecutada por consola. El proyecto está diseñado con un enfoque orientado a objetos, separando claramente las responsabilidades de entrada/salida, reglas del juego y estado del tablero (principio de responsabilidad única y bajo acoplamiento mediante interfaces).

Paquete: `mx.tecnm.culiacan`

---

## Descripción general

El programa simula la evolución de organismos en un tablero de celdas (filas × columnas). El usuario define:
1. El tamaño del tablero (entre 2 y 20 filas/columnas).
2. El número de generaciones a simular (entre 1 y 50).
3. Los organismos vivos iniciales, indicando sus coordenadas.

En cada generación se aplican las reglas clásicas de Conway para determinar qué celdas viven, mueren o nacen. La simulación termina cuando:
- Todos los organismos mueren.
- Una generación es idéntica a la anterior (estado estable/ciclo).
- Se alcanza el número máximo de generaciones solicitado.

Al finalizar, se muestra un historial detallado con el estado de cada celda en cada generación.

---

## Arquitectura y responsabilidades

El diseño sigue una separación de capas:

- **Entrada/Salida** → `Keyboard`, `VistaConsola`
- **Control de flujo del juego** → `ElJuegoDeLaVida`
- **Estado y lógica del tablero** → `Tablero`
- **Reglas del juego (contrato + implementación)** → `Reglas`, `ReglasDelJuego`
- **Modelos de datos inmutables (records)** → `RegistroCelda`, `HistorialGeneracion`
- **Tipos de dominio** → `EstadoOrganismo`
- **Manejo de errores** → `ReglasException`
- **Punto de entrada** → `App`

`ElJuegoDeLaVida` y `Tablero` dependen de la interfaz `Reglas`, no de `ReglasDelJuego` directamente, lo que permite sustituir el conjunto de reglas sin modificar el resto del código (inversión de dependencias).

---

## Descripción de cada clase

### `App`
Punto de entrada del programa (método `main`). Se encarga de **armar las dependencias**: crea una instancia de `ReglasDelJuego`, una `VistaConsola`, las inyecta en `ElJuegoDeLaVida` y llama a `jugar()` para iniciar la partida.

### `ElJuegoDeLaVida`
Es el **controlador principal** de la aplicación. Coordina todo el flujo de una partida:
- Solicita las dimensiones del tablero y crea el `Tablero` (con reintento si hay errores de validación).
- Pide el número de generaciones a simular.
- Solicita los datos iniciales de los organismos y los aplica al tablero.
- Muestra el resumen inicial mediante `VistaConsola`.
- Ejecuta el ciclo de generaciones (`simularGeneraciones`), registrando cada una en una lista de `HistorialGeneracion`.
- Determina cuándo debe terminar el juego (`juegoTerminado`): sin organismos vivos o generación repetida.
- Pausa entre generaciones esperando que el usuario presione Enter.
- Al final, muestra el historial completo.

No contiene lógica de reglas ni de presentación directa: delega ambas cosas en `Reglas` y `VistaConsola` respectivamente.

### `Tablero`
Representa el **estado del tablero** y contiene la lógica de simulación:
- Se construye con un número de filas/columnas (validado por `Reglas`) y todas las celdas inician muertas.
- `iniciar(String datosIniciales)`: valida y coloca los organismos vivos iniciales, luego calcula los registros de la generación 1.
- `nextGeneration()`: guarda una copia de la matriz actual (para poder comparar generaciones), aplica los resultados ya calculados a cada celda y recalcula los registros de la nueva generación.
- `contarVecinosVivos(fila, columna)`: cuenta los vecinos vivos en las 8 direcciones alrededor de una celda, descartando las que caen fuera del tablero (privado).
- `calcularRegistros()`: genera, para cada celda, un `RegistroCelda` con su estado actual, vecinos vivos y el estado que tendrá en la siguiente generación, usando `Reglas.establecerNuevoEstadoOrganismo`.
- `generacionRepetida()` y `sinOrganismosVivos()`: delegan en `Reglas` la comparación de generaciones y la verificación de organismos vivos.
- `toString()`: genera una representación en texto (cuadrícula) del tablero, con índices de filas/columnas.

No interactúa directamente con el usuario; esa responsabilidad es de `ElJuegoDeLaVida` y `VistaConsola`.

### `Reglas` (interfaz)
Define el **contrato de reglas** del juego, dividido en dos grupos:
1. **Validaciones de datos del usuario**: dimensiones del tablero, número de generaciones, datos iniciales y coordenadas.
2. **Reglas de simulación**: cómo evoluciona el estado de un organismo y cuándo termina el juego (generaciones iguales, todos muertos).

Gracias a esta interfaz, `ElJuegoDeLaVida` y `Tablero` no dependen de una implementación concreta.

### `ReglasDelJuego`
**Implementación concreta** de `Reglas` con las reglas clásicas de Conway:
- Tablero: entre 2 y 20 filas/columnas.
- Generaciones: entre 1 y 50.
- Organismos iniciales: la cantidad no puede superar la mitad de las celdas del tablero, y debe coincidir con las coordenadas proporcionadas; cada coordenada debe estar dentro del tablero.
- Evolución de un organismo (`establecerNuevoEstadoOrganismo`):
  - Vivo con menos de 2 vecinos → muere (soledad).
  - Vivo con más de 3 vecinos → muere (sobrepoblación).
  - Muerto con exactamente 3 vecinos → nace.
  - En cualquier otro caso, conserva su estado (vivo con 2 o 3 vecinos sobrevive).
- `validarGeneracionesIguales`: compara el texto de dos generaciones.
- `validarTodosOrganismosMuertos`: revisa si la representación del tablero contiene algún organismo vivo (`'V'`).

Es una clase sin estado propio, por lo que una misma instancia puede compartirse entre `ElJuegoDeLaVida` y `Tablero`.

### `ReglasException`
**Excepción no verificada** (extiende `RuntimeException`) que se lanza cuando los datos del usuario incumplen alguna regla (dimensiones fuera de rango, cantidad de organismos inválida, coordenadas fuera del tablero, etc.). Al ser unchecked, los métodos que la lanzan no necesitan declararla con `throws`, y quien inicia el juego puede capturarla para mostrar solo el mensaje (`getMessage()`) en vez del stack trace completo.

### `EstadoOrganismo` (enum)
Representa el **estado de una celda**:
- `VIVO` → representado con el carácter `'V'`.
- `MUERTO` → representado con el carácter `'.'`.

Cada valor tiene asociado un carácter (`getCaracter()`) usado para dibujar el tablero en consola.

### `RegistroCelda` (record)
Modelo de datos **inmutable** que describe una sola celda dentro de una generación:
- `fila`, `columna`: posición de la celda.
- `estado`: estado actual (`EstadoOrganismo`).
- `vecinos`: cantidad de vecinos vivos.
- `resultado`: estado que tendrá en la siguiente generación.

`Tablero` crea uno por cada celda; `HistorialGeneracion` los agrupa por generación.

### `HistorialGeneracion` (record)
Modelo de datos **inmutable** que representa una generación completa:
- `numero`: número de la generación.
- `historialMovimientos`: lista de `RegistroCelda` de todas las celdas de esa generación, en orden por fila y columna.

### `VistaConsola`
Responsable **exclusivamente de la presentación** en consola. No contiene lógica del juego ni modifica el tablero. Sus métodos principales:
- `pedirNumeroFilas()`, `pedirNumeroColumnas()`, `pedirNumeroGeneraciones()`, `pedirDatosIniciales()`: mensajes de solicitud de datos al usuario.
- `mostrarGeneracion(numero, tablero)`: imprime el número y el estado actual del tablero.
- `mostrarOrganismosMuertos()` / `mostrarGeneracionRepetida()`: mensajes de fin de juego según la causa.
- `pedirEnter()`: solicita presionar Enter para continuar.
- `mostrarDatosJuego(...)`: imprime un resumen inicial (dimensiones, generaciones, coordenadas iniciales).
- `mostrarHistorial(...)`: imprime, generación por generación, una tabla con posición, estado, vecinos y resultado de cada celda.
- `mostrarError(mensaje)`: imprime un mensaje de error de validación.
- Métodos privados auxiliares: `formatearCoordenadas(...)` (da formato legible a los datos iniciales) e `imprimirTablaGeneracion(...)` (dibuja la tabla de una generación).

### `Keyboard`
Clase de utilidad (autoría: *Lewis and Loftus*) que **abstrae la lectura de entrada por teclado**, facilitando el parseo de tipos primitivos y cadenas sin manejar excepciones de bajo nivel en el resto del código. Provee métodos como `readInt()`, `readString()`, `readLine()`, `readWord()`, `readBoolean()`, `readChar()`, `readLong()`, `readFloat()`, `readDouble()`, además de utilidades para contar y mostrar errores de lectura (`getErrorCount()`, `setPrintErrors(boolean)`).

En este proyecto se usa principalmente `readInt()` (filas, columnas, generaciones), `readString()` (datos iniciales) y `readLine()` (pausa entre generaciones).

---

## Flujo de ejecución

```
App.main()
  └── ElJuegoDeLaVida.jugar()
        ├── crearTablero()              → pide filas/columnas → new Tablero(...)
        ├── pedirNumeroGeneraciones()   → valida con Reglas
        ├── iniciarTablero(tablero)     → pide datos iniciales → Tablero.iniciar(...)
        ├── vista.mostrarDatosJuego(...)
        ├── simularGeneraciones(...)
        │     ├── vista.mostrarGeneracion(...)
        │     ├── registra HistorialGeneracion
        │     ├── juegoTerminado(tablero)?  (sin vivos / generación repetida)
        │     └── si continúa: espera Enter → tablero.nextGeneration()
        └── vista.mostrarHistorial(historial)
```

---

## Formato de entrada de datos iniciales

```
numeroOrganismos, fila1,columna1, fila2,columna2, ...
```

**Ejemplo:**
```
2, 0,1, 3,4
```
Esto coloca 2 organismos vivos: uno en la celda `(0,1)` y otro en `(3,4)`.

---

## Reglas de Conway aplicadas

| Estado actual | Vecinos vivos | Nuevo estado |
|---|---|---|
| Vivo | < 2 | Muerto (soledad) |
| Vivo | 2 o 3 | Vivo (sobrevive) |
| Vivo | > 3 | Muerto (sobrepoblación) |
| Muerto | = 3 | Vivo (nace) |
| Muerto | ≠ 3 | Muerto (sin cambio) |

---

## Requisitos

- Java 16 o superior (el proyecto usa **records**, por lo que requiere al menos Java 16).
- No tiene dependencias externas.
