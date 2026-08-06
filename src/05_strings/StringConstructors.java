public class StringConstructors {
    public static void main(String[] args) {
        String empty = new String();
        String fromLiteral = new String("Hello");
        String fromString = new String(fromLiteral);

        char[] letters = {'A', 'd', 'i', 't', 'y', 'a', ' ', 'T', 'a', 'n', 'd', 'o', 'n'};
        String fromChars = new String(letters);

        // offset and count, not from-index and to-index
        String fromCharsSubset = new String(letters, 0, 6);   // Aditya

        byte[] bytes = {97, 98, 99};
        String fromBytes = new String(bytes, 0, 2);           // ab

        String fromBuilder = new String(new StringBuffer("Hello"));

        System.out.println(empty.isEmpty());
        System.out.println(fromLiteral + " , " + fromString);
        System.out.println(fromChars + " , " + fromCharsSubset);
        System.out.println(fromBytes + " , " + fromBuilder);

        // The char array is copied, so mutating it afterwards does not touch the String
        letters[0] = 'B';
        System.out.println(fromChars);
    }
}
