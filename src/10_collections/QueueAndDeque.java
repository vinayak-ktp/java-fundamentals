import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

/*
 * Queue has two flavours of each operation: one throws on failure, one
 * signals with a special value. Prefer the returning variants.
 *
 *   insert   add    / offer
 *   remove   remove / poll
 *   inspect  element / peek
 *
 * ArrayDeque implements Deque, a double ended queue, so it can also serve as
 * a faster Stack.
 */
public class QueueAndDeque {
    public static void main(String[] args) {
        queue();
        deque();
        asStack();
    }

    static void queue() {
        Queue<Integer> queue = new ArrayDeque<>();

        queue.offer(1);
        queue.offer(2);
        queue.offer(3);

        System.out.println(queue.peek());   // 1, FIFO order, null if empty
        System.out.println(queue.poll());   // removes 1
        System.out.println(queue);

        queue.clear();
        System.out.println(queue.poll());   // null instead of an exception

        try {
            queue.remove();
        } catch (java.util.NoSuchElementException e) {
            System.out.println("remove() throws on an empty queue");
        }
    }

    static void deque() {
        Deque<Integer> deque = new ArrayDeque<>();

        deque.offerFirst(1);
        deque.offerLast(2);
        deque.offerFirst(0);

        System.out.println(deque);
        System.out.println(deque.peekFirst() + " , " + deque.peekLast());
        System.out.println(deque.pollFirst() + " , " + deque.pollLast());
    }

    static void asStack() {
        Deque<Integer> stack = new ArrayDeque<>();

        stack.push(1);
        stack.push(2);
        stack.push(3);

        System.out.println(stack.pop());    // 3, LIFO
        System.out.println(stack.peek());
    }
}
