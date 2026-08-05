public class LogicalOperators {
    public static void main(String[] args) {
        int a = 25;
        int b = 10;
        int c = 15;

        System.out.println((a < b) && (b < c));   // false
        System.out.println((a > b) || (b > c));   // true
        System.out.println(!(a > b));             // false

        // && and || short circuit: the right side is never evaluated
        // when the result is already decided, so isPositive() is not called.
        System.out.println((a < b) && isPositive(a));

        // & and | on booleans do not short circuit
        System.out.println((a < b) & isPositive(a));
    }

    static boolean isPositive(int value) {
        System.out.println("isPositive was called");
        return value > 0;
    }
}
