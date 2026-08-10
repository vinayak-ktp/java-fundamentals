public class LocalAndAnonymousClasses {
    public static void main(String[] args) {
        localClass();
        anonymousClass();
        anonymousInterface();
    }

    /*
     * A local class lives inside a method, constructor, block or loop.
     * It can only capture locals that are effectively final - never reassigned
     * after initialisation - because it captures a copy of the value.
     */
    static void localClass() {
        int captured = 5;

        class Greeter {
            void sayHello() {
                System.out.println("Captured value is " + captured);
            }
        }

        new Greeter().sayHello();
    }

    // An anonymous class is a one-off subclass declared and instantiated at once
    static void anonymousClass() {
        Person person = new Person() {
            String name = "Aditya";

            @Override
            void introduce() {
                greet();
                System.out.println("Hi, I am " + name);
            }

            void greet() {
                System.out.println("Hello");
            }
        };

        person.introduce();
    }

    // For a single abstract method a lambda replaces the anonymous class entirely
    static void anonymousInterface() {
        Runnable viaAnonymous = new Runnable() {
            @Override
            public void run() {
                System.out.println("Anonymous Runnable");
            }
        };

        Runnable viaLambda = () -> System.out.println("Lambda Runnable");

        viaAnonymous.run();
        viaLambda.run();
    }

    static class Person {
        void introduce() {
            System.out.println("Hi, I am a person");
        }
    }
}
