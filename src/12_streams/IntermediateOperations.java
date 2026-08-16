import java.util.List;
import java.util.stream.Stream;

/*
 * Intermediate operations return a new stream and stay lazy.
 *
 * Stateless: filter, map, flatMap, peek, limit, skip - each element is handled
 * on its own. Stateful: sorted and distinct - they must see other elements
 * (or all of them) before they can emit anything.
 */
public class IntermediateOperations {
    public static void main(String[] args) {
        mapAndFilter();
        flatten();
        sortAndDeduplicate();
        limitAndSkip();
        peekForDebugging();
    }

    static void mapAndFilter() {
        List.of(11, 34, 1, 13, 4).stream()
                .filter(x -> x > 10)
                .map(x -> x * 2)
                .forEach(System.out::println);
    }

    // flatMap turns a stream of collections into one flat stream
    static void flatten() {
        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));

        nested.stream()
                .flatMap(inner -> inner.stream())
                .map(x -> x * 2)
                .forEach(System.out::println);
    }

    static void sortAndDeduplicate() {
        List.of(5, 1, 5, 3, 1).stream()
                .distinct()          // uses equals and hashCode
                .sorted()
                .forEach(System.out::println);
    }

    // limit short circuits, which is what makes an infinite source usable
    static void limitAndSkip() {
        Stream.iterate(1, x -> x + 1)
                .limit(10)
                .skip(5)
                .forEach(System.out::println);
    }

    // peek is for observing a pipeline, never for changing state
    static void peekForDebugging() {
        long count = List.of(1, 2, 3).stream()
                .peek(x -> System.out.println("saw " + x))
                .filter(x -> x > 1)
                .count();

        System.out.println(count);
    }
}
