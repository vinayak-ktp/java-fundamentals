public class StringMethods {
    public static void main(String[] args) {
        lengthAndEmptiness();
        characterAccess();
        comparison();
        searching();
        extraction();
        splittingAndJoining();
        conversionAndFormatting();
    }

    static void lengthAndEmptiness() {
        String s = "Aditya";
        System.out.println(s.length());     // 6
        System.out.println(s.isEmpty());    // false, length == 0
        System.out.println("   ".isBlank()); // true, only whitespace
    }

    static void characterAccess() {
        String s = "Aditya";
        System.out.println(s.charAt(2));    // i
        System.out.println(s.toCharArray().length);
    }

    static void comparison() {
        String s = "aditya";

        System.out.println(s.equals("Aditya"));             // false, compares content
        System.out.println(s.equalsIgnoreCase("Aditya"));    // true

        // compareTo is lexicographic: negative, zero or positive
        System.out.println(s.compareTo("abc"));
    }

    static void searching() {
        String s = "Aditya Adi";
        System.out.println(s.contains("ity"));      // true
        System.out.println(s.indexOf("Adi"));       // 0, first hit
        System.out.println(s.lastIndexOf("Adi"));   // 7, last hit
        System.out.println(s.startsWith("Ad"));     // true
        System.out.println(s.endsWith("Adi"));      // true
    }

    static void extraction() {
        String s = "  Aditya  ";

        // substring's end index is exclusive
        System.out.println("Aditya".substring(1));
        System.out.println("Aditya".substring(1, 4));   // dit

        System.out.println(s.trim());        // drops ASCII whitespace
        System.out.println(s.strip());       // unicode aware version
        System.out.println("ab".repeat(3));  // ababab
        System.out.println("Aditya".toUpperCase());
        System.out.println("Aditya".replace("ity", "abc"));
        System.out.println("a1b2".replaceAll("[0-9]", ""));   // takes a regex
    }

    static void splittingAndJoining() {
        String[] parts = "Aditya-Rohit-Rohan".split("-");
        for (String part : parts) {
            System.out.print(part + " ");
        }
        System.out.println();

        System.out.println(String.join("-", "a", "b", "c"));   // a-b-c
    }

    static void conversionAndFormatting() {
        System.out.println(String.valueOf(10));

        for (byte b : "abc".getBytes()) {
            System.out.print(b + " ");
        }
        System.out.println();

        String name = "Aditya";
        int age = 28;
        System.out.println(String.format("Hello %s, your age is %d", name, age));
    }
}
