public class NestedTryAndMultiCatch {
    public static void main(String[] args) {
        innerHandlesIt();
        outerHandlesIt();
        multiCatch();
    }

    static void innerHandlesIt() {
        try {
            System.out.println("Outer try starts");
            try {
                System.out.println(5 / 0);
            } catch (ArithmeticException e) {
                System.out.println("Handled by the inner catch");
            }
            System.out.println("Outer try ends");
        } catch (ArithmeticException e) {
            System.out.println("Handled by the outer catch");
        }
    }

    // When the inner catch does not match, the exception propagates outward
    static void outerHandlesIt() {
        try {
            try {
                System.out.println(5 / 0);
            } catch (NullPointerException e) {
                System.out.println("Never reached");
            }
            System.out.println("Never reached either");
        } catch (ArithmeticException e) {
            System.out.println("Handled by the outer catch");
        }
    }

    /*
     * Catch blocks are checked top to bottom, so subclasses must come before
     * their superclass - a catch(Exception) first would make the rest dead code.
     */
    static void multiCatch() {
        try {
            String s = null;
            s.length();
        } catch (ArithmeticException | NullPointerException e) {
            System.out.println("Multi-catch: " + e.getClass().getSimpleName());
        } catch (RuntimeException e) {
            System.out.println("Some other runtime exception");
        } catch (Exception e) {
            System.out.println("Anything else");
        }
    }
}
