import java.util.Comparator;
import java.util.PriorityQueue;

/*
 * A PriorityQueue is a binary heap: poll always returns the smallest element
 * by natural order, or by the comparator you supply. Iteration order is *not*
 * sorted - only the head is guaranteed.
 */
public class PriorityQueueConcept {
    public static void main(String[] args) {
        minHeap();
        maxHeap();
        customOrder();
    }

    static void minHeap() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.offer(30);
        pq.offer(10);
        pq.offer(20);

        while (!pq.isEmpty()) {
            System.out.print(pq.poll() + " ");   // 10 20 30
        }
        System.out.println();
    }

    // Reversing the comparator turns it into a max heap
    static void maxHeap() {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.reverseOrder());
        pq.offer(30);
        pq.offer(10);
        pq.offer(20);

        while (!pq.isEmpty()) {
            System.out.print(pq.poll() + " ");   // 30 20 10
        }
        System.out.println();
    }

    static void customOrder() {
        PriorityQueue<String> byLength = new PriorityQueue<>(Comparator.comparingInt(String::length));
        byLength.offer("Rohan");
        byLength.offer("Om");
        byLength.offer("Aditya");

        System.out.println(byLength.poll());   // Om
    }
}
