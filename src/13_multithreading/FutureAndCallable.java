import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * Runnable returns nothing and cannot throw a checked exception.
 * Callable<T> returns a value and may throw, and submit() hands back a Future
 * to collect the result later. get() blocks until the task is done.
 *
 * The difference that bites: an exception inside execute() surfaces on the
 * worker thread as an uncaught error, while an exception inside submit() is
 * stored in the Future and rethrown, wrapped in ExecutionException, by get().
 */
public class FutureAndCallable {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Integer> result = executor.submit(() -> {
            Thread.sleep(300);
            return 10;
        });

        System.out.println("submitted, main is free to do other work");

        try {
            System.out.println("result = " + result.get());
        } catch (ExecutionException e) {
            System.out.println("task failed: " + e.getCause());
        }

        Future<Integer> failing = executor.submit(() -> 10 / 0);

        try {
            failing.get();
        } catch (ExecutionException e) {
            System.out.println("caught through the Future: " + e.getCause());
        }

        System.out.println("done = " + failing.isDone() + ", cancelled = " + failing.isCancelled());

        executor.shutdown();
    }
}
