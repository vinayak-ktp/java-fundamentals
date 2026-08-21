/*
 * final means "assign exactly once":
 *   variable - cannot be reassigned
 *   method   - cannot be overridden
 *   class    - cannot be extended
 *
 * A final reference still allows the object itself to change; see
 * 06_oop/ImmutableClass.java for the consequences.
 */
public class FinalKeyword {
    public static void main(String[] args) {
        // A blank final local: declared now, assigned once later.
        // A second assignment gives:
        //   error: cannot assign a value to final variable x
        final int x;
        x = 4;
        System.out.println(x);

        System.out.println(MathConstant.PI);

        new Sealed().cannotOverride();
    }

    static class MathConstant {
        // A blank static final field must be assigned in a static block
        static final double PI;

        static {
            PI = 3.14;
        }
    }

    // A final class cannot be subclassed - String is the best known example:
    //   class Sub extends Sealed { }
    //   error: cannot inherit from final Sealed
    static final class Sealed {
        final void cannotOverride() {
            System.out.println("final method");
        }
    }
}
