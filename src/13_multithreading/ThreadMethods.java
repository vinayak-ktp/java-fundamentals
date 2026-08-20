/*
 * The Thread API worth knowing, in one place.
 */
public class ThreadMethods {
    public static void main(String[] args) throws InterruptedException {
        sleeping();
        joining();
        yielding();
        interrupting();
        aliveAndNamed();
        priority();
        daemon();
    }

    // sleep parks the current thread for a while: RUNNABLE -> TIMED_WAITING -> RUNNABLE
    static void sleeping() throws InterruptedException {
        System.out.println("sleeping for 200ms");
        Thread.sleep(200);
        System.out.println("awake");
    }

    // join makes the caller wait for the other thread to terminate.
    // join(ms) gives up after the timeout even if it is still running.
    static void joining() throws InterruptedException {
        Thread worker = new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("worker done");
        });

        worker.start();
        worker.join();
        System.out.println("main continues after join");
    }

    /*
     * yield only hints that other threads of the same priority may run. The OS
     * is free to ignore it, and the thread stays RUNNABLE either way.
     */
    static void yielding() {
        Thread t = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("yielding thread: " + i);
                Thread.yield();
            }
        });
        t.start();
    }

    /*
     * interrupt() sets a flag asking the thread to stop - it never kills it.
     * A thread parked in sleep, join or wait throws InterruptedException
     * instead, and that throw clears the flag.
     *
     *   isInterrupted() reads the flag
     *   interrupted()   reads it and clears it (static, current thread only)
     */
    static void interrupting() throws InterruptedException {
        Thread worker = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                // busy work
            }
            System.out.println("worker noticed the interrupt and stopped");
        });

        worker.start();
        Thread.sleep(100);
        worker.interrupt();
        worker.join();
    }

    static void aliveAndNamed() throws InterruptedException {
        Thread worker = new Thread(() -> {
            System.out.println("running as " + Thread.currentThread().getName());
        });
        worker.setName("worker-1");

        System.out.println(worker.isAlive());   // false, not started yet
        worker.start();
        worker.join();
        System.out.println(worker.isAlive());   // false again, terminated
    }

    /*
     * MIN_PRIORITY 1, NORM_PRIORITY 5, MAX_PRIORITY 10. It is only a hint -
     * an OS may honour it fully, partially or not at all.
     */
    static void priority() {
        Thread t = new Thread(() -> System.out.println("priority thread"));
        t.setPriority(Thread.MAX_PRIORITY);
        System.out.println(t.getPriority());
        t.start();
    }

    /*
     * A daemon thread does not keep the JVM alive: when the last user thread
     * ends, daemons are killed wherever they are. The garbage collector is one.
     * setDaemon must be called before start().
     */
    static void daemon() throws InterruptedException {
        Thread background = new Thread(() -> {
            while (true) {
                // never ends on its own
            }
        });

        background.setDaemon(true);
        background.start();

        Thread.sleep(100);
        System.out.println("main ends, and the daemon dies with it");
    }
}
