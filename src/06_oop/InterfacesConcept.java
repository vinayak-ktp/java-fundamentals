public class InterfacesConcept {
    public static void main(String[] args) {
        // Interface fields are implicitly public static final
        System.out.println(MathConstant.PI);

        StreetDog dog = new StreetDog();
        dog.eat();
        dog.bark();

        Multi multi = new Multi();
        multi.first();
        multi.second();
    }

    interface MathConstant {
        double PI = 3.14;
        int RADIUS = 10;
    }

    // Interfaces can extend other interfaces
    interface Animal {
        void eat();
    }

    interface Dog extends Animal {
        void bark();
    }

    static class StreetDog implements Dog {
        @Override
        public void eat() {
            System.out.println("Eating");
        }

        @Override
        public void bark() {
            System.out.println("Barking");
        }
    }

    // A class may implement any number of interfaces:
    // this is how Java gets multiple inheritance of type
    interface First {
        void first();
    }

    interface Second {
        void second();
    }

    static class Multi implements First, Second {
        @Override
        public void first() {
            System.out.println("first");
        }

        @Override
        public void second() {
            System.out.println("second");
        }
    }
}
