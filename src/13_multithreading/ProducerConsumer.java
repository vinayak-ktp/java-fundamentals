/*
 * Three versions of the same problem.
 *
 *   1. No coordination at all - the consumer reads stale or missing items.
 *   2. synchronized plus a busy wait - correct, but the waiting thread holds
 *      the lock while spinning, so the other one can never get in: deadlock.
 *   3. wait() / notify() - wait releases the lock and parks the thread until
 *      another thread notifies it. Always call them inside a synchronized block
 *      and always loop on the condition, never use a plain if.
 */
public class ProducerConsumer {
    public static void main(String[] args) {
        runWith(new CoordinatedBox());
    }

    static void runWith(CoordinatedBox box) {
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    Thread.sleep(100);
                    box.produce(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    Thread.sleep(70);
                    box.consume();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        producer.start();
        consumer.start();
    }

    // 1. Broken: nothing stops the consumer reading an empty box
    static class UnsafeBox {
        Integer item;

        void produce(int value) {
            item = value;
            System.out.println("produced " + item);
        }

        void consume() {
            System.out.println("consumed " + item);
            item = null;
        }
    }

    // 2. Busy waiting inside a synchronized method deadlocks: the waiting
    // thread never releases the monitor the other thread needs
    static class BusyWaitBox {
        private Integer item;
        private boolean full;

        synchronized void produce(int value) {
            while (full) {
                // spins forever while holding the lock
            }
            item = value;
            full = true;
        }

        synchronized void consume() {
            while (!full) {
                // same problem
            }
            item = null;
            full = false;
        }
    }

    // 3. Correct: wait() gives up the lock, notify() wakes the other side
    static class CoordinatedBox {
        private Integer item;
        private boolean full;

        synchronized void produce(int value) throws InterruptedException {
            while (full) {
                wait();
            }

            item = value;
            full = true;
            System.out.println("produced " + item);
            notify();
        }

        synchronized void consume() throws InterruptedException {
            while (!full) {
                wait();
            }

            System.out.println("consumed " + item);
            item = null;
            full = false;
            notify();
        }
    }
}
