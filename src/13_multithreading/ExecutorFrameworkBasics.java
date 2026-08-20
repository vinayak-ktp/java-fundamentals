import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * Creating a thread per task is expensive and unbounded. A thread pool reuses
 * a fixed set of workers and queues the rest, so five tasks below run on two
 * threads.
 *
 * shutdown() stops accepting work and lets running tasks finish;
 * shutdownNow() also interrupts them. Forgetting both leaves the JVM alive,
 * because pool threads are user threads.
 */
public class ExecutorFrameworkBasics {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int i = 1; i <= 5; i++) {
            int taskId = i;   // must be effectively final to be captured

            executor.execute(() -> System.out.println(
                    "Task " + taskId + " ran on " + Thread.currentThread().getName()));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("all tasks finished");
    }
}
