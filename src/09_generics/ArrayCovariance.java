/*
 * Arrays are covariant: Dog[] is usable as Animal[]. The compiler allows it,
 * then the JVM has to check every store at runtime and throws
 * ArrayStoreException when the element does not fit the real array type.
 *
 * Generics chose the safer rule instead - they are invariant, so
 * List<Dog> is *not* a List<Animal>. Wildcards fill that gap; see Wildcards.java.
 */
public class ArrayCovariance {
    public static void main(String[] args) {
        Dog[] dogs = new Dog[3];
        Animal[] animals = dogs;   // allowed, but the array is still a Dog[]

        animals[0] = new Dog();

        try {
            animals[1] = new Animal();   // compiles, fails at runtime
        } catch (ArrayStoreException e) {
            System.out.println("ArrayStoreException: cannot store an Animal in a Dog[]");
        }

        for (Animal animal : animals) {
            if (animal != null) {
                animal.eat();
            }
        }
    }

    static class Animal {
        void eat() {
            System.out.println("Eating");
        }
    }

    static class Dog extends Animal {
        void bark() {
            System.out.println("Barking");
        }
    }
}
