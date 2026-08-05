public class IfElseConditionals {
    public static void main(String[] args) {
        simpleIf(7);
        ifElseIfLadder(7);
        independentIfs(50);
        nestedIf(8);
    }

    static void simpleIf(int i) {
        if (i > 5 && i < 10) {
            System.out.println(i + " is between 5 and 10");
        } else {
            System.out.println(i + " is outside 5..10");
        }
    }

    // Only the first matching branch runs
    static void ifElseIfLadder(int i) {
        if (i == 5) {
            System.out.println("i is 5");
        } else if (i == 6) {
            System.out.println("i is 6");
        } else if (i == 7) {
            System.out.println("i is 7");
        } else {
            System.out.println("i is something else");
        }
    }

    // Separate ifs all get evaluated, and the else belongs only to the last if
    static void independentIfs(int age) {
        if (age > 80) {
            System.out.println("You are very old");
        }
        if (age > 60) {
            System.out.println("You are old");
        }
        if (age > 40) {
            System.out.println("You are becoming old");
        }
        if (age > 20) {
            System.out.println("You are young");
        } else {
            System.out.println("You are a child");
        }
    }

    static void nestedIf(int i) {
        if (i > 5) {
            if (i < 10) {
                System.out.println(i + " is between 5 and 10");
            }
        }
    }
}
