/*
 * An uncaught exception unwinds the whole call stack and kills the thread,
 * so "Step 2" below would never print without the try block.
 *
 * finally always runs - normal exit, caught exception or rethrow - which
 * makes it the place for cleanup such as closing resources.
 */
public class TryCatchFinally {
    public static void main(String[] args) {
        System.out.println("Step 1");

        try {
            int a = 5;
            int b = 0;
            System.out.println(a / b);   // throws ArithmeticException
        } catch (ArithmeticException e) {
            System.out.println("Caught: " + e.getMessage());
        } finally {
            System.out.println("finally always runs");
        }

        System.out.println("Step 2");

        System.out.println(finallyWinsOverReturn());
    }

    // finally runs after the return value is computed, and can even replace it
    static int finallyWinsOverReturn() {
        try {
            return 1;
        } finally {
            System.out.println("cleanup before returning");
        }
    }
}
