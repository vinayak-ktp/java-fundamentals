/*
 * Two interfaces can both supply a default method with the same signature:
 *
 *      A
 *     / \
 *    B   C      both override fun()
 *     \ /
 *      D        must override fun() itself, or it will not compile
 *
 * Resolution priority when a class and an interface both provide a method:
 *   1. the class's own method
 *   2. the superclass method
 *   3. the interface default method
 * So a concrete class always beats a default method.
 */
public class DiamondProblem {
    public static void main(String[] args) {
        new D().fun();               // D
        new ClassBeatsInterface().fun();   // the class implementation wins
    }

    interface A {
        void fun();
    }

    interface B extends A {
        @Override
        default void fun() {
            System.out.println("B");
        }
    }

    interface C extends A {
        @Override
        default void fun() {
            System.out.println("C");
        }
    }

    // Without the override below:
    //   error: types B and C are incompatible;
    //          class D inherits unrelated defaults for fun() from types B and C
    static class D implements B, C {
        @Override
        public void fun() {
            // B.super.fun() would delegate to a chosen parent instead
            System.out.println("D");
        }
    }

    interface WithDefault {
        default void fun() {
            System.out.println("Interface default");
        }
    }

    static class Base {
        public void fun() {
            System.out.println("Superclass method");
        }
    }

    static class ClassBeatsInterface extends Base implements WithDefault {
    }
}
