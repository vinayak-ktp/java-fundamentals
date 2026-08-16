import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Collectors are the recipes collect() uses to build a result. The grouping
 * ones take a downstream collector, which is what makes them composable.
 */
public class CollectorsConcept {
    public static void main(String[] args) {
        toCollections();
        joining();
        grouping();
        partitioning();
        summarising();
    }

    static void toCollections() {
        List<String> names = List.of("AA", "BBB", "AA");

        System.out.println(names.stream().collect(Collectors.toList()));
        System.out.println(names.stream().distinct().collect(Collectors.toSet()));

        // toMap needs a merge function when keys can collide
        Map<Integer, String> byLength = names.stream()
                .collect(Collectors.toMap(String::length, s -> s, (a, b) -> a + "|" + b));
        System.out.println(byLength);
    }

    static void joining() {
        System.out.println(List.of("AA", "BBB", "CCCC").stream()
                .collect(Collectors.joining("-", "[", "]")));
    }

    // groupingBy builds a Map from a classifier; the downstream shapes the values
    static void grouping() {
        List<String> names = List.of("AA", "BBB", "CCCC", "DD", "EEE");

        Map<Integer, List<String>> byLength = names.stream()
                .collect(Collectors.groupingBy(String::length));
        System.out.println(byLength);

        Map<Integer, List<String>> lowercasedByLength = names.stream()
                .collect(Collectors.groupingBy(
                        String::length,
                        Collectors.mapping(String::toLowerCase, Collectors.toList())));
        System.out.println(lowercasedByLength);

        Map<Integer, Long> countByLength = names.stream()
                .collect(Collectors.groupingBy(String::length, Collectors.counting()));
        System.out.println(countByLength);
    }

    // partitioningBy is groupingBy with a predicate: always exactly two keys
    static void partitioning() {
        Map<Boolean, List<Integer>> evenAndOdd = List.of(1, 2, 3, 4).stream()
                .collect(Collectors.partitioningBy(x -> x % 2 == 0));

        System.out.println(evenAndOdd);
    }

    static void summarising() {
        System.out.println(List.of(1, 2, 3, 4).stream()
                .collect(Collectors.summarizingInt(x -> x)));
        System.out.println(List.of(1, 2, 3, 4).stream()
                .collect(Collectors.averagingInt(x -> x)));
    }
}
