public class SwitchStatements {
    public static void main(String[] args) {
        classicSwitch(1);
        fallThrough(2);
        stringSwitch("pending");
    }

    /*
     * The switch expression must be byte, short, int, char, an enum,
     * a String (Java 7+) or their wrapper types:
     *   switch (someLong) { ... }
     *   error: selector type long is not allowed
     *
     * Case labels must be compile-time constants, and must be unique:
     *   case 1: ... case 1: ...
     *   error: duplicate case label
     */
    static void classicSwitch(int i) {
        switch (i) {
            case 1:
                System.out.println("i is 1");
                break;
            case 2:
                System.out.println("i is 2");
                break;
            case 3:
                System.out.println("i is 3");
                break;
            default:
                System.out.println("i is greater than 3");
        }
    }

    // A missing break falls through into the next case
    static void fallThrough(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
                System.out.println(i + " is small");
                break;
            default:
                System.out.println(i + " is large");
        }
    }

    static void stringSwitch(String status) {
        switch (status) {
            case "success":
                System.out.println("Payment done");
                break;
            case "pending":
                System.out.println("Payment in progress");
                break;
            default:
                System.out.println("Payment failed");
        }
    }
}
