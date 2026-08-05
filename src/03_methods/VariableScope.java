public class VariableScope {

    static String name = "Aditya";   // class level, visible to every method here

    public static void main(String[] args) {
        int x = 4;

        if (x == 4) {
            int insideBlock = 7;   // dies at the closing brace
            System.out.println(insideBlock);
        }

        System.out.println(x + " , " + name);

        printLocals();
    }

    // These locals are unrelated to main's, they just share a name
    static void printLocals() {
        int x = 40;
        System.out.println(x + " , " + name);
    }
}
