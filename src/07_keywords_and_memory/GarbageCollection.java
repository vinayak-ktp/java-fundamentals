import java.util.ArrayList;
import java.util.List;

/*
 * The garbage collector reclaims heap objects that are no longer reachable
 * from any live reference. It runs on a daemon thread, so it never keeps the
 * JVM alive, and you cannot force it - System.gc() is only a hint.
 *
 * Anything still reachable can never be collected, which is what makes the
 * growing list below fatal.
 */
public class GarbageCollection {
    public static void main(String[] args) {
        makeGarbage();
        exhaustTheHeap();
    }

    static void makeGarbage() {
        String s = new String("collect me");
        s = null;   // the object is now unreachable and eligible for collection

        System.gc();   // a request, not a command
        System.out.println("Requested a collection");
    }

    /*
     * Every block stays reachable through the list, so the heap fills up and
     * the JVM throws OutOfMemoryError. Try it with a small heap:
     *   java -Xmx64m GarbageCollection
     */
    static void exhaustTheHeap() {
        List<int[]> blocks = new ArrayList<>();
        int count = 0;

        try {
            while (true) {
                blocks.add(new int[250_000]);   // 250k ints ~ 1 MB
                System.out.println("Allocated block " + (++count));
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Heap exhausted after " + count + " blocks");
        }
    }
}
