/*
 * Interfaces gained bodies over time:
 *   Java 8 - default and static methods
 *   Java 9 - private methods, to share code between default methods
 *
 * default methods are how the JDK adds methods to published interfaces
 * (List.sort, Collection.stream) without breaking existing implementations.
 */
public class InterfaceMethods {
    public static void main(String[] args) {
        Vehicle vehicle = new Car();
        vehicle.drive();       // inherited default method

        // static interface methods are not inherited, so they are called on the interface.
        // Through an instance or an implementing class:
        //   vehicle.brake();
        //   error: illegal static interface method call
        Vehicle.brake();

        new Bike().drive();    // the default method overridden
    }

    interface Vehicle {
        default void drive() {
            System.out.println("Vehicle is driving");
            accelerate();
        }

        static void brake() {
            System.out.println("Vehicle is braking");
        }

        private void accelerate() {
            System.out.println("Vehicle is accelerating");
        }
    }

    static class Car implements Vehicle {
    }

    static class Bike implements Vehicle {
        @Override
        public void drive() {
            System.out.println("Bike is driving");
        }
    }
}
