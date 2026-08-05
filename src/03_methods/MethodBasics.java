public class MethodBasics {
    public static void main(String[] args) {
        greet();
        sayHello("Rohit");

        System.out.println(getNumber());
        System.out.println(multiply(2, 4));

        chainedCalls();
    }

    // no input, no output
    static void greet() {
        System.out.println("Hello");
    }

    // input, no output
    static void sayHello(String name) {
        System.out.println("Hello " + name);
    }

    // no input, returns a value
    static int getNumber() {
        return 10;
    }

    // input and output
    static int multiply(int a, int b) {
        return a * b;
    }

    // Each call is pushed on the stack, so the prints unwind in reverse order
    static void chainedCalls() {
        levelOne();
        System.out.println("back in main");
    }

    static void levelOne() {
        levelTwo();
        System.out.println("level one");
    }

    static void levelTwo() {
        System.out.println("level two");
    }
}
