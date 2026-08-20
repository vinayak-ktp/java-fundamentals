/*
 * An instance lock protects one object. Static state is shared by every
 * instance, so it needs the *class* lock instead - Counter.class.
 *
 * The two locks are independent: a thread holding the class lock does not
 * block a thread holding an instance lock, which is exactly what the second
 * demo below shows.
 */
public class StaticSynchronization {
    public static void main(String[] args) throws InterruptedException {
        sharedClassLock();
        Thread.sleep(1500);
        differentLocksDoNotBlock();
    }

    static void sharedClassLock() {
        new Thread(Counter::increment).start();
        new Thread(Counter::increment).start();
    }

    static void differentLocksDoNotBlock() {
        Resource resource = new Resource();

        new Thread(Resource::staticMethod).start();   // locks Resource.class
        new Thread(resource::instanceMethod).start(); // locks the instance
    }

    static class Counter {
        static int count;

        static void increment() {
            synchronized (Counter.class) {
                count++;
                System.out.println("count = " + count);
            }
        }
    }

    static class Resource {
        static void staticMethod() {
            synchronized (Resource.class) {
                System.out.println("static method entered");
                pause();
                System.out.println("static method left");
            }
        }

        void instanceMethod() {
            synchronized (this) {
                System.out.println("instance method entered");
                pause();
                System.out.println("instance method left");
            }
        }

        private static void pause() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
