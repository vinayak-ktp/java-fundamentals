public class Loops {
    public static void main(String[] args) {
        forLoop();
        commaSeparatedFor();
        whileLoop();
        doWhileLoop();
        nestedLoops();
    }

    /*
     * Flow of a for loop:
     *   1. the init statement runs once
     *   2. the condition is evaluated
     *   3. if true the body runs
     *   4. the update statement runs
     *   5. back to step 2
     */
    static void forLoop() {
        for (int i = 10; i >= 1; i--) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    // Both the init and the update section accept several comma separated statements
    static void commaSeparatedFor() {
        for (int i = 1, j = 1; i <= 10 && j <= 5; i++, j += 2) {
            System.out.print((i * j) + " ");
        }
        System.out.println();
    }

    static void whileLoop() {
        int i = 1;
        while (i <= 5) {
            System.out.print(i + " ");
            i++;
        }
        System.out.println();
    }

    // do-while always runs the body at least once, which suits menu driven input
    static void doWhileLoop() {
        int i = 1;
        do {
            System.out.print(i + " ");
            i++;
        } while (i <= 5);
        System.out.println();
    }

    static void nestedLoops() {
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }
}
