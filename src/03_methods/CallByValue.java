public class CallByValue {
    public static void main(String[] args) {
        primitivesAreCopied();
        referencesAreCopiedToo();
    }

    static void primitivesAreCopied() {
        int x = 4;
        int y = 5;

        addTen(x, y);
        System.out.println(x + " , " + y);   // still 4 , 5
    }

    static void addTen(int x, int y) {
        x = x + 10;
        y = y + 10;
    }

    /*
     * Java is always call by value. For an object the *reference* is copied,
     * so the callee can mutate the same object but cannot make the caller's
     * variable point somewhere else.
     */
    static void referencesAreCopiedToo() {
        Point p = new Point(4, 5);

        addTen(p);
        System.out.println(p.x + " , " + p.y);   // 14 , 15

        reassign(p);
        System.out.println(p.x + " , " + p.y);   // unchanged, still 14 , 15
    }

    static void addTen(Point p) {
        p.x += 10;
        p.y += 10;
    }

    static void reassign(Point p) {
        p = new Point(0, 0);
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // copy constructor
        Point(Point other) {
            this(other.x, other.y);
        }
    }
}
