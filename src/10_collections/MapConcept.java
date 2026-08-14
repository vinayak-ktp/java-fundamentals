import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/*
 * A Map stores key to value pairs with unique keys. It is not a Collection,
 * but keySet, values and entrySet expose collection views of it.
 *
 *   HashMap       - hashing, no order
 *   LinkedHashMap - insertion order
 *   TreeMap       - sorted by key
 */
public class MapConcept {
    public static void main(String[] args) {
        basics();
        nullSafeAccess();
        views();
        constructors();
    }

    static void basics() {
        Map<Integer, String> map = new HashMap<>();

        // put returns the previous value, or null when the key was new
        System.out.println(map.put(101, "Aditya"));
        map.put(102, "Rohit");
        map.put(103, "Rohan");

        System.out.println(map.put(103, "Abhay"));   // Rohan, the old value
        System.out.println(map.get(103));

        System.out.println(map.containsKey(101));
        System.out.println(map.containsValue("Aditya"));
        System.out.println(map.size() + " , " + map.isEmpty());

        map.remove(101);
        map.remove(102, "Rohit");   // only removes if the value matches too
        System.out.println(map);
    }

    static void nullSafeAccess() {
        Map<Integer, String> map = new HashMap<>();
        map.put(101, "Aditya");

        System.out.println(map.getOrDefault(105, "Unknown"));

        // putIfAbsent leaves an existing mapping alone, unlike put
        System.out.println(map.putIfAbsent(101, "Abhay"));
        System.out.println(map.putIfAbsent(102, "Rohit"));

        map.replace(101, "Sonu");
        map.replace(101, "Sonu", "Aditya");   // replace only if the value matches
        System.out.println(map);
    }

    static void views() {
        Map<Integer, String> map = new HashMap<>();
        map.put(101, "Aditya");
        map.put(102, "Rohit");

        Set<Integer> keys = map.keySet();
        Collection<String> values = map.values();
        Set<Map.Entry<Integer, String>> entries = map.entrySet();

        System.out.println(keys + " , " + values);

        for (Map.Entry<Integer, String> entry : entries) {
            System.out.println(entry.getKey() + " , " + entry.getValue());
        }

        // Map.of is immutable, like List.of
        Map<Integer, String> fixed = Map.of(101, "Aditya", 102, "Rohit");
        try {
            fixed.put(103, "Rohan");
        } catch (UnsupportedOperationException e) {
            System.out.println("Map.of is immutable");
        }
    }

    static void constructors() {
        Map<Integer, String> defaults = new LinkedHashMap<>();
        Map<Integer, String> withCapacity = new LinkedHashMap<>(100);
        Map<Integer, String> withLoadFactor = new LinkedHashMap<>(100, 0.8f);
        Map<Integer, String> copy = new LinkedHashMap<>(withLoadFactor);

        System.out.println(defaults.size() + withCapacity.size() + copy.size());
    }
}
