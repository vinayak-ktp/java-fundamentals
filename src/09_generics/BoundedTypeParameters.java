/*
 * An unbounded T is treated as Object, so no useful method is available.
 * A bound narrows what may be substituted and unlocks that type's API:
 *
 *   <T extends Number>              upper bound, a single class or interface
 *   <T extends Animal & Swimmable>  several bounds, the class must come first
 */
public class BoundedTypeParameters {
    public static void main(String[] args) {
        NumberBox<Integer> intBox = new NumberBox<>(5);
        intBox.printAsDouble();   // doubleValue() exists because of the bound

        //   NumberBox<String> box = new NumberBox<>("x");
        //   error: type argument String is not within bounds of type-variable T

        MultiBoundBox<Fish> fishBox = new MultiBoundBox<>(new Fish());
        fishBox.describe();

        //   MultiBoundBox<Dog> box = new MultiBoundBox<>(new Dog());
        //   error: type argument Dog is not within bounds of type-variable T
        //          (Dog is an Animal, but does not implement Swimmable)
    }

    static class NumberBox<T extends Number> {
        private final T value;

        NumberBox(T value) {
            this.value = value;
        }

        void printAsDouble() {
            System.out.println(value.doubleValue());
        }
    }

    static class MultiBoundBox<T extends Animal & Swimmable> {
        private final T value;

        MultiBoundBox(T value) {
            this.value = value;
        }

        void describe() {
            value.display();
            value.swim();
        }
    }

    static class Animal {
        void display() {
            System.out.println("An animal");
        }
    }

    interface Swimmable {
        void swim();
    }

    static class Dog extends Animal {
    }

    static class Fish extends Animal implements Swimmable {
        @Override
        public void swim() {
            System.out.println("The fish is swimming");
        }
    }
}
