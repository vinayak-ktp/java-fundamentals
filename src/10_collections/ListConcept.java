import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/*
 * List adds positional access on top of Collection: get, set, indexed add and
 * remove, indexOf and a bidirectional ListIterator.
 *
 *   ArrayList  - backed by an array, O(1) random access, shifting on insert
 *   LinkedList - doubly linked nodes, O(1) ends, O(n) random access
 */
public class ListConcept {
    public static void main(String[] args) {
        positionalAccess();
        listIterator();
        linkedList();
        immutableFactories();
    }

    static void positionalAccess() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));

        System.out.println(list.get(1));
        list.set(1, 5);
        list.addAll(0, List.of(9, 8, 7));

        // remove(int) removes by index, remove(Object) removes by value:
        // a real trap with a List<Integer>
        list.remove(0);
        list.remove(Integer.valueOf(5));

        System.out.println(list);
        System.out.println(list.indexOf(3) + " , " + list.lastIndexOf(3));
    }

    // A ListIterator can walk backwards and modify while iterating
    static void listIterator() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));

        ListIterator<Integer> it = list.listIterator(list.size());
        while (it.hasPrevious()) {
            System.out.print(it.previousIndex() + ":" + it.previous() + " ");
        }
        System.out.println();
    }

    static void linkedList() {
        LinkedList<Integer> list = new LinkedList<>(List.of(2, 3));

        list.addFirst(1);
        list.addLast(4);

        System.out.println(list.getFirst() + " , " + list.getLast());
        System.out.println(list);
    }

    // List.of and List.copyOf return immutable lists: every mutator throws
    static void immutableFactories() {
        List<Integer> fixed = List.of(1, 2, 3);

        try {
            fixed.add(4);
        } catch (UnsupportedOperationException e) {
            System.out.println("List.of is immutable");
        }

        List<Integer> copy = List.copyOf(fixed);
        System.out.println(copy);
    }
}
