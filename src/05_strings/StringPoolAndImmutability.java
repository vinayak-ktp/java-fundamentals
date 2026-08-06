public class StringPoolAndImmutability {
    public static void main(String[] args) {
        literalsAreShared();
        compileTimeVersusRuntime();
        immutability();
    }

    static void literalsAreShared() {
        String s1 = "Hello";
        String s2 = "Hello";
        System.out.println(s1 == s2);   // true, both point into the string pool

        String s3 = new String("Aditya");
        String s4 = new String("Aditya");
        System.out.println(s3 == s4);   // false, new always allocates on the heap

        // intern() returns the pooled instance
        System.out.println(s3.intern() == "Aditya");   // true
    }

    static void compileTimeVersusRuntime() {
        // Concatenation of literals is folded at compile time, so it lands in the pool
        String s1 = "Ja" + "va";
        System.out.println(s1 == "Java");   // true

        // Concatenation involving a variable happens at runtime -> new heap object
        String hello = "Hello";
        String joined = hello + " World";
        System.out.println(joined == "Hello World");   // false
    }

    /*
     * Strings never change, every "modification" builds a new object.
     * This loop creates five throwaway strings, which is why StringBuilder
     * exists for loops like this one.
     */
    static void immutability() {
        String s = "";
        for (int i = 0; i < 5; i++) {
            s += i;
            System.out.println(s);
        }
    }
}
