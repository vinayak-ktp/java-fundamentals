import java.util.List;

/*
 * A parallel stream splits the source with a Spliterator and runs the pipeline
 * on the common ForkJoinPool. It only pays off for large sources and genuinely
 * independent work, and it needs stateless, non interfering operations.
 *
 * forEach gives no order guarantee in parallel; forEachOrdered restores it at
 * the cost of some of the speedup.
 */
public class ParallelStreams {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        System.out.println("-- sequential");
        list.stream()
                .map(x -> x * 2)
                .forEach(x -> System.out.print(x + " "));
        System.out.println();

        System.out.println("-- parallel, unordered");
        list.parallelStream()
                .map(x -> x * 2)
                .forEach(x -> System.out.print(x + " "));
        System.out.println();

        System.out.println("-- parallel, ordered");
        list.parallelStream()
                .map(x -> x * 2)
                .forEachOrdered(x -> System.out.print(x + " "));
        System.out.println();

        // reduce is safe in parallel because it never mutates shared state
        System.out.println(list.parallelStream().reduce(0, Integer::sum));
    }
}
