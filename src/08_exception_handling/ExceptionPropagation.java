/*
 * An exception travels up the call stack until some method catches it:
 *
 *   main -> methodA -> methodB   throws here
 *   main <- methodA <- methodB   caught in methodB, so main continues
 *
 * If nothing catches it, the default handler prints the stack trace and the
 * thread dies.
 */
public class ExceptionPropagation {
    public static void main(String[] args) {
        System.out.println("Step 1");
        methodA(5, 0);
        System.out.println("Step 3");
    }

    static void methodA(int a, int b) {
        methodB(a, b);
    }

    static void methodB(int a, int b) {
        try {
            System.out.println(a / b);
        } catch (ArithmeticException e) {
            System.out.println("Divide by zero is not allowed");
        }
        System.out.println("Step 2");
    }
}
