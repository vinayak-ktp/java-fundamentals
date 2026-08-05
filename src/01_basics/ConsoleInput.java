import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * How a typed line reaches the program:
 *   1. the OS buffers the keystrokes
 *   2. System.in, an InputStream, delivers raw bytes
 *   3. InputStreamReader decodes those bytes into characters
 *   4. BufferedReader assembles characters into lines
 *
 * System.in.read() returns a single byte as an int, which is why it needs the
 * cast to char.
 */
public class ConsoleInput {
    public static void main(String[] args) throws IOException {
        int first = System.in.read();
        System.out.println("First byte as a character: " + (char) first);

        // try-with-resources closes the reader automatically
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter your name: ");
            String name = reader.readLine();

            System.out.print("Enter your age: ");
            // readLine always gives a String, so numbers must be parsed
            int age = Integer.parseInt(reader.readLine().trim());

            System.out.println(name + " is " + age + " years old");
        }
    }
}
