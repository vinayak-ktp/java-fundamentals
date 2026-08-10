/*
 * What cannot be overridden:
 *   static  - belongs to the class, a child only hides it
 *   private - not visible to the child at all
 *   final   - explicitly closed for overriding (a final class cannot be extended)
 *
 * Fields are never polymorphic: they are resolved by the reference type at
 * compile time, which is why behaviour belongs in methods.
 */
public class OverridingRules {
    public static void main(String[] args) {
        Parent p = new Child();

        System.out.println(p.value);      // 10, taken from Parent - fields do not override
        System.out.println(p.getValue()); // 20, the method call is dispatched at runtime

        p.sealed();
        Parent.shared();   // static methods are best called on the class
    }

    static class Parent {
        int value = 10;

        int getValue() {
            return 10;
        }

        final void sealed() {
            System.out.println("Cannot be overridden");
        }

        static void shared() {
            System.out.println("Parent static");
        }

        private void hidden() {
            System.out.println("Invisible to the child");
        }
    }

    static class Child extends Parent {
        int value = 20;

        @Override
        int getValue() {
            return 20;
        }

        // Not an override, this hides Parent.shared()
        static void shared() {
            System.out.println("Child static");
        }
    }
}
