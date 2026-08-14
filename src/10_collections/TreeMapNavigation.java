import java.util.Map;
import java.util.TreeMap;

/*
 * TreeMap is the map counterpart of TreeSet: keys are kept sorted, and it
 * implements SortedMap and NavigableMap with the same family of queries.
 */
public class TreeMapNavigation {
    public static void main(String[] args) {
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(103, "Rohan");
        map.put(101, "Aditya");
        map.put(102, "Rohit");

        System.out.println(map);   // iterates in key order

        System.out.println(map.firstEntry());
        System.out.println(map.lastEntry());
        System.out.println(map.firstKey() + " , " + map.lastKey());

        System.out.println(map.headMap(102));        // keys below 102
        System.out.println(map.tailMap(102));        // keys from 102 up
        System.out.println(map.subMap(101, 103));    // [101, 103)

        System.out.println(map.lowerEntry(102));     // strictly below
        System.out.println(map.floorEntry(102));     // at or below
        System.out.println(map.higherEntry(102));    // strictly above
        System.out.println(map.ceilingEntry(102));   // at or above

        System.out.println(map.descendingMap());

        Map.Entry<Integer, String> first = map.pollFirstEntry();
        System.out.println("removed " + first + " leaving " + map);
    }
}
