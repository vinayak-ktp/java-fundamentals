import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/*
 * A terminal operation runs the pipeline and closes the stream.
 * findFirst, findAny, anyMatch, allMatch and noneMatch short circuit: they stop
 * as soon as the answer is known.
 */
public class TerminalOperations {
    public static void main(String[] args) {
        collecting();
        reducing();
        matching();
        primitiveStatistics();
    }

    static void collecting() {
        List<Integer> list = List.of(1, 13, 11, 9);

        System.out.println(list.stream().map(x -> x + 1).toList());
        System.out.println(list.stream().map(x -> x + 1).collect(Collectors.toSet()));
        System.out.println(list.stream().count());
    }

    // reduce folds the stream into a single value, starting from an identity
    static void reducing() {
        List<Integer> list = List.of(1, 2, 3, 4);

        System.out.println(list.stream().reduce(0, (a, b) -> a + b));

        // Without an identity the result may be absent, hence the Optional
        Optional<Integer> product = list.stream().reduce((a, b) -> a * b);
        System.out.println(product.orElse(0));
    }

    static void matching() {
        List<Integer> list = List.of(1, 13, 11, 9);

        System.out.println(list.stream().anyMatch(x -> x > 10));
        System.out.println(list.stream().allMatch(x -> x > 10));
        System.out.println(list.stream().noneMatch(x -> x > 100));

        System.out.println(list.stream().filter(x -> x > 10).findFirst().orElse(-1));
    }

    /*
     * sum, average, max and min live on the primitive streams, so map to one
     * first with mapToInt / mapToLong / mapToDouble. That also avoids boxing.
     */
    static void primitiveStatistics() {
        List<Integer> list = List.of(1, 13, 11, 9);

        OptionalDouble average = list.stream()
                .filter(x -> x > 10)
                .mapToInt(x -> x)
                .average();

        System.out.println(average.getAsDouble());
        System.out.println(list.stream().mapToInt(x -> x).sum());
        System.out.println(list.stream().mapToInt(x -> x).max().getAsInt());
    }
}
