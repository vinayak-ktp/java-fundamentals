/*
 * count++ is three operations - read, add, write - so two threads can read the
 * same value and one increment is lost. The region that must not interleave is
 * the critical section, and synchronized makes threads take turns through it
 * by acquiring the object's monitor lock.
 *
 * A synchronized method locks `this`; a synchronized block locks whatever
 * object you name, so it can cover fewer statements.
 */
public class Synchronization {
    public static void main(String[] args) throws InterruptedException {
        UnsafeCounter unsafe = new UnsafeCounter();
        incrementTwice(unsafe::increment);
        System.out.println("unsafe: " + unsafe.count);   // usually below 20000

        SafeCounter safe = new SafeCounter();
        incrementTwice(safe::increment);
        System.out.println("safe:   " + safe.getCount());   // always 20000

        methodLevelLockIsShared();
    }

    // Runs the given increment 10000 times on each of two threads
    static void incrementTwice(Runnable increment) throws InterruptedException {
        Runnable loop = () -> {
            for (int i = 0; i < 10_000; i++) {
                increment.run();
            }
        };

        Thread t1 = new Thread(loop);
        Thread t2 = new Thread(loop);

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    /*
     * Two synchronized methods on the same object share one lock, so a thread
     * inside m1 blocks another thread trying to enter m2.
     */
    static void methodLevelLockIsShared() {
        Resource resource = new Resource();

        new Thread(resource::m1).start();
        new Thread(resource::m2).start();
    }

    static class UnsafeCounter {
        int count;

        void increment() {
            count++;
        }
    }

    static class SafeCounter {
        private int count;

        void increment() {
            // Only the critical section is locked, not the whole method
            synchronized (this) {
                count++;
            }
        }

        int getCount() {
            return count;
        }
    }

    static class Resource {
        synchronized void m1() {
            System.out.println(Thread.currentThread().getName() + " entered m1");
            pause();
            System.out.println(Thread.currentThread().getName() + " left m1");
        }

        synchronized void m2() {
            System.out.println(Thread.currentThread().getName() + " entered m2");
            pause();
            System.out.println(Thread.currentThread().getName() + " left m2");
        }

        private void pause() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
