# Objetos 2 — UNLP, Facultad de Informática

**Docente:** Dra. Alejandra Garrido | **Lenguaje:** Java | **Evaluaciones:** en papel (sin IDE)

## Temario (3 unidades)
1. **REFACTORING** — teoría y práctica completas
2. **PATRONES DE DISEÑO** — en curso (Adapter visto, Template Method en progreso)
3. **FRAMEWORKS** — pendiente

---

## UNIDAD 1: REFACTORING

### Qué es
- **Sustantivo:** transformación interna que preserva el comportamiento observable
- **Proceso:** mejorar organización, legibilidad, adaptabilidad, mantenibilidad — SIN alterar comportamiento externo
- Referencia: Martin Fowler — "Refactoring. Improving the Design of Existing Code" (1999)

### Por qué importa
- Entender código existente = 50% del tiempo de mantenimiento (Lehman)
- Leyes de Lehman: Continuing Change, Increasing Complexity, Declining Quality
- **The 2 Hats (Kent Beck):** sombrero amarillo = Adding Function; sombrero marrón = Refactoring. Solo 1 por vez, siempre con tests en verde.

### CLEAN Code
**C**ohesive, **L**oosely coupled, **E**ncapsulated, **A**sertive, **N**on-redundant + legible

### Cuándo refactorizar
- Al encontrar código con mal olor
- Cuando no puedo entender el código
- Cuando encuentro una mejor forma — siempre con tests en verde

---

### BAD SMELLS

| Categoría | Smells |
|---|---|
| **Bloaters** | Long Method, Large Class, Data Clumps, Long Parameter List, Primitive Obsession |
| **Tool abusers** | Switch Statements, Refused Bequest, Alternative Classes w/ Different Interfaces, Temporary Field |
| **Change preventers** | Divergent Change, Shotgun Surgery, Parallel Inheritance Hierarchies |
| **Dispensables** | Lazy Class, Speculative Generality, Data Class, Duplicated Code |
| **Couplers** | Feature Envy, Inappropriate Intimacy, Message Chains, Middle Man |

**Principales:**
- **Duplicate Code:** mismo código en muchos lados → difícil mantener
- **Large Class:** hace demasiado, baja cohesión
- **Long Method:** >~20 líneas
- **Feature Envy:** un método usa principalmente datos de OTRA clase → está en la clase incorrecta
- **Data Class:** solo getters/setters → diseño procedural
- **Switch Statements:** condicionales por tipo → usar subclases

---

### CATÁLOGO DE REFACTORINGS

#### Extract Method
- **Smell:** Long Method, código muy comentado
- **Precondiciones:** unidad sintáctica completa; puede modificar como máx. 1 variable temporal usada después (= valor de retorno)
- **Mecánica:** crear método con nombre descriptivo → copiar código → revisar variables temporales (mover declaración / pasar como param / retornar) → compilar → reemplazar → compilar y testear

#### Replace Temp with Query
- **Motivación:** evitar métodos largos, poder reusar desde otros métodos, reducir parámetros antes de Extract Method
- **Mecánica:** extraer expresión en método → reemplazar TODAS las referencias a la temp → eliminar declaración

#### Move Method
- **Smell:** Feature Envy
- **Precondiciones:** (1) clase destino no tiene ese método ni lo hereda; (2) no hay otra def en super/subclases de origen; (3) no modifica v.i. de clase origen; (4) puede acceder desde destino a todo lo que accedía
- **Mecánica:** revisar v.i. → declarar en destino (renombrar si corresponde) → copiar y ajustar → compilar destino → determinar referencia desde origen → reemplazar por delegación o eliminar → compilar y testear

#### Replace Conditional with Polymorphism
- **Smell:** Switch Statements
- **Mecánica:** crear jerarquía → Extract Method si el condicional está en método largo → por cada subclase: crear método override + copiar branch + compilar/testear + borrar branch de superclase → hacer abstracto en superclase

#### Pull Up Field
- **Precondiciones:** mismo nombre, mismo tipo, no existe en superclase, se usa igual
- **Mecánica:** crear v.i. en superclase (protected si era private) → borrar de subclases → compilar y testear

#### Pull Up Method
- **Precondiciones:** cuerpo idéntico, signatura idéntica, superclase común, sin conflictos, elementos accesibles desde superclase
- **Mecánica:** crear en superclase → borrar de subclases de a una compilando y testeando

#### Extract Superclass
- **Cuándo:** dos clases con implementaciones similares → extraer superclase común con lo compartido
- Combina Pull Up Field + Pull Up Method

#### Encapsulate Field
- **Smell:** campo público rompe encapsulamiento
- **Mecánica:** hacer private → crear getter/setter → actualizar todos los accesos

#### Rename Method / Rename Field / Rename Variable
- Cambiar nombre de método/campo/variable a lo largo de todos sus usos (afecta AST completo)

#### Replace Loop with Pipeline
- Reemplazar loops imperativos por stream/filter/map/reduce

### Smells → Refactorings

| Smell | Refactoring |
|---|---|
| Código duplicado | Extract Method, Pull Up Method, Form Template Method |
| Métodos largos | Extract Method, Decompose Conditional, Replace Temp with Query |
| Clases grandes | Extract Class, Extract Subclass |
| Muchos parámetros | Replace Parameter with Method, Introduce Parameter Object |
| Feature Envy | Move Method |
| Data Class | Move Method |
| Switch Statements | Replace Conditional with Polymorphism |
| Cadena de mensajes | Hide Delegate |
| Refused bequest | Push Down Method/Field |

---

### EJEMPLO TRABAJADO: Club de Tenis
`ClubTenis` → muchos `Partido` → 2 `Jugador` (zona A/B/C)

**Smells iniciales:** Long Method, Duplicate Code (j1/j2), Feature Envy, Switch Statements (por zona)

**Secuencia:**
1. Extract Method en ClubTenis → `mostrarPartido(Partido p)`
2. Move Method → `Partido.toString()` (Feature Envy)
3. Extract Method en Partido → `puntosJugadorToString(Jugador)`
4. Move Method → `Jugador.puntosEnPartidoToString(Partido)` (Feature Envy)
5. Replace Conditional with Polymorphism → `JugadorZonaA/B/C` con `puntosGanadosEnPartido(Partido)` abstracto
6. Replace Temp with Query → `totalGamesEnPartido(Partido)` en `Jugador`

**Lección clave:** refactorings en secuencia, uno a la vez, tests en verde siempre. Performance no es prioridad durante refactoring.

---

### Herramientas de Refactoring
- Usan **AST (Abstract Syntax Tree)** para chequear precondiciones y transformar
- Lo que no está en AST no se chequea → unit testing es fundamental
- Eclipse: Refactor > Extract Method, Move, Rename, etc.

---

### PRÁCTICA REFACTORING — Ejercicios (Cuadernillo 2026)

| Ejercicio | Tema | Refactorings involucrados |
|---|---|---|
| 1.1 Cliente | Code smells | Rename Method (lmtCrdt, mtFcE, mtCbE) |
| 1.2 Proyectos | Move Method | Feature Envy: `participaEnProyecto` en `Persona` → mover a `Proyecto` |
| 1.3 Cálculos | Long Method | Extract Method, Replace Temp with Query |
| 2 | Iteradores circulares | Rename Variable (`result` → `currentPosition`) — riesgo: colisión de nombres |
| 3 | Iteradores circulares bis | Extract Superclass (CharRing + IntRing → superclase común) |
| 4 | Alcance Redes Sociales | Rename Method: `procesar`→`impacto`, `calcular`→`alcance`; Rename Parameter |
| 5 | Productos (HotelStay/CarRental) | Encapsulate Field (`cost`), Rename Field, Pull Up Method (`startDate`, `endDate`) |
| 6.1 | Empleados | Duplicate Code (3 clases sin jerarquía) → Extract Superclass + Pull Up |
| 6.2 | Juego | Feature Envy: `Juego` accede a `j.puntuacion` → Move Method a `Jugador` |
| 6.3 | Publicaciones | Long Method → Extract Method, Replace Loop with Pipeline |
| 6.4 | Carrito de compras | Feature Envy en `total()` de Carrito → ya está bien delegado |
| 6.5 | Envío de pedidos | Message Chains (`cliente.direccion.getCalle()`) → Hide Delegate |
| 6.6 | Películas (HBOO) | Switch Statements por tipoSubscripcion → Replace Conditional with Polymorphism |
| 7 | Etiquetas | Duplicate Code en `generar()` → Pull Up Method (requiere pasos previos) |
| 8 | Documentos y estadísticas | Duplicate Code (stream) → Replace Temp with Query; bug en promedio |
| 9 | Pedidos | Replace Loop with Pipeline (líneas 16-19), Replace Conditional with Polymorphism (21-27), Extract+Move Method (línea 28), Extract+Replace Temp with Query (28-33) |

---

## UNIDAD 2: PATRONES DE DISEÑO

### Qué es un patrón
- Origen: Christopher Alexander "A Pattern Language" (1977) → Ward Cunningham & Kent Beck, OOPSLA 87
- **Definición:** "Each pattern describes a problem which occurs over and over again in our environment, and then describes the core of the solution to that problem, in such a way that you can use this solution a million times over, without ever doing it the same way twice"
- Un patrón = par **problema-solución**: recurrente, probado, suficientemente genérico
- Objetivo: abstracciones correctas, asignación correcta de responsabilidades, jerarquías adecuadas, diseños modulares, extensibles, comprensibles, reusables

---

### PATRÓN ADAPTER

**Problema que resuelve:** querer usar una clase existente cuya interfaz es incompatible con lo que el cliente espera (no se puede cambiar esa clase ni su jerarquía).

**Analogía:** adaptador de enchufe de viaje.

**Ejemplo IoT:** `Sensor` notifica a suscriptores `Actuador` via `update(Sensor)`. Se quiere agregar `TelegramNotifier` (librería externa, no modificable, tiene `notify(String)` no `update(Sensor)`).
- **Solución:** crear `TelegramAdapter extends Actuador` que tiene referencia a `TelegramNotifier` y en `actuarAnteCambio(Sensor)` llama a `notifier.notify(...)`

**Intención:** "Convertir" la interfaz de una clase en otra que el cliente espera. Permite que clases con interfaces incompatibles trabajen en conjunto.

**Aplicabilidad:** cuando se quiere usar una clase existente y su interfaz no es compatible con lo que se necesita.

**Participantes:**
- **Target:** define la interfaz que usa el cliente (ej: `Actuador`)
- **Client:** colabora con objetos que satisfacen la interfaz de Target (ej: `Sensor`)
- **Adaptee:** clase con interfaz incompatible que se quiere reutilizar (ej: `TelegramNotifier`)
- **Adapter:** adapta la interfaz del Adaptee a la interfaz de Target (ej: `TelegramAdapter`)

**Estructura:** `Client` → `Target` ← `Adapter` → `Adaptee`

**Colaboraciones:** Client llama a Adapter → Adapter llama a Adaptee (traduciendo la llamada).

**Consecuencias:**
- Una misma clase Adapter puede usarse para muchos Adaptees (y sus subclases)
- El Adapter puede agregar funcionalidad a los adaptados
- Se generan más objetos intermediarios

**Otro ejemplo:** `FlightSimulator` usa `Scene` (abstract, `draw()`). `MountainScene` necesita `Landscape.render()` — `MountainScene` actúa como Adapter.

---

### PATRÓN TEMPLATE METHOD *(en progreso — ver PDF páginas 21-37)*

El ejemplo IoT ya introduce la idea: `Actuador.update(Sensor)` llama `registrarCambio()` y `actuarAnteCambio()`. `update()` es el **template method** — define el esqueleto del algoritmo; las subclases (`Ventilador`, `Luz`) implementan `actuarAnteCambio()`.

---

## EJERCICIOS DE REPASO (pre-materia)

### Piedra Papel Tijera (+ Lagarto y Spock)
- **Patrón:** Double Dispatch
- `enum Resultado { GANA, PIERDE, EMPATA }` con `invertir()`
- Clase abstracta `Jugada` con `juegaContra(Jugada)` y `contraX()` para cada subclase
- `invertir()` necesario porque `contraX()` devuelve resultado desde el punto de vista de `otra`
- Tradeoff: viola OCP al agregar jugadas, elimina `instanceof`

### Red Social (tipo Twitter)
- `RedSocial` usa `Map<String, Usuario>` para screenName único
- `Tweet` valida texto 1..280 chars; `ReTweet extends Tweet`
- Eliminación en cascada coordinada por `RedSocial`

---

## ARCHIVOS DISPONIBLES

### Teoría
- `TEORIA/1 - REFACTORING/Intro a refactoring.pdf` — conceptos base, Lehman
- `TEORIA/1 - REFACTORING/2-Slides-Catalogo.pdf` — catálogo completo de refactorings
- `TEORIA/1 - REFACTORING/3-Refactoring-Tools.pdf` — AST, herramientas, 2 hats
- `TEORIA/1 - REFACTORING/Refactoring-Ejemplo.pdf` — ejemplo Club de Tenis completo
- `TEORIA/2 - PATRONES/1 - IntroPatrones-Adapter-Template.pdf` — intro patrones, Adapter, Template Method (37 pág)

### Práctica
- `PRACTICA/1 - REFACTORING/Cuadernillo Semestral de Actividades - Refactoring 2026 (PUBLICO).pdf`
- `PRACTICA/0 -INTRODUCCION/` — ejercicios Piedra Papel Tijera y Red Social

### Guías
- `GUIAS PARA LA MATERIA/Catálogo de code smells de Objetos 2.pdf`
- `GUIAS PARA LA MATERIA/Diagrama de clases UML - Resumen.docx.pdf`
- `GUIAS PARA LA MATERIA/Trabajando con proyectos Maven v2.docx.pdf`
- `GUIAS PARA LA MATERIA/cheatsheet streams.pdf`

---

## CÓMO ACTUALIZAR ESTE ARCHIVO

Cuando subas material nuevo, decime el nombre del archivo y lo leo. Actualizo **solo la sección correspondiente** — no hay que re-procesar nada anterior.

_Última actualización: 2026-04-05 — Agregado Patrón Adapter completo + listado de ejercicios del cuadernillo de práctica_
