/*
 * Two ways to define a task:
 *   extend Thread          - simple, but burns the one superclass slot
 *   implement Runnable     - preferred, separates the task from the worker
 *
 * start() asks the OS for a new thread, which gets its own stack and program
 * counter and then runs run(). Calling run() directly executes it on the
 * current thread, with no concurrency at all.
 */
public class ThreadCreation {
    public static void main(String[] args) {
        new MyThread().start();

        new Thread(new MyRunnable()).start();

        new Thread(() -> System.out.println("Lambda thread is running")).start();

        startTwiceFails();
        interleaving();
    }

    // A thread object is single use: start() twice throws IllegalThreadStateException
    static void startTwiceFails() {
        Thread t = new Thread(() -> System.out.println("runs once"));
        t.start();

        try {
            t.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("The same thread cannot be started twice");
        }
    }

    // The interleaving of the output changes between runs - execution order
    // across threads is not deterministic
    static void interleaving() {
        Thread even = new Thread(() -> {
            for (int i = 2; i <= 10; i += 2) {
                System.out.println("even: " + i);
            }
        });

        Thread odd = new Thread(() -> {
            for (int i = 1; i <= 10; i += 2) {
                System.out.println("odd: " + i);
            }
        });

        even.start();
        odd.start();
    }

    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Thread subclass is running");
        }
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("Runnable is running");
        }
    }
}
