import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/*
 * java.util.function ships the shapes you would otherwise keep redeclaring:
 *
 *   Function<T,R>   T in, R out          apply
 *   BiFunction<T,U,R>  two in, one out   apply
 *   UnaryOperator<T>   T in, T out       apply
 *   Consumer<T>     T in, nothing out    accept
 *   Supplier<T>     nothing in, T out    get
 *   Predicate<T>    T in, boolean out    test
 */
public class BuiltInFunctionalInterfaces {
    public static void main(String[] args) {
        Function<Integer, Integer> square = x -> x * x;
        System.out.println(square.apply(5));

        BiFunction<Integer, Integer, Integer> sum = (a, b) -> a + b;
        System.out.println(sum.apply(2, 3));

        UnaryOperator<String> shout = s -> s.toUpperCase();
        System.out.println(shout.apply("aditya"));

        Consumer<Integer> print = x -> System.out.println(x);
        print.accept(7);

        Supplier<Double> random = () -> Math.random();
        System.out.println(random.get());

        Predicate<Integer> isEven = x -> x % 2 == 0;
        System.out.println(isEven.test(7));

        // forEach takes a Consumer, which is why this reads so cleanly
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
        list.forEach(print);
    }
}
