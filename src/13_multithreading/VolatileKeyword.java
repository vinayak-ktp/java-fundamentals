/*
 * Each thread may cache a field in a register or CPU cache, so a write by one
 * thread can stay invisible to another indefinitely - the loop below can spin
 * forever without volatile.
 *
 * volatile guarantees visibility: reads and writes go to main memory. It does
 * *not* make compound operations such as count++ atomic - that still needs
 * synchronized or an atomic class.
 */
public class VolatileKeyword {

    private static volatile boolean flag = false;

    public static void main(String[] args) {
        Thread writer = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            flag = true;
            System.out.println("writer set the flag");
        });

        Thread reader = new Thread(() -> {
            while (!flag) {
                // spin until the write becomes visible
            }
            System.out.println("reader saw the flag");
        });

        writer.start();
        reader.start();
    }
}
