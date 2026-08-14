import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
 * A Set holds no duplicates - add returns false instead of storing a second copy.
 *
 *   HashSet       - hashing, O(1) average, no order guarantee
 *   LinkedHashSet - hashing plus a linked list, keeps insertion order
 *   TreeSet       - red-black tree, sorted, O(log n)
 */
public class SetConcept {
    public static void main(String[] args) {
        duplicatesAreRejected();
        orderingDiffers();
        constructors();
    }

    static void duplicatesAreRejected() {
        Set<String> names = new HashSet<>();
        System.out.println(names.add("Aditya"));   // true
        System.out.println(names.add("Aditya"));   // false
        System.out.println(names.contains("Rohit"));
        System.out.println(names.size());
    }

    static void orderingDiffers() {
        List<Integer> values = List.of(50, 10, 30, 20);

        System.out.println(new HashSet<>(values));         // arbitrary order
        System.out.println(new LinkedHashSet<>(values));   // insertion order
        System.out.println(new TreeSet<>(values));         // sorted
    }

    /*
     * The hash based sets share the same constructors. Capacity and load
     * factor only tune when the table is resized; they never change behaviour.
     */
    static void constructors() {
        Set<Integer> defaults = new LinkedHashSet<>();
        Set<Integer> withCapacity = new LinkedHashSet<>(100);
        Set<Integer> withLoadFactor = new LinkedHashSet<>(100, 0.8f);
        Set<Integer> fromCollection = new LinkedHashSet<>(List.of(1, 2, 3));

        System.out.println(defaults.size() + withCapacity.size()
                + withLoadFactor.size() + fromCollection.size());
    }
}
