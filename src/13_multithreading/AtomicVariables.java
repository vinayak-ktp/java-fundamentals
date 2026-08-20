import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/*
 * The atomic classes fix count++ without a lock, using a CPU level
 * compare-and-set: read the current value, compute the new one, then swap only
 * if nothing changed in between. If it did, retry.
 *
 * Lock free and fast under moderate contention, and the retry loop is written
 * out in casByHand() below to show what incrementAndGet does for you.
 */
public class AtomicVariables {
    public static void main(String[] args) throws InterruptedException {
        counter();
        seatBooking();
    }

    static void counter() throws InterruptedException {
        Counter counter = new Counter();

        Runnable loop = () -> {
            for (int i = 0; i < 10_000; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(loop);
        Thread t2 = new Thread(loop);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("count = " + counter.get());   // always 20000
    }

    // Exactly one thread can win the compare-and-set, so the seat is never double booked
    static void seatBooking() throws InterruptedException {
        SeatBooking booking = new SeatBooking();

        Thread t1 = new Thread(() -> System.out.println("t1 booked: " + booking.book("Aditya")));
        Thread t2 = new Thread(() -> System.out.println("t2 booked: " + booking.book("Rohit")));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("seat = " + booking.seat.get());
    }

    static class Counter {
        private final AtomicInteger count = new AtomicInteger(0);

        void increment() {
            count.incrementAndGet();
        }

        void casByHand() {
            while (true) {
                int current = count.get();
                int next = current + 1;

                if (count.compareAndSet(current, next)) {
                    return;
                }
                // Someone else won the race, read again and retry
            }
        }

        int get() {
            return count.get();
        }
    }

    static class SeatBooking {
        final AtomicReference<String> seat = new AtomicReference<>("EMPTY");

        boolean book(String name) {
            return seat.compareAndSet("EMPTY", name);
        }
    }
}
