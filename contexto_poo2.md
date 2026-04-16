# Contexto POO2 - Objetos 2 UNLP

> Pegá este archivo al inicio de cada sesión para darle contexto a Claude.
> Actualizar cada vez que agregues material nuevo.

---

## Información general

- **Materia:** Programación Orientada a Objetos 2 (Objetos 2)
- **Facultad:** Facultad de Informática - UNLP
- **Docente:** Dra. Alejandra Garrido
- **Lenguaje:** Java
- **Evaluaciones:** En papel (sin IDE)

---

## Temario (3 unidades)

1. **REFACTORING** ← tema actual
2. **PATRONES DE DISEÑO** (State, Strategy, Template Method, etc.)
3. **FRAMEWORKS** (caja negra y blanca)

---

## Ejercicios ya resueltos (repaso previo)

### Ejercicio: Piedra Papel Tijera (+ Lagarto y Spock)

- **Patrón:** Double Dispatch
- **Decisiones clave:**
  - `enum Resultado { GANA, PIERDE, EMPATA }` con método `invertir()`
  - Clase abstracta `Jugada` con métodos `juegaContra(Jugada)` y `contraX()` para cada subclase
  - `invertir()` necesario porque `contraX()` devuelve resultado desde el punto de vista de `otra`, no de `this`
  - Tradeoff: viola OCP al agregar jugadas nuevas, pero elimina `instanceof`
- **Clases:** `Jugada` (abstract), `Piedra`, `Papel`, `Tijera`, `Lagarto`, `Spock`

### Ejercicio: Red Social (tipo Twitter)

- **Decisiones clave:**
  - `RedSocial` usa `Map<String, Usuario>` para garantizar screenName único
  - `Tweet` valida texto 1..280 chars en constructor con `IllegalArgumentException`
  - `ReTweet extends Tweet` — es un Tweet, sin texto adicional propio
  - Eliminación en cascada coordinada por `RedSocial` (conoce a todos los usuarios)
  - Orden de borrado: 1) limpiar retweets huérfanos en otros usuarios, 2) limpiar tweets del usuario, 3) eliminar del mapa
  - `removeIf` + `instanceof` + cast para eliminar retweets huérfanos
- **Clases:** `RedSocial`, `Usuario`, `Tweet`, `ReTweet`

---

## UNIDAD 1: REFACTORING

### ¿Qué es?

- **Sustantivo:** transformación en la estructura interna del software que preserva el comportamiento observable
- **Proceso:** cambiar el sistema para mejorar organización, legibilidad, adaptabilidad y mantenibilidad — SIN alterar comportamiento externo
- Referencia principal: Martin Fowler — "Refactoring. Improving the Design of Existing Code" (1999)

### ¿Por qué es importante?

- Ganar comprensión del código
- Reducir costo de mantenimiento
- Facilitar detección de bugs
- Poder agregar funcionalidad más rápido después de refactorizar
- Entender código existente = 50% del tiempo de mantenimiento (Lehman)

### Leyes de Lehman (contexto)

- Continuing Change: los sistemas deben adaptarse continuamente
- Increasing Complexity: la complejidad crece a menos que se trabaje para evitarlo
- Declining Quality: la calidad declina sin mantenimiento riguroso

### Cuándo aplicar refactoring

- Cuando se descubre código con mal olor
- Cuando no puedo entender el código
- Cuando encuentro una mejor forma de codificar algo
- **Siempre con tests en verde** (metáfora de los 2 sombreros de Kent Beck)

### The 2 Hats (Kent Beck)

- Sombrero amarillo = Adding Function (explorar ideas, corregir bugs)
- Sombrero marrón = Refactoring (solo con tests en verde)
- Solo 1 sombrero por vez, podés cambiar frecuentemente

### CLEAN Code

- **C**ohesive, **L**oosely coupled, **E**ncapsulated, **A**sertive, **N**on-redundant
- Y además: legible

---

## BAD SMELLS / CODE SMELLS

### Categorías

| Categoría             | Smells                                                                                           |
| --------------------- | ------------------------------------------------------------------------------------------------ |
| **Bloaters**          | Long Method, Large Class, Data Clumps, Long Parameter List, Primitive Obsession                  |
| **Tool abusers**      | Switch Statements, Refused Bequest, Alternative Classes w/ Different Interfaces, Temporary Field |
| **Change preventers** | Divergent Change, Shotgun Surgery, Parallel Inheritance Hierarchies                              |
| **Dispensables**      | Lazy Class, Speculative Generality, Data Class, Duplicated Code                                  |
| **Couplers**          | Feature Envy, Inappropriate Intimacy, Message Chains, Middle Man                                 |

### Smells principales

- **Duplicate Code:** mismo código en muchos lugares → difícil de mantener y bugfixear
- **Large Class:** hace demasiado, baja cohesión, muchas variables de instancia
- **Long Method:** más de ~20 líneas, difícil de entender/cambiar/reusar
- **Feature Envy:** un método usa principalmente datos/métodos de OTRA clase → está en la clase incorrecta
- **Data Class:** solo tiene variables y getters/setters → diseño procedural
- **Switch Statements:** condicionales para diferentes tipos → usar subclases
- **Long Parameter List:** difícil de entender y reusar

---

## CATÁLOGO DE REFACTORINGS

### Organización Fowler

1. Composición de métodos
2. Mover aspectos entre objetos
3. Organización de datos
4. Simplificación de expresiones condicionales
5. Simplificación en la invocación de métodos
6. Manipulación de la generalización
7. Big refactorings

### Refactorings vistos en clase

#### Extract Method

- **Smell que resuelve:** Long Method, código muy comentado
- **Motivación:** aumentar reuso y legibilidad
- **Precondiciones:**
  1. El código a extraer debe ser una unidad sintáctica completa
  2. El código a extraer puede modificar como máximo 1 variable temporal usada después (ese será el valor de retorno). Si hay más de una, no se puede extraer directamente.
- **Mecánica:**
  1. Crear nuevo método con nombre que explique su propósito
  2. Copiar el código al nuevo método
  3. Revisar variables temporales: mover declaración si solo se usa en el extracto; pasar como parámetro si se lee; retornar si se modifica y se usa después
  4. Compilar
  5. Reemplazar código original por llamada al nuevo método
  6. Compilar y testear

#### Replace Temp with Query

- **Motivación:** evitar métodos largos, poder usar la expresión desde otros métodos, reducir parámetros antes de un Extract Method
- **Mecánica:** extraer expresión en método, reemplazar TODAS las referencias a la temp, eliminar declaración de la temp

#### Move Method

- **Smell que resuelve:** Feature Envy
- **Motivación:** un método usa principalmente servicios de otra clase
- **Precondiciones:**
  1. La clase destino (B) no debe tener ya definido ese método (ni heredarlo)
  2. No debe haber otra definición en superclase ni subclases de A
  3. El método no debe modificar variables de instancia de A
  4. El método debe poder acceder desde B a todo lo que accedía desde A
- **Mecánica:**
  1. Revisar si las v.i. que usa tiene sentido moverlas también
  2. Declarar el método en la clase destino (renombrar si corresponde)
  3. Copiar el código y ajustar (parámetros, excepciones)
  4. Compilar la clase destino
  5. Determinar cómo referenciar B desde A
  6. Reemplazar el método original por delegación o eliminarlo
  7. Compilar y testear

#### Replace Conditional with Polymorphism

- **Smell que resuelve:** Switch Statements
- **Motivación:** eliminar condicionales que discriminan por tipo
- **Mecánica:**
  1. Crear la jerarquía de clases necesaria
  2. Si el condicional está en un método largo, primero Extract Method
  3. Por cada subclase: crear método que sobreescribe, copiar el branch correspondiente, compilar y testear, borrar ese branch de la superclase
  4. Hacer abstracto el método en la superclase

#### Pull Up Field

- **Motivación:** atributo repetido en subclases con misma semántica
- **Precondiciones:** mismo nombre, mismo tipo, no existe en superclase, se usa de la misma manera
- **Mecánica:** crear v.i. en superclase (protected si era private), borrar de subclases, compilar y testear

#### Pull Up Method

- **Motivación:** código duplicado en subclases
- **Precondiciones:** cuerpo idéntico, signatura idéntica, superclase común, sin conflictos, elementos accesibles desde superclase
- **Mecánica:** crear en superclase, borrar de subclases de a una compilando y testeando

### Smells → Refactorings

| Smell              | Refactoring                                                    |
| ------------------ | -------------------------------------------------------------- |
| Código duplicado   | Extract Method, Pull Up Method, Form Template Method           |
| Métodos largos     | Extract Method, Decompose Conditional, Replace Temp with Query |
| Clases grandes     | Extract Class, Extract Subclass                                |
| Muchos parámetros  | Replace Parameter with Method, Introduce Parameter Object      |
| Feature Envy       | Move Method                                                    |
| Data Class         | Move Method                                                    |
| Switch Statements  | Replace Conditional with Polymorphism                          |
| Cadena de mensajes | Hide Delegate                                                  |
| Refused bequest    | Push Down Method/Field                                         |

---

## EJEMPLO TRABAJADO: Club de Tenis

### Enunciado

Retornar un string formateado con los puntajes de los partidos de tenis de una fecha específica. Para cada jugador se muestra nombre, games ganados por set, y puntos que el partido le permitió ganar. Los puntos dependen de la **zona** del jugador: A, B o C.

### Diagrama inicial

- `ClubTenis` → tiene muchos `Partido`
- `Partido` → tiene 2 `Jugador`, fecha, puntosPorJugador, ganador
- `Jugador` → nombre, zona (A/B/C), setters y getters

### Código inicial (con smells)

```java
public class ClubTenis {
    public String mostrarPuntajesJugadoresEnFecha(LocalDate fecha) {
        int totalGames = 0;
        String result = "Puntajes para los partidos de la fecha " + fecha.toString() + "\n";
        List<Partido> partidosFecha = coleccionPartidos.stream()
            .filter(p -> p.fecha().equals(fecha)).collect(Collectors.toList());
        for (Partido p : partidosFecha) {
            totalGames = 0;
            Jugador j1 = p.jugador1();
            result += "Partido: \n";
            result += "Puntaje del jugador: " + j1.nombre() + ": ";
            for (int gamesGanados : p.puntosPorSetDe(j1)) {
                result += Integer.toString(gamesGanados) + ";";
                totalGames += gamesGanados;
            }
            result += "Puntos del partido: ";
            if (j1.zona() == "A") result += Integer.toString(totalGames * 2);
            if (j1.zona() == "B") result += Integer.toString(totalGames);
            if (j1.zona() == "C")
                if (p.ganador() == j1) result += Integer.toString(totalGames);
                else result += Integer.toString(0);
            // ...idem para j2...
        }
        return result;
    }
}
```

### Smells detectados

- **Long Method** en `mostrarPuntajesJugadoresEnFecha`
- **Duplicate Code** (bloque de j1 repetido para j2)
- **Feature Envy** (el método accede constantemente a datos de `Partido` y `Jugador`)
- **Switch Statements** (condicionales por zona A/B/C)

### Secuencia de refactorings aplicados

#### Paso 1: Extract Method (en ClubTenis)

- Se extrae el bloque del for en un método `mostrarPartido(Partido p)`
- Ajuste previo: mover declaración de `totalGames` dentro del código a extraer (así solo modifica 1 variable temporal: `result`)
- Resultado:

```java
// ClubTenis
public String mostrarPuntajesJugadoresEnFecha(LocalDate fecha) {
    String result = "Puntajes para los partidos de la fecha " + fecha.toString() + "\n";
    List<Partido> partidosFecha = coleccionPartidos.stream()
        .filter(p -> fecha.equals(p.fecha())).collect(Collectors.toList());
    for (Partido p : partidosFecha)
        result += this.mostrarPartido(p);
    return result;
}
private String mostrarPartido(Partido p) { ... }
```

#### Paso 2: Move Method (ClubTenis → Partido)

- `mostrarPartido(Partido p)` tiene Feature Envy: todo lo que hace es con datos de `Partido`
- Se mueve a `Partido` y se renombra a `toString()` (Rename Method)
- En `ClubTenis` se reemplaza por `p.toString()` o simplemente `p.mostrar()`

#### Paso 3: Extract Method (en Partido)

- `toString()` sigue siendo largo y tiene código duplicado para j1 y j2
- Se extrae `puntosJugadorToString(Jugador unJugador)` que maneja un jugador
- Resultado:

```java
// Partido
public String toString() {
    String result = "Partido: \n";
    result += puntosJugadorToString(jugador1());
    result += puntosJugadorToString(jugador2());
    return result;
}
private String puntosJugadorToString(Jugador unJugador) { ... }
```

#### Paso 4: Move Method (Partido → Jugador)

- `puntosJugadorToString` tiene Feature Envy con `Jugador`
- Se mueve a `Jugador` como `puntosEnPartidoToString(Partido partido)`
- `Partido.toString()` queda:

```java
public String toString() {
    String result = "Partido: \n";
    result += jugador1().puntosEnPartidoToString(this);
    result += jugador2().puntosEnPartidoToString(this);
    return result;
}
```

#### Paso 5: Replace Conditional with Polymorphism (en Jugador)

- El if/else por zona A/B/C es un Switch Statement
- Se crea jerarquía: `JugadorZonaA`, `JugadorZonaB`, `JugadorZonaC` extends `Jugador`
- Se extrae el condicional en método `puntosGanadosEnPartido(Partido, int totalGames)` y cada subclase lo sobreescribe
- `zona` deja de ser necesaria como campo
- Resultado:

```java
// Jugador (abstracto)
public String puntosEnPartidoToString(Partido partido) {
    int totalGames = 0;
    String result = "Puntaje del jugador: " + nombre() + ": ";
    for (int gamesGanados : partido.puntosPorSetDe(this)) {
        result += Integer.toString(gamesGanados) + ";";
        totalGames += gamesGanados;
    }
    result += "Puntos del partido: ";
    result += Integer.toString(this.puntosGanadosEnPartido(partido, totalGames));
    return result;
}
public abstract int puntosGanadosEnPartido(Partido partido, int totalGames);

// JugadorZonaA
public int puntosGanadosEnPartido(Partido partido, int totalGames) {
    return totalGames * 2;
}
// JugadorZonaB
public int puntosGanadosEnPartido(Partido partido, int totalGames) {
    return totalGames;
}
// JugadorZonaC
public int puntosGanadosEnPartido(Partido partido, int totalGames) {
    return partido.ganador() == this ? totalGames : 0;
}
```

#### Paso 6: Replace Temp with Query (en Jugador)

- `totalGames` es una variable temporal que se puede extraer en método
- Se crea `totalGamesEnPartido(Partido partido)` en `Jugador`
- `puntosGanadosEnPartido` ya no necesita recibir `totalGames` como parámetro

```java
// Jugador
public int totalGamesEnPartido(Partido partido) {
    int total = 0;
    for (int g : partido.puntosPorSetDe(this)) total += g;
    return total;
}
// JugadorZonaA
public int puntosGanadosEnPartido(Partido partido) {
    return totalGamesEnPartido(partido) * 2;
}
```

### Diagrama final

- `ClubTenis` → tiene muchos `Partido`
- `Partido` → tiene 2 `Jugador`; método `toString()`
- `Jugador` (abstracto) → nombre; `puntosEnPartidoToString(Partido)`, `totalGamesEnPartido(Partido)`, `puntosGanadosEnPartido(Partido)` (abstracto)
  - `JugadorZonaA` → sobreescribe `puntosGanadosEnPartido`
  - `JugadorZonaB` → sobreescribe `puntosGanadosEnPartido`
  - `JugadorZonaC` → sobreescribe `puntosGanadosEnPartido`

### Lecciones clave del ejemplo

- Los refactorings se aplican en secuencia, uno a la vez, testeando siempre entre pasos
- Extract Method puede requerir ajustes manuales previos (mover declaraciones de temporales)
- Move Method resuelve Feature Envy redistribuyendo responsabilidades
- Replace Conditional with Polymorphism elimina el switch de zonas y hace el sistema extensible (agregar ZonaD = nueva subclase, sin tocar existentes)
- Replace Temp with Query reduce parámetros y hace el código más reutilizable
- La performance no es prioridad durante el refactoring; primero flexibilidad, luego optimizar si hace falta

---

## Herramientas de Refactoring

- Refactoring automático usa el **AST (Abstract Syntax Tree)**
- Precondiciones se chequean sobre AST + symbol table
- Transformaciones se realizan mediante AST rewriting
- Lo que no se puede analizar en AST **no se chequea** → unit testing es fundamental
- IDEs modernas tienen refactoring automático integrado (Eclipse: Refactor > Extract Method, Move, etc.)

---

## EJERCICIOS DEL CUADERNILLO RESUELTOS

### Ejercicio 1 — Algo huele mal

#### 1.1 Protocolo de Cliente

- **Smell:** nombres abreviados e ilegibles (`lmtCrdt`, `mtFcE`, `mtCbE`) → el código obliga a leer el comentario para entender el propósito
- **Refactoring:** Rename Method + Rename Parameter
- **Resultado:** `limiteDeCredito()`, `montoFacturadoEntre(fechaInicio, fechaFin)`, `montoCobradoEntre(fechaInicio, fechaFin)`

#### 1.2 Participación en proyectos

- **Smell:** Feature Envy — `Persona.participaEnProyecto()` accede a datos internos de `Proyecto`
- **Refactoring:** Move Method → se mueve a `Proyecto` como `participa(Persona p)`
- **El cambio es apropiado** porque `Proyecto` es quien conoce sus participantes

#### 1.3 Cálculos

- **Smells:** Long Method + Duplicate Code (el loop se repite para cada cálculo)
- **Refactoring:** Extract Method → `obtenerPromedioEdades()` y `obtenerTotalSalarios()`
- **Tradeoff:** separar en dos métodos introduce un segundo recorrido, pero se gana claridad. Fowler: primero claridad, luego optimizar si hace falta
- **Nota:** la división por cero si `personal` está vacío es un bug, corregirlo NO es refactoring (cambia el comportamiento)

---

### Ejercicio 2 — Iteradores circulares

- **Tarea:** Rename Variable `result` → `currentPosition` en `next()` (líneas 17 y 18)
- **Inconveniente:** existe otra variable `result` en el constructor, pero son locales a distintos métodos → no hay conflicto real. Un IDE mal configurado podría renombrar ambas si no distingue el scope correctamente

---

### Ejercicio 3 — Iteradores circulares bis

- **Smell:** Duplicate Code — `CharRing` e `IntRing` tienen estructura casi idéntica: campo `idx`, lógica de reset en `next()`
- **Refactoring:** Extract Superclass
- **Secuencia:**
  1. Crear superclase `Ring` con Pull Up Field de `idx` (protected)
  2. Extract Method de la lógica del reset → `resetIfNeeded(int length)`
  3. Pull Up Method de `resetIfNeeded()` a `Ring`
  4. Declarar `next()` abstracto en `Ring` (tipos de retorno distintos, no se puede subir completo)
  5. `CharRing` e `IntRing` extienden `Ring`
- **Nota:** `source` NO se sube porque tiene distinto tipo en cada subclase (`char[]` vs `int[]`)
- **Tests:** deben seguir pasando ya que el comportamiento observable no cambió

---

### Ejercicio 4 — Alcance en Redes Sociales

Rename Method y Rename Parameter — cambios archivo por archivo:

1. `procesar` → `impacto`: afecta línea 11 (declaración) y línea 15 (llamada) en `Publicacion.java`
2. `calcular` → `alcance` (en `Publicacion`): afecta línea 14 en `Publicacion.java` y línea 13 en `Perfil.java` (impacto cruzado entre archivos)
3. `calcular` → `alcance` (en `Perfil`): afecta línea 15 en `Perfil.java`. En el código mostrado no hay otras llamadas, pero en un sistema real habría que rastrear todas las referencias
4. Rename Parameter `p` → `publicacion` en `agregarPublicacion`: afecta la firma y el cuerpo del método en línea 10 de `Perfil.java`

**Regla general:** Rename Method debe rastrear TODOS los lugares donde se invoca, no solo la declaración.

---

### Ejercicio 5 — Productos (HotelStay / CarRental)

#### Tarea 1: Encapsulate Field (`cost` público → privado)

- Cambiar `public double cost` a `private double cost` en ambas clases
- Generar getter `getCost()` y setter `setCost()` públicos
- **¿Modificar tests?** Solo si el test accedía directamente al campo público (mala práctica). Un test bien escrito es agnóstico de la implementación

#### Tarea 2: Rename Field (`cost` → `quote` en `priceFactor()`)

- Afecta en `HotelStay`: línea de declaración, parámetro del constructor, asignación en constructor, y referencia en `priceFactor()`
- **¿Modificar tests?** Si el campo era público y los tests lo usaban directamente, sí hay que actualizarlos

#### Tareas 3, 4 y 5: Pull Up Method (`startDate` y `endDate`)

- **No es posible directamente** porque ambos métodos usan `timePeriod` que vive en las subclases
- **Precondición violada:** los elementos que usa el método deben ser accesibles desde la superclase
- **Refactorings previos necesarios:**
  1. Pull Up Field de `timePeriod` a `Product`
  2. Change Access Modifier: `private` → `protected`
- **Luego sí:** Pull Up Method de `startDate()` y `endDate()` a `Product`

#### Tarea 6: Feature Envy en `price()`

- `HotelStay.price()` envidia datos de `Hotel`; `CarRental.price()` envidia datos de `Company`
- **Refactoring:** Move Method → crear `precioConDescuento()` en `Hotel` y `precioConPromocion()` en `Company`
- `price()` en cada subclase delega a esos métodos

---

### Ejercicio 6 — Iteración sobre smells (parcialmente resuelto)

#### 6.1 Empleados

- **Iteración 1:** Duplicate Code en campos → Extract Superclass + Pull Up Field de `nombre`, `apellido`, `sueldoBasico` a clase abstracta `Empleado` + Encapsulate Field (protected)
- **Iteración 2:** Duplicate Code en `(sueldoBasico * 0.13)` → Extract Method `descuento()` + Pull Up Method a `Empleado`
- **Iteración 3:** campos `horasTrabajadas` y `cantidadHijos` siguen siendo públicos → Encapsulate Field (protected)
- **Iteración 4 (Form Template Method):** `sueldo()` tiene estructura común en todas las subclases (`sueldoBasico + extras - descuento()`). Se sube la estructura a `Empleado` y se declara `extras()` abstracto:

```java
// Empleado
public double sueldo() {
    return this.sueldoBasico + extras() - descuento();
}
protected abstract double extras();
protected double descuento() { return sueldoBasico * 0.13; }

// EmpleadoTemporario
protected double extras() {
    return (horasTrabajadas * 500) + (cantidadHijos * 1000);
}

// EmpleadoPlanta
protected double extras() {
    return cantidadHijos * 2000;
}

// EmpleadoPasante
protected double extras() {
    return 0;
}
```

- **Relación con patrones:** Form Template Method es el refactoring que lleva al patrón Template Method (Unidad 2)

#### 6.2 Juego

- **Smells:** Encapsulamiento roto (campos públicos) + Feature Envy (`Juego` manipula directamente `puntuacion` de `Jugador`)
- **Refactoring:** Encapsulate Field + Move Method
- **Principio clave:** los objetos deben ser responsables de su propio estado. `Jugador` decide cómo modificar su puntuación; `Juego` solo decide cuándo

#### 6.3 a 6.6 — Pendientes

---

### Ejercicios 7, 8 y 9 — Pendientes

---

## Refactorings adicionales vistos en ejercicios

| Refactoring                    | Smell que resuelve   | Descripción                                                                                                                               |
| ------------------------------ | -------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **Rename Method**              | Nombres ilegibles    | Renombrar un método para que exprese mejor su propósito                                                                                   |
| **Rename Parameter**           | Nombres ilegibles    | Renombrar un parámetro para mayor claridad                                                                                                |
| **Rename Variable**            | Nombres ilegibles    | Renombrar una variable local. Afecta solo su scope                                                                                        |
| **Encapsulate Field**          | Encapsulamiento roto | Campo público → privado + getter/setter                                                                                                   |
| **Extract Superclass**         | Duplicate Code       | Extraer comportamiento/estado común en una superclase                                                                                     |
| **Change Access Modifier**     | —                    | Cambiar visibilidad (ej: private → protected)                                                                                             |
| **Replace Loop with Pipeline** | Long Method          | Reemplazar un for con streams de Java                                                                                                     |
| **Form Template Method**       | Duplicate Code       | Subir estructura común de un método a la superclase, dejando los pasos variables como métodos abstractos. Lleva al patrón Template Method |
| **Extract Class**              | Large Class          | Extraer responsabilidades en una nueva clase                                                                                              |
| **Introduce Parameter Object** | Long Parameter List  | Reemplazar grupo de parámetros relacionados con un objeto                                                                                 |
| **Hide Delegate**              | Message Chains       | Ocultar la cadena de delegaciones detrás de un método                                                                                     |
| **Push Down Method/Field**     | Refused Bequest      | Mover método/campo de superclase a las subclases que lo usan                                                                              |

---

## Visibilidad en Java

| Modificador       | Accesible desde                         |
| ----------------- | --------------------------------------- |
| `public`          | Cualquier clase                         |
| `protected`       | La propia clase y sus subclases         |
| (sin modificador) | Solo el mismo paquete (package-private) |
| `private`         | Solo la propia clase                    |

---

## Streams en Java (resumen)

```java
// Estructura básica
coleccion.stream()
    .operacionIntermedia()  // filter, map, sorted...
    .operacionFinal();      // collect, sum, count, average...

// Ejemplos comunes
personal.stream()
    .filter(e -> e.getEdad() > 30)
    .collect(Collectors.toList());           // List filtrada

personal.stream()
    .mapToDouble(e -> e.getSalario())
    .sum();                                  // suma

personal.stream()
    .mapToInt(e -> e.getEdad())
    .average()
    .orElse(0.0);                           // promedio (orElse es más seguro que getAsDouble)

personal.stream()
    .filter(e -> e.getSalario() > 50000)
    .map(e -> e.getNombre())
    .collect(Collectors.toList());           // filter + map combinados
```

- `.collect(Collectors.toList())` solo va cuando querés una `List` como resultado
- `mapToDouble` / `mapToInt` convierten a stream de primitivos habilitando `.sum()`, `.average()`, etc.
- Los streams son **lazy**: no se ejecutan hasta la operación final

---

## Material disponible

- `Intro_a_refactoring.pdf` — conceptos base, leyes de Lehman, ejemplos con Product/HotelStay/CarRental
- `2-Slides-Catalogo.pdf` — catálogo completo de refactorings con mecánica
- `3-Refactoring-Tools.pdf` — herramientas, AST, the 2 hats
- `Refactoring-Ejemplo.pdf` — ejemplo completo Club de Tenis (Extract Method, Move Method, Replace Conditional with Polymorphism, Replace Temp with Query)
- `Cuadernillo_Semestral_de_Actividades_Refactoring_2026.pdf` — ejercicios de práctica

## Pendiente / por agregar

- Ejercicios 6.3 a 6.6, 7, 8 y 9 del cuadernillo
- PDFs de Patrones de diseño
- PDFs de Frameworks

---

_Última actualización: agregado Form Template Method en 6.1 + catálogo de refactorings completo_
