/*
 * Autoboxing wraps a primitive in its wrapper type, unboxing does the reverse.
 * It happens on assignments, method calls and arithmetic.
 */
public class WrapperClassesAndAutoboxing {
    public static void main(String[] args) {
        boxingAndUnboxing();
        cachedInstances();
        nullUnboxingTrap();
    }

    static void boxingAndUnboxing() {
        int primitive = 10;
        Integer boxed = primitive;    // autoboxing
        int back = boxed;             // unboxing

        Integer a = 10;
        Integer b = 20;
        int sum = a + b;              // both unboxed for the arithmetic

        System.out.println(back + " , " + sum);
        printInteger(50);             // boxed at the call site
    }

    static void printInteger(Integer value) {
        System.out.println(value);
    }

    /*
     * Integer caches -128..127, so == accidentally works in that range and
     * fails outside it. Always compare wrappers with equals.
     */
    static void cachedInstances() {
        Integer small1 = 100;
        Integer small2 = 100;
        System.out.println(small1 == small2);        // true, same cached object

        Integer large1 = 200;
        Integer large2 = 200;
        System.out.println(large1 == large2);        // false, two objects
        System.out.println(large1.equals(large2));   // true
    }

    // Unboxing null throws NullPointerException
    static void nullUnboxingTrap() {
        Integer value = null;
        try {
            int unboxed = value;
            System.out.println(unboxed);
        } catch (NullPointerException e) {
            System.out.println("Unboxing null throws NullPointerException");
        }
    }
}
