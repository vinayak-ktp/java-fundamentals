import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Iterators of the java.util collections are fail-fast: they track a
 * modification count and throw ConcurrentModificationException as soon as they
 * notice the collection changed behind their back. Removing through the
 * iterator is the supported way.
 */
public class FailFastIterator {
    public static void main(String[] args) {
        brokenRemoval();
        correctRemoval();
    }

    static void brokenRemoval() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));

        try {
            for (Integer value : list) {
                if (value == 3) {
                    list.remove(value);   // structural change during iteration
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException");
        }
    }

    static void correctRemoval() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == 3) {
                iterator.remove();
            }
        }

        System.out.println(list);

        // removeIf does the same thing in one line
        list.removeIf(value -> value == 4);
        System.out.println(list);
    }
}
