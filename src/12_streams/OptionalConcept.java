import java.util.Optional;

/*
 * Optional is a container that either holds a value or is empty. It replaces
 * "returns null sometimes" with a type the caller cannot ignore.
 *
 * Think of it as a stream of at most one element: map, flatMap and filter all
 * behave the same way.
 */
public class OptionalConcept {
    public static void main(String[] args) {
        creating();
        readingSafely();
        transforming();
        replacingNestedNullChecks();
    }

    static void creating() {
        Optional<String> present = Optional.of("Aditya");
        Optional<String> empty = Optional.empty();
        Optional<String> maybe = Optional.ofNullable(null);   // of(null) would throw

        System.out.println(present.isPresent() + " , " + empty.isEmpty() + " , " + maybe.isEmpty());
    }

    static void readingSafely() {
        Optional<String> name = Optional.ofNullable("Aditya");

        name.ifPresent(System.out::println);
        name.ifPresentOrElse(System.out::println, () -> System.out.println("Unknown"));

        System.out.println(name.orElse("Unknown"));
        System.out.println(name.orElseGet(() -> "computed only when empty"));

        // get() and orElseThrow() throw on an empty Optional - prefer the others
        System.out.println(name.orElseThrow());
    }

    static void transforming() {
        Optional.of("Aditya")
                .map(String::length)
                .filter(length -> length > 4)
                .ifPresent(System.out::println);
    }

    /*
     * The nested null checks this replaces:
     *   if (user != null && user.address != null && user.address.city != null)
     *
     * flatMap is needed where the field is itself an Optional, otherwise the
     * result would be Optional<Optional<Address>>.
     */
    static void replacingNestedNullChecks() {
        getUser()
                .flatMap(user -> user.address)
                .map(address -> address.city)
                .ifPresent(System.out::println);
    }

    static Optional<User> getUser() {
        Address address = new Address();
        address.city = "Delhi";

        User user = new User();
        user.address = Optional.of(address);

        return Optional.of(user);
    }

    static class User {
        Optional<Address> address = Optional.empty();
    }

    static class Address {
        String city;
    }
}
