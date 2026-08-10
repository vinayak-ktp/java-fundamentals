public class Polymorphism {
    public static void main(String[] args) {
        // The reference type decides what you may call,
        // the object type decides which implementation runs.
        Payment payment = new DebitCard();
        payment.pay();

        Car car = new ElectricCar();
        car.start();
        car.accelerate();
        car.brake();
    }

    interface Payment {
        void pay();
    }

    static class CreditCard implements Payment {
        @Override
        public void pay() {
            System.out.println("Paying via credit card");
        }
    }

    static class DebitCard implements Payment {
        @Override
        public void pay() {
            System.out.println("Paying via debit card");
        }
    }

    interface Car {
        void start();

        void accelerate();

        void brake();
    }

    static class FuelCar implements Car {
        @Override
        public void start() {
            System.out.println("Fuel car has started");
        }

        @Override
        public void accelerate() {
            System.out.println("Fuel car is accelerating");
        }

        @Override
        public void brake() {
            System.out.println("Fuel car is stopping");
        }
    }

    static class ElectricCar implements Car {
        @Override
        public void start() {
            System.out.println("Electric car has started");
        }

        @Override
        public void accelerate() {
            System.out.println("Electric car is accelerating");
        }

        @Override
        public void brake() {
            System.out.println("Electric car is stopping");
        }
    }
}
