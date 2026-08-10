/*
 * Static nested class: no link to an outer instance, so it can hold static
 * members. Typical uses are helper classes, builders and request/response DTOs.
 *
 * Inner (non static) class: tied to an outer instance and created through it,
 * which is why it can read the outer instance fields.
 */
public class NestedClasses {
    public static void main(String[] args) {
        StaticOuter staticOuter = new StaticOuter();
        StaticOuter.Helper helper = new StaticOuter.Helper(staticOuter);
        helper.print();

        System.out.println(new BankAccount().computeInterest(1000));

        InnerOuter outer = new InnerOuter();
        InnerOuter.Inner inner = outer.new Inner();   // needs an outer instance
        inner.print();
    }

    static class StaticOuter {
        private static int shared = 4;
        int instanceValue = 7;

        static class Helper {
            private final StaticOuter outer;

            Helper(StaticOuter outer) {
                this.outer = outer;
            }

            void print() {
                System.out.println(shared);              // static members: direct access
                System.out.println(outer.instanceValue); // instance members: via a reference
            }
        }
    }

    // A private static nested class hides an implementation detail completely
    static class BankAccount {
        private static class InterestCalculator {
            static double calculateYearly(double principal, double rate) {
                return principal * rate;
            }
        }

        double computeInterest(double principal) {
            return InterestCalculator.calculateYearly(principal, 0.09);
        }
    }

    static class InnerOuter {
        int x = 10;

        class Inner {
            int x = 20;

            void print() {
                System.out.println(x);                  // the inner field
                System.out.println(this.x);             // same thing, explicitly
                System.out.println(InnerOuter.this.x);  // the outer field
            }
        }
    }
}
