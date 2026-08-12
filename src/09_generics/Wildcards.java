import java.util.ArrayList;
import java.util.List;

/*
 * Generics are invariant, so List<Dog> is not a List<Animal>. Wildcards let a
 * method accept a family of types:
 *
 *   List<?>                unknown type - read as Object, write nothing
 *   List<? extends Animal> some subtype of Animal - safe to read, not to write
 *   List<? super Animal>   some supertype of Animal - safe to write, read as Object
 *
 * The mnemonic is PECS: Producer Extends, Consumer Super.
 */
public class Wildcards {
    public static void main(String[] args) {
        List<Dog> dogs = new ArrayList<>();
        dogs.add(new Dog());
        dogs.add(new Dog());

        printSize(dogs);
        readOnly(dogs);

        List<Animal> animals = new ArrayList<>();
        writeOnly(animals);
    }

    // Accepts any list at all, but nothing can be added to it
    static void printSize(List<?> values) {
        System.out.println("size = " + values.size());
        Object first = values.get(0);
        System.out.println(first.getClass().getSimpleName());
    }

    // Producer: every element is at least an Animal, so reading is typed.
    // Adding is rejected - the real list could be a List<Cat>.
    static void readOnly(List<? extends Animal> values) {
        for (Animal animal : values) {
            animal.eat();
        }
    }

    // Consumer: the list holds Animal or something above it,
    // so any Animal subtype can be added. Reads only give back Object.
    static void writeOnly(List<? super Animal> values) {
        values.add(new Animal());
        values.add(new Dog());
        values.add(new Labrador());

        for (Object obj : values) {
            ((Animal) obj).eat();
        }
    }

    static class Animal {
        void eat() {
            System.out.println("Animal eating");
        }
    }

    static class Dog extends Animal {
        @Override
        void eat() {
            System.out.println("Dog eating");
        }
    }

    static class Labrador extends Dog {
    }

    static class Cat extends Animal {
    }
}
