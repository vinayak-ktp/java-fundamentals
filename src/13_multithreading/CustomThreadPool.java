import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * The Executors factories are shortcuts over ThreadPoolExecutor. Building it
 * directly exposes what actually drives the pool:
 *
 *   corePoolSize      threads kept alive even when idle
 *   maximumPoolSize   ceiling once the queue is full
 *   keepAliveTime     how long extra threads linger before dying
 *   workQueue         where tasks wait
 *
 * Order of events for a new task: fill the core threads, then the queue, then
 * grow to the maximum, and only then reject.
 */
public class CustomThreadPool {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,                            // core
                5,                            // maximum
                10, TimeUnit.SECONDS,         // keep alive for the extra threads
                new ArrayBlockingQueue<>(2)); // bounded queue

        for (int i = 1; i <= 5; i++) {
            int taskId = i;

            executor.execute(() -> {
                System.out.println("Task " + taskId + " ran on " + Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        System.out.println("pool size = " + executor.getPoolSize());
        System.out.println("queued = " + executor.getQueue().size());

        executor.shutdown();
    }
}
