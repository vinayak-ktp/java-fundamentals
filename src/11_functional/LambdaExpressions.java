/*
 * A lambda is an implementation of a functional interface - an interface with
 * exactly one abstract method. @FunctionalInterface makes the compiler enforce
 * that, so adding a second abstract method fails loudly instead of quietly
 * breaking every lambda.
 */
public class LambdaExpressions {
    public static void main(String[] args) {
        // A lambda has no type of its own - it takes the type of what it is
        // assigned to, so there must be something to infer from:
        //   var add = (a, b) -> a + b;
        //   error: cannot infer type for local variable add
        Calculator add = (a, b) -> a + b;
        Calculator multiply = (a, b) -> a * b;

        System.out.println(apply(5, 4, add));
        System.out.println(apply(5, 4, multiply));

        // A body with braces needs an explicit return
        Calculator max = (a, b) -> {
            if (a > b) {
                return a;
            }
            return b;
        };
        System.out.println(apply(5, 4, max));

        // The verbose equivalent of the first lambda
        System.out.println(apply(5, 4, new Addition()));
    }

    static int apply(int a, int b, Calculator calculator) {
        return calculator.calculate(a, b);
    }

    // Adding a second abstract method here is rejected at the interface itself,
    // rather than at every lambda that used it:
    //   error: Unexpected @FunctionalInterface annotation
    @FunctionalInterface
    interface Calculator {
        int calculate(int a, int b);
    }

    static class Addition implements Calculator {
        @Override
        public int calculate(int a, int b) {
            return a + b;
        }
    }
}
