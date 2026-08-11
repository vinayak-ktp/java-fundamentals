/*
 * Run with:
 *   java CommandLineArguments input.txt output.txt
 *
 * args is never null - with no arguments it is simply an empty array,
 * and every element arrives as a String.
 */
public class CommandLineArguments {
    public static void main(String[] args) {
        System.out.println("Number of arguments: " + args.length);

        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument " + i + " = " + args[i]);
        }
    }
}
