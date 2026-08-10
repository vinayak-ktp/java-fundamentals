/*
 * An abstract class cannot be instantiated and is meant to be extended.
 * It may mix abstract methods with concrete ones, and it may have
 * constructors, static members, private methods and final methods.
 * It cannot be final, since that would make it useless.
 */
public class AbstractClasses {
    public static void main(String[] args) {
        Animal animal = new Dog("Bruno");
        animal.makeSound();
        animal.sleep();

        Car car = new FuelCar();
        car.start();       // inherited implementation
        car.accelerate();  // supplied by the subclass
    }

    abstract static class Animal {
        String name;

        Animal(String name) {
            this.name = name;
        }

        void makeSound() {
            System.out.println("Making a sound");
        }

        final void sleep() {
            System.out.println("Sleeping");
        }
    }

    static class Dog extends Animal {
        Dog(String name) {
            super(name);
        }

        @Override
        void makeSound() {
            System.out.println(name + " is barking");
        }
    }

    abstract static class Car {
        void start() {
            System.out.println("Car started");
        }

        abstract void accelerate();

        abstract void brake();
    }

    static class FuelCar extends Car {
        @Override
        void accelerate() {
            System.out.println("Fuel car is accelerating");
        }

        @Override
        void brake() {
            System.out.println("Fuel car is stopping");
        }
    }
}
