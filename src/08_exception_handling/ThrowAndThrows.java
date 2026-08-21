import java.io.FileNotFoundException;
import java.io.FileReader;

/*
 * throw  - raises an exception object right here
 * throws - declares that a method may let one escape
 *
 * Checked exceptions (Exception minus RuntimeException) must be either caught
 * or declared. Unchecked ones (RuntimeException, Error) need neither.
 */
public class ThrowAndThrows {
    public static void main(String[] args) {
        try {
            readFile();
        } catch (FileNotFoundException e) {
            System.out.println("Checked exception: " + e.getMessage());
        }

        try {
            validate(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("Unchecked exception: " + e.getMessage());
        }
    }

    // FileNotFoundException is checked, so the declaration is mandatory.
    // Dropping the throws clause gives:
    //   error: unreported exception FileNotFoundException;
    //          must be caught or declared to be thrown
    static void readFile() throws FileNotFoundException {
        new FileReader("abc.txt");
    }

    // IllegalArgumentException is unchecked, no throws clause required
    static void validate(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }
}
