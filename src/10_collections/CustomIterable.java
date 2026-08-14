import java.util.Iterator;

/*
 * Implementing Iterable is all it takes for a custom type to work with the
 * enhanced for loop. The iterator itself is usually an anonymous class holding
 * the cursor.
 */
public class CustomIterable {
    public static void main(String[] args) {
        NameContainer container = new NameContainer(new String[]{"Aditya", "Rohit", "Rohan"});

        for (String name : container) {
            System.out.println(name);
        }
    }

    static class NameContainer implements Iterable<String> {
        private final String[] names;

        NameContainer(String[] names) {
            this.names = names;
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                private int position = 0;

                @Override
                public boolean hasNext() {
                    return position < names.length;
                }

                @Override
                public String next() {
                    return names[position++];
                }
            };
        }
    }
}
