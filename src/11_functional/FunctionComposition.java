import java.util.function.Consumer;
import java.util.function.Function;

/*
 * Functions combine into pipelines instead of nesting calls:
 *   f.andThen(g)  -> g(f(x))
 *   f.compose(g)  -> f(g(x))
 *
 * Consumers chain the same way, with every consumer seeing the same input.
 */
public class FunctionComposition {
    public static void main(String[] args) {
        functions();
        consumers();
    }

    static void functions() {
        Function<Integer, Integer> add2 = x -> x + 2;
        Function<Integer, Integer> multiply3 = x -> x * 3;

        // (x + 2) * 3, written twice
        System.out.println(multiply3.apply(add2.apply(2)));
        System.out.println(add2.andThen(multiply3).apply(2));

        // x * 3 + 2
        System.out.println(add2.compose(multiply3).apply(2));
    }

    static void consumers() {
        Consumer<String> printAsIs = System.out::println;
        Consumer<String> printUpperCase = s -> System.out.println(s.toUpperCase());

        printAsIs.andThen(printUpperCase).accept("Aditya");
    }
}
