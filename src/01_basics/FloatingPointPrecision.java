public class FloatingPointPrecision {
    public static void main(String[] args) {
        float f = 0.7f;
        double d = 0.7;

        // Binary floating point cannot represent 0.7 exactly; the error shows
        // up once you print more digits than the type can actually hold.
        System.out.printf("float  : %.20f%n", f);
        System.out.printf("double : %.20f%n", d);

        System.out.println(0.1 + 0.2);   // 0.30000000000000004
    }
}
