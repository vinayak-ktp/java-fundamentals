import java.util.Iterator;
import java.util.TreeSet;

/*
 * TreeSet implements SortedSet and NavigableSet. Because it is a balanced
 * binary search tree, the smallest element is the leftmost node and the
 * largest is the rightmost, and every query below costs O(log n).
 */
public class TreeSetNavigation {
    public static void main(String[] args) {
        TreeSet<Integer> set = new TreeSet<>();
        set.add(80);
        set.add(23);
        set.add(10);
        set.add(90);
        set.add(50);

        sortedSetViews(set);
        navigableSetQueries(set);
        polling(new TreeSet<>(set));
    }

    static void sortedSetViews(TreeSet<Integer> set) {
        System.out.println(set.first() + " , " + set.last());

        // Views are half open by default: the from element is in, the to element is out
        System.out.println(set.headSet(80));          // strictly below 80
        System.out.println(set.tailSet(80));          // 80 and above
        System.out.println(set.subSet(23, 80));       // [23, 80)

        // The overloads make each bound's inclusiveness explicit
        System.out.println(set.headSet(80, true));
        System.out.println(set.subSet(10, false, 80, true));
    }

    static void navigableSetQueries(TreeSet<Integer> set) {
        System.out.println(set.lower(50));     // greatest element strictly below
        System.out.println(set.floor(50));     // greatest element at or below
        System.out.println(set.higher(50));    // smallest element strictly above
        System.out.println(set.ceiling(50));   // smallest element at or above

        System.out.println(set.descendingSet());

        Iterator<Integer> it = set.descendingIterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println();
    }

    // poll removes as it returns, so it works on a copy here
    static void polling(TreeSet<Integer> set) {
        System.out.println(set.pollFirst());
        System.out.println(set.pollLast());
        System.out.println(set);
    }
}
