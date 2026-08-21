public class MethodOverloading {
    public static void main(String[] args) {
        System.out.println(sum(2, 3));
        System.out.println(sum(3, 5, 6));
        System.out.println(sum(2.5, 3.5));

        greet("Aditya", 28);
        greet(28, "Rohit");
    }

    // Overloads must differ in the number, type or order of parameters.
    // The return type alone is not part of the signature, so these cannot coexist:
    //   static int fun() { ... }
    //   static void fun() { ... }
    //   error: method fun() is already defined in class MethodOverloading
    static int sum(int a, int b) {
        return a + b;
    }

    static int sum(int a, int b, int c) {
        return a + b + c;
    }

    static int sum(double a, double b) {
        return (int) (a + b);
    }

    static void greet(String name, int age) {
        System.out.println("Hi " + name + ", your age is " + age);
    }

    static void greet(int age, String name) {
        System.out.println("Hi " + name + ", your age is " + age);
    }
}
