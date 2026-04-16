/*
Ejercicio 6.1 - Empleados

-------------------------------------------------------------------------
Iteración 1
-------------------------------------------------------------------------

Code smell identificado: Duplicate Code
- Los campos nombre, apellido y sueldoBasico se repiten en las tres clases
- Los campos tienen visibilidad public, lo que rompe el encapsulamiento

Refactorings aplicados: 
- Extract Superclass: se crea la clase Empleado
- Pull Up Field: se suben los campos comunes a la superclase
- Encapsulate Field: se cambia la visibilidad a protected
*/

public abstract class Empleado {
    protected String nombre;
    protected String apellido;
    protected double sueldoBasico = 0;
}

public class EmpleadoTemporario extends Empleado {
    public double horasTrabajadas = 0;
    public int cantidadHijos = 0;

    public double sueldo() {
        return this.sueldoBasico
            + (this.horasTrabajadas * 500)
            + (this.cantidadHijos * 1000)
            - (this.sueldoBasico * 0.13);
    }
}

public class EmpleadoPlanta extends Empleado {
    public int cantidadHijos = 0;

    public double sueldo() {
        return this.sueldoBasico
            + (this.cantidadHijos * 2000)
            - (this.sueldoBasico * 0.13);
    }
}

public class EmpleadoPasante extends Empleado {
    public double sueldo() {
        return this.sueldoBasico - (this.sueldoBasico * 0.13);
    }
}

/*
-------------------------------------------------------------------------
Iteración 2
-------------------------------------------------------------------------

Code smell identificado: Duplicate Code
La expresión (this.sueldoBasico * 0.13) representa el descuento de 
jubilación y se repite en los tres métodos sueldo().

Refactorings aplicados:
- Extract Method: se extrae la expresión en un método descuento()
- Pull Up Method: se sube a Empleado ya que es idéntico en las tres clases
*/

public abstract class Empleado {
    protected String nombre;
    protected String apellido;
    protected double sueldoBasico = 0;

    protected double descuento() {
        return this.sueldoBasico * 0.13;
    }
}

public class EmpleadoTemporario extends Empleado {
    public double horasTrabajadas = 0;
    public int cantidadHijos = 0;

    public double sueldo() {
        return this.sueldoBasico
            + (this.horasTrabajadas * 500)
            + (this.cantidadHijos * 1000)
            - descuento();
    }
}

public class EmpleadoPlanta extends Empleado {
    public int cantidadHijos = 0;

    public double sueldo() {
        return this.sueldoBasico
            + (this.cantidadHijos * 2000)
            - descuento();
    }
}

public class EmpleadoPasante extends Empleado {
    public double sueldo() {
        return this.sueldoBasico - descuento();
    }
}

/*
-------------------------------------------------------------------------
Iteración 3
-------------------------------------------------------------------------

Code smell identificado: Encapsulamiento roto
Los campos horasTrabajadas y cantidadHijos en EmpleadoTemporario 
y EmpleadoPlanta siguen siendo públicos.

Refactoring aplicado: Encapsulate Field
Se cambia la visibilidad a protected en cada subclase.
*/

public class EmpleadoTemporario extends Empleado {
    protected double horasTrabajadas = 0;
    protected int cantidadHijos = 0;

    public double sueldo() {
        return this.sueldoBasico
            + (this.horasTrabajadas * 500)
            + (this.cantidadHijos * 1000)
            - descuento();
    }
}

public class EmpleadoPlanta extends Empleado {
    protected int cantidadHijos = 0;

    public double sueldo() {
        return this.sueldoBasico
            + (this.cantidadHijos * 2000)
            - descuento();
    }
}

/*
-------------------------------------------------------------------------
Iteración 4
-------------------------------------------------------------------------

Code smell identificado: Duplicate Code
Los métodos sueldo() en las tres subclases tienen la misma estructura:
sueldoBasico + extras - descuento()
Solo varía el cálculo de extras en cada subclase.

Refactoring aplicado: Form Template Method
Se sube la estructura general de sueldo() a Empleado y se declara
extras() como abstracto para que cada subclase lo implemente.

Esto elimina el Duplicate Code estructural y centraliza la lógica
de sueldo() en un único lugar.
*/

public abstract class Empleado {
    protected String nombre;
    protected String apellido;
    protected double sueldoBasico = 0;

    public double sueldo() {
        return this.sueldoBasico + extras() - descuento();
    }

    protected abstract double extras();

    protected double descuento() {
        return this.sueldoBasico * 0.13;
    }
}

public class EmpleadoTemporario extends Empleado {
    protected double horasTrabajadas = 0;
    protected int cantidadHijos = 0;

    protected double extras() {
        return (this.horasTrabajadas * 500) + (this.cantidadHijos * 1000);
    }
}

public class EmpleadoPlanta extends Empleado {
    protected int cantidadHijos = 0;

    protected double extras() {
        return this.cantidadHijos * 2000;
    }
}

public class EmpleadoPasante extends Empleado {
    protected double extras() {
        return 0;
    }
}

/*
Resultado final: sueldo() vive en un único lugar (Empleado).
Cada subclase solo define sus propios extras.
Este refactoring es el que lleva al patrón Template Method (Unidad 2):
la superclase define el algoritmo general y las subclases 
completan los pasos variables.
*/