public class JumpStatements {
    public static void main(String[] args) {
        breakOnFirstDivisor(9);
        skipEvenNumbers();
        labelledBreak();
        labelledBlock();
    }

    static void breakOnFirstDivisor(int p) {
        int i;
        for (i = 2; i < p; i++) {
            if (p % i == 0) {
                System.out.println(p + " is not prime");
                break;
            }
        }

        // The loop variable survives the loop, so it tells us how we left it
        if (i == p) {
            System.out.println(p + " is prime");
        }
    }

    static void skipEvenNumbers() {
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continue;
            }
            System.out.print(i + " ");
        }
        System.out.println();
    }

    // A plain break only leaves the innermost loop; a label leaves the one it names
    static void labelledBreak() {
        outer:
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.print("* ");
                if (j >= 5) {
                    break outer;
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    // Labels also work on plain blocks, giving a structured "jump forward"
    static void labelledBlock() {
        first:
        {
            second:
            {
                System.out.println("Inside the innermost block");
                break first;
            }
        }
        System.out.println("After the block");
    }
}
