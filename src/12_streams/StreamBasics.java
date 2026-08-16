import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/*
 * A stream is a pipeline over a data source, not a data structure. It has
 * three parts: a source, any number of lazy intermediate operations, and one
 * terminal operation that actually runs the pipeline.
 *
 * A stream is consumed once - reusing it throws IllegalStateException.
 */
public class StreamBasics {
    public static void main(String[] args) {
        pipeline();
        laziness();
        sources();
        singleUse();
    }

    static void pipeline() {
        List<Integer> list = new ArrayList<>(List.of(5, 12, 7, 14));

        list.stream()
                .filter(x -> x > 10)
                .map(x -> x * 2)
                .forEach(System.out::println);
    }

    // Nothing is printed until the terminal operation asks for elements
    static void laziness() {
        Stream<Integer> stream = List.of(1, 2, 3).stream()
                .map(x -> {
                    System.out.println("mapping " + x);
                    return x * 2;
                });

        System.out.println("no work has happened yet");
        stream.forEach(System.out::println);
    }

    static void sources() {
        System.out.println(List.of(1, 2, 3).stream().count());
        System.out.println(Stream.of("a", "b").count());
        System.out.println(java.util.Arrays.stream(new int[]{1, 2, 3}).sum());
        System.out.println(Stream.iterate(1, x -> x + 1).limit(5).count());
    }

    static void singleUse() {
        Stream<Integer> stream = List.of(1, 2, 3).stream();
        stream.count();

        try {
            stream.count();
        } catch (IllegalStateException e) {
            System.out.println("A stream cannot be reused");
        }
    }
}
