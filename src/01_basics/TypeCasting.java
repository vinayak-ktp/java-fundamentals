public class TypeCasting {
    public static void main(String[] args) {
        implicitWidening();
        explicitNarrowing();
        truncation();
        compoundAssignmentQuirk();
    }

    // Smaller type fits into a larger one, so no cast is needed
    static void implicitWidening() {
        byte b = 24;
        int fromByte = b;

        char c = 'a';
        int fromChar = c;   // 97

        System.out.println(fromByte + " , " + fromChar);
    }

    // Larger to smaller needs an explicit cast and can overflow
    static void explicitNarrowing() {
        int i = 300;
        byte b = (byte) i;  // 300 wraps around the 8-bit range -> 44

        System.out.println(b);
    }

    // Floating point to integer drops the fractional part, it does not round
    static void truncation() {
        float f = 15.678f;
        int i = (int) f;    // 15

        System.out.println(i);
    }

    // boolean has no conversion to or from any other type.

    static void compoundAssignmentQuirk() {
        byte b = 50;
        // b * 2 is promoted to int, so the plain assignment needs a cast back...
        b = (byte) (b * 2);
        System.out.println(b);

        byte c = 50;
        c *= 2;   // ...but compound operators cast implicitly
        System.out.println(c);
    }
}
