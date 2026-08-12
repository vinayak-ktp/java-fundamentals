/*
 * A method can be generic on its own, whatever the class does. The type
 * parameters go before the return type, and the compiler infers them from
 * the arguments, so the call site stays clean.
 */
public class GenericMethods {
    public static void main(String[] args) {
        Integer number = identity(23);
        String text = identity("Aditya");
        System.out.println(number + " , " + text);

        printPair(11, "Aditya");

        Integer[] numbers = {1, 2, 3};
        printAll(numbers);
    }

    static <T> T identity(T value) {
        return value;
    }

    static <T, U> void printPair(T first, U second) {
        System.out.println(first + " , " + second);
    }

    static <T> void printAll(T[] values) {
        for (T value : values) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
