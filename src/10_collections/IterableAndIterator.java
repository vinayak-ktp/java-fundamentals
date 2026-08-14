import java.util.Iterator;
import java.util.TreeSet;

/*
 * Collection extends Iterable, so every collection can hand out an Iterator
 * and can be used in the enhanced for loop - which the compiler rewrites into
 * exactly the iterator loop below.
 */
public class IterableAndIterator {
    public static void main(String[] args) {
        TreeSet<Integer> numbers = new TreeSet<>();
        numbers.add(30);
        numbers.add(10);
        numbers.add(20);

        Iterator<Integer> iterator = numbers.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();

        for (int number : numbers) {
            System.out.print(number + " ");
        }
        System.out.println();

        // remove() deletes the element returned by the last next() call
        Iterator<Integer> removing = numbers.iterator();
        while (removing.hasNext()) {
            if (removing.next() == 20) {
                removing.remove();
            }
        }
        System.out.println(numbers);
    }
}
