/*
 * Thread states:
 *   NEW            created, not started
 *   RUNNABLE       eligible to run (the OS decides when it is on a core)
 *   BLOCKED        waiting for a monitor lock
 *   WAITING        wait(), join() with no timeout
 *   TIMED_WAITING  sleep(ms), wait(ms), join(ms)
 *   TERMINATED     run() finished
 */
public class ThreadLifecycle {
    public static void main(String[] args) throws InterruptedException {
        Thread mainThread = Thread.currentThread();

        Thread worker = new Thread(() -> {
            System.out.println("worker name: " + Thread.currentThread().getName());
            System.out.println("worker id: " + Thread.currentThread().threadId());
            System.out.println("main thread state seen from the worker: " + mainThread.getState());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("before start: " + worker.getState());   // NEW

        worker.start();
        System.out.println("after start: " + worker.getState());    // RUNNABLE

        Thread.sleep(100);
        System.out.println("while sleeping: " + worker.getState()); // TIMED_WAITING

        worker.join();
        System.out.println("after join: " + worker.getState());     // TERMINATED
    }
}
