public class ArithmeticAndRelationalOperators {
    public static void main(String[] args) {
        arithmetic();
        compoundAssignment();
        incrementAndDecrement();
        relational();
    }

    static void arithmetic() {
        int a = 5;
        int b = 10;

        System.out.println(a + b);   // 15
        System.out.println(a - b);   // -5
        System.out.println(a * b);   // 50
        System.out.println(b / a);   // 2  (integer division)
        System.out.println(b % a);   // 0
    }

    static void compoundAssignment() {
        int value = 7;

        value += 2;
        value -= 2;
        value *= 3;
        value /= 5;
        value %= 5;

        System.out.println(value);
    }

    static void incrementAndDecrement() {
        int i = 9;

        int post = i++;   // assigns first, then increments -> post = 9, i = 10
        int pre = ++i;    // increments first, then assigns -> pre = 11, i = 11

        System.out.println(i + " , " + post + " , " + pre);
    }

    static void relational() {
        int a = 10;
        int b = 10;

        System.out.println(a == b);   // true
        System.out.println(a != b);   // false
        System.out.println(a < b);    // false
        System.out.println(a <= b);   // true
    }
}
