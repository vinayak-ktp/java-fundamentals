/*
 * Before generics, a container held Object and every read needed a cast.
 * The compiler cannot check those casts, so a wrong one only blows up at
 * runtime with ClassCastException. Generics move that error to compile time.
 */
public class RawTypeProblem {
    public static void main(String[] args) {
        upcastingIsAlwaysSafe();
        downcastingIsNot();

        Box intBox = new Box(10);

        // Compiles happily, fails at runtime: the type information is gone
        try {
            String value = (String) intBox.getValue();
            System.out.println(value);
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: an Integer is not a String");
        }
    }

    static void upcastingIsAlwaysSafe() {
        String s = "Hello";
        Object obj = s;   // widening a reference, no cast needed
        System.out.println(obj);
    }

    static void downcastingIsNot() {
        Object obj = "Aditya";
        String s = (String) obj;   // fine, the object really is a String
        System.out.println(s);
    }

    static class Box {
        private Object value;

        Box(Object value) {
            this.value = value;
        }

        Object getValue() {
            return value;
        }

        void setValue(Object value) {
            this.value = value;
        }
    }
}
