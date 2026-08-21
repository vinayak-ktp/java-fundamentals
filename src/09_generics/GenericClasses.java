public class GenericClasses {
    public static void main(String[] args) {
        // Box<Integer>: the type argument fills in the type parameter T
        Box<Integer> intBox = new Box<>(10);
        Box<String> stringBox = new Box<>("Hello");
        Box<Boolean> boolBox = new Box<>(false);

        // No cast on the way out, and no wrong cast is even possible
        System.out.println(intBox.getValue() + 5);
        System.out.println(stringBox.getValue());
        System.out.println(boolBox.getValue());

        Pair<Integer, String> pair = new Pair<>(23, "Aditya");
        System.out.println(pair.getFirst() + " , " + pair.getSecond());
    }

    /*
     * Type erasure removes T at runtime, which rules all of these out:
     *   T value = new T();          error: unexpected type
     *   T[] array = new T[10];      error: generic array creation
     *   static T shared;            error: non-static type variable T cannot be
     *                                      referenced from a static context
     *   List<int> primitives;       error: unexpected type
     */
    static class Box<T> {
        private T value;

        Box(T value) {
            this.value = value;
        }

        T getValue() {
            return value;
        }

        void setValue(T value) {
            this.value = value;
        }
    }

    // A class can declare as many type parameters as it needs
    static class Pair<T, U> {
        private final T first;
        private final U second;

        Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }

        T getFirst() {
            return first;
        }

        U getSecond() {
            return second;
        }
    }
}
