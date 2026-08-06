public class StringBuilderAndBuffer {
    public static void main(String[] args) {
        mutation();
        capacity();
        bufferIsSynchronized();
    }

    // Unlike String, these objects are modified in place
    static void mutation() {
        StringBuilder sb = new StringBuilder("Aditya");

        sb.append(" Tandon");
        sb.insert(2, 'o');
        sb.delete(0, 2);
        sb.deleteCharAt(1);
        sb.replace(1, 3, "XY");
        sb.setCharAt(0, 'Z');
        sb.reverse();

        System.out.println(sb);
        System.out.println(sb.charAt(1));
    }

    /*
     * The default capacity is 16, or 16 + length when built from a string.
     * It grows as (old * 2) + 2 whenever the content overflows it.
     */
    static void capacity() {
        StringBuilder sb = new StringBuilder();
        sb.append("Aditya").append("Tandon").append("aaaaa");   // 17 chars

        System.out.println(sb.capacity());   // 34 after one growth from 16
        sb.trimToSize();
        System.out.println(sb.capacity());   // 17

        sb.ensureCapacity(100);
        System.out.println(sb.capacity());
    }

    // StringBuffer has the same API but every method is synchronized:
    // thread safe, and slower than StringBuilder in single threaded code.
    static void bufferIsSynchronized() {
        StringBuffer sb = new StringBuffer("Hello");
        sb.append(" World");
        System.out.println(sb);
    }
}
