import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * The methods every Collection implementation must provide. Membership tests
 * such as contains and remove rely on equals, so value types stored in a
 * collection should override equals and hashCode.
 */
public class CollectionInterfaceMethods {
    public static void main(String[] args) {
        Collection<Integer> c = new ArrayList<>();

        c.add(1);
        c.add(2);
        c.add(3);

        System.out.println(c.size());
        System.out.println(c.isEmpty());
        System.out.println(c.contains(2));

        // Bulk operations
        c.addAll(List.of(5, 6, 7));
        System.out.println(c.containsAll(List.of(1, 2, 3)));

        c.removeAll(List.of(6, 7));      // difference
        System.out.println(c);

        c.retainAll(List.of(1, 2, 5));   // intersection
        System.out.println(c);

        c.remove(5);
        System.out.println(c);

        // Converting to an array: the typed overload avoids casts later
        Object[] asObjects = c.toArray();
        Integer[] asIntegers = c.toArray(new Integer[0]);
        System.out.println(asObjects.length + " , " + asIntegers.length);

        c.clear();
        System.out.println(c);
    }
}
