import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/*
 * A method reference is a lambda that does nothing but call an existing method.
 *
 *   Class::staticMethod          static
 *   instance::instanceMethod     bound to one object
 *   Class::instanceMethod        unbound, the first argument is the receiver
 *   Class::new                   constructor
 */
public class MethodReferences {
    public static void main(String[] args) {
        Function<String, Integer> parseLambda = s -> Integer.parseInt(s);
        Function<String, Integer> parseRef = Integer::parseInt;
        System.out.println(parseLambda.apply("10") + parseRef.apply("20"));

        Consumer<String> print = System.out::println;   // bound to System.out
        print.accept("bound instance method");

        // Unbound: the String the function receives becomes the receiver
        Function<String, String> upper = String::toUpperCase;
        System.out.println(upper.apply("aditya"));

        BiFunction<String, String, Boolean> startsWith = String::startsWith;
        System.out.println(startsWith.apply("Aditya", "Ad"));

        Supplier<List<String>> newList = ArrayList::new;
        System.out.println(newList.get().size());

        List.of("Aditya", "Rohit").forEach(System.out::println);
    }
}
