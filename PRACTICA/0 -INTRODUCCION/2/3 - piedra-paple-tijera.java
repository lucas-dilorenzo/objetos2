/*
 * DECISIÓN: Enum en lugar de String para representar resultados.
 * Ventaja: el compilador detecta errores de tipeo, no puede existir
 * un resultado inválido como "Gana" vs "gana".
 * El método invertir() permite que juegaContra() devuelva siempre
 * el resultado desde el punto de vista de quien llama (this).
 */
enum Resultado {
    GANA, PIERDE, EMPATE;

    Resultado invertir() {
        if (this == GANA)   return PIERDE;
        if (this == PIERDE) return GANA;
        return EMPATE;
    }
}

/*
 * DECISIÓN: Clase abstracta Jugada con Double Dispatch.
 *
 * El problema central es que juegaContra(Jugada otra) recibe una
 * referencia genérica, y sin saber el tipo concreto de "otra" no
 * se puede determinar el resultado sin usar instanceof (code smell).
 *
 * SOLUCIÓN - Double Dispatch:
 * Cada subclase implementa juegaContra() anunciándose a sí misma
 * mediante el método contraX correspondiente (ej: contraPiedra).
 * Así el polimorfismo resuelve el tipo concreto sin ningún if/switch.
 *
 * Flujo ejemplo: piedra.juegaContra(papel)
 *   → papel.contraPiedra(this)   // papel sabe que juega contra Piedra
 *   → Resultado.PIERDE           // desde el punto de vista de Papel
 *   → .invertir()                // se invierte: Piedra PIERDE
 *
 * TRADEOFF: Agregar una nueva jugada requiere modificar Jugada
 * (nuevo método abstracto) y todas las subclases existentes.
 * Viola Open/Closed Principle, pero elimina completamente
 * los instanceof y los if/switch de comparación.
 */
abstract class Jugada {
    abstract Resultado juegaContra(Jugada otra);
    abstract Resultado contraPiedra(Piedra p);
    abstract Resultado contraPapel(Papel p);
    abstract Resultado contraTijera(Tijera t);
    abstract Resultado contraLagarto(Lagarto l);
    abstract Resultado contraSpock(Spock s);
}

/*
 * Cada subclase implementa:
 * - juegaContra(): se anuncia a sí misma e invierte el resultado
 * - contraX(): devuelve el resultado desde el punto de vista de "otra"
 *
 * Reglas Piedra: aplasta Tijera y Lagarto, pierde contra Papel y Spock
 */
class Piedra extends Jugada {
    Resultado juegaContra(Jugada otra) {
        return otra.contraPiedra(this).invertir();
    }
    Resultado contraPiedra(Piedra p)   { return Resultado.EMPATE; }
    Resultado contraPapel(Papel p)     { return Resultado.PIERDE; } // Papel cubre Piedra
    Resultado contraTijera(Tijera t)   { return Resultado.GANA;   } // Piedra aplasta Tijera
    Resultado contraLagarto(Lagarto l) { return Resultado.GANA;   } // Piedra aplasta Lagarto
    Resultado contraSpock(Spock s)     { return Resultado.PIERDE; } // Spock vaporiza Piedra
}

// Reglas Papel: cubre Piedra y desaprueba Spock, pierde contra Tijera y Lagarto
class Papel extends Jugada {
    Resultado juegaContra(Jugada otra) {
        return otra.contraPapel(this).invertir();
    }
    Resultado contraPiedra(Piedra p)   { return Resultado.GANA;   } // Papel cubre Piedra
    Resultado contraPapel(Papel p)     { return Resultado.EMPATE; }
    Resultado contraTijera(Tijera t)   { return Resultado.PIERDE; } // Tijera corta Papel
    Resultado contraLagarto(Lagarto l) { return Resultado.PIERDE; } // Lagarto come Papel
    Resultado contraSpock(Spock s)     { return Resultado.GANA;   } // Papel desaprueba Spock
}

// Reglas Tijera: corta Papel y decapita Lagarto, pierde contra Piedra y Spock
class Tijera extends Jugada {
    Resultado juegaContra(Jugada otra) {
        return otra.contraTijera(this).invertir();
    }
    Resultado contraPiedra(Piedra p)   { return Resultado.PIERDE; } // Piedra aplasta Tijera
    Resultado contraPapel(Papel p)     { return Resultado.GANA;   } // Tijera corta Papel
    Resultado contraTijera(Tijera t)   { return Resultado.EMPATE; }
    Resultado contraLagarto(Lagarto l) { return Resultado.GANA;   } // Tijera decapita Lagarto
    Resultado contraSpock(Spock s)     { return Resultado.PIERDE; } // Spock rompe Tijera
}

// Reglas Lagarto: come Papel y envenena Spock, pierde contra Piedra y Tijera
class Lagarto extends Jugada {
    Resultado juegaContra(Jugada otra) {
        return otra.contraLagarto(this).invertir();
    }
    Resultado contraPiedra(Piedra p)   { return Resultado.PIERDE; } // Piedra aplasta Lagarto
    Resultado contraPapel(Papel p)     { return Resultado.GANA;   } // Lagarto come Papel
    Resultado contraTijera(Tijera t)   { return Resultado.PIERDE; } // Tijera decapita Lagarto
    Resultado contraLagarto(Lagarto l) { return Resultado.EMPATE; }
    Resultado contraSpock(Spock s)     { return Resultado.GANA;   } // Lagarto envenena Spock
}

// Reglas Spock: vaporiza Piedra y rompe Tijera, pierde contra Papel y Lagarto
class Spock extends Jugada {
    Resultado juegaContra(Jugada otra) {
        return otra.contraSpock(this).invertir();
    }
    Resultado contraPiedra(Piedra p)   { return Resultado.GANA;   } // Spock vaporiza Piedra
    Resultado contraPapel(Papel p)     { return Resultado.PIERDE; } // Papel desaprueba Spock
    Resultado contraTijera(Tijera t)   { return Resultado.GANA;   } // Spock rompe Tijera
    Resultado contraLagarto(Lagarto l) { return Resultado.PIERDE; } // Lagarto envenena Spock
    Resultado contraSpock(Spock s)     { return Resultado.EMPATE; }
}

public class Main {
    public static void main(String[] args) {
        // Casos base - Piedra Papel Tijera
        System.out.println("piedra vs piedra: " + new Piedra().juegaContra(new Piedra())); // EMPATE
        System.out.println("piedra vs papel:  " + new Piedra().juegaContra(new Papel()));  // PIERDE
        System.out.println("piedra vs tijera: " + new Piedra().juegaContra(new Tijera())); // GANA
        System.out.println("papel vs piedra:  " + new Papel().juegaContra(new Piedra()));  // GANA
        System.out.println("papel vs papel:   " + new Papel().juegaContra(new Papel()));   // EMPATE
        System.out.println("papel vs tijera:  " + new Papel().juegaContra(new Tijera()));  // PIERDE
        System.out.println("tijera vs piedra: " + new Tijera().juegaContra(new Piedra())); // PIERDE
        System.out.println("tijera vs papel:  " + new Tijera().juegaContra(new Papel()));  // GANA
        System.out.println("tijera vs tijera: " + new Tijera().juegaContra(new Tijera())); // EMPATE

        // Casos extendidos - Lagarto y Spock
        System.out.println("spock vs piedra:  " + new Spock().juegaContra(new Piedra()));   // GANA
        System.out.println("piedra vs spock:  " + new Piedra().juegaContra(new Spock()));   // PIERDE
        System.out.println("lagarto vs papel: " + new Lagarto().juegaContra(new Papel()));  // GANA
        System.out.println("spock vs lagarto: " + new Spock().juegaContra(new Lagarto()));  // PIERDE
    }
}