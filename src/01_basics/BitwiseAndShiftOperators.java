public class BitwiseAndShiftOperators {
    public static void main(String[] args) {
        bitwise();
        shifts();
    }

    static void bitwise() {
        int a = 2;   // ...00000010
        int b = 3;   // ...00000011

        System.out.println(a & b);   // 2
        System.out.println(a | b);   // 3
        System.out.println(a ^ b);   // 1
        System.out.println(~a);      // -3, every bit flipped (two's complement)
    }

    static void shifts() {
        // For an int the shift distance is taken modulo 32,
        // so << 33 behaves exactly like << 1.
        int a = 1;
        System.out.println(a << 33);   // 2

        // Byte and short operands are promoted to int before shifting,
        // hence the cast back.
        byte b = 1;
        System.out.println((byte) (b << 1));   // 2

        int negative = -8;
        System.out.println(negative >> 1);    // -4, sign bit is preserved
        System.out.println(negative >>> 1);   // huge positive, zero-filled
    }
}
