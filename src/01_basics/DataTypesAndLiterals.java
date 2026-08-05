public class DataTypesAndLiterals {
    public static void main(String[] args) {
        // Integer literals can be written in binary, octal, decimal or hex
        byte binary = 0b101;      // 5
        byte octal = 07;          // leading 0 means octal
        byte hex = 0XA;           // 10
        short small = 10;
        int number = 4000;

        // Underscores are allowed anywhere between digits for readability
        long big = 3412__56_789L;

        float single = 10.5_4f;              // single precision, needs the f suffix
        double avogadro = 6.02____2e23;      // double precision, scientific notation

        char letter = 'a';                   // stored as its numeric code point
        boolean flag = false;

        System.out.println("Integers -> " + binary + " , " + octal + " , " + hex
                + " , " + small + " , " + number + " , " + big);
        System.out.println("Floating point -> " + single + " , " + avogadro);
        System.out.println("Character -> " + letter);
        System.out.println("Boolean -> " + flag);
    }
}
