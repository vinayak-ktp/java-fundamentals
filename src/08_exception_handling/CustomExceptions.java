/*
 * Extend Exception for a checked exception the caller must handle, or
 * RuntimeException when the failure means the caller has a bug.
 * Carrying the offending value as a field makes the exception far more useful
 * than a message alone.
 */
public class CustomExceptions {
    public static void main(String[] args) {
        try {
            checkEligibility(-5);
        } catch (InvalidAgeException e) {
            System.out.println(e.getMessage());
            System.out.println("Entered age was " + e.getAge());
        }
    }

    static void checkEligibility(int age) throws InvalidAgeException {
        if (age <= 0) {
            throw new InvalidAgeException("Age cannot be negative", age);
        }

        if (age >= 18) {
            System.out.println("You are eligible to vote");
        }
    }

    static class InvalidAgeException extends Exception {
        private final int age;

        InvalidAgeException(String message, int age) {
            super(message);   // hands the message to Throwable
            this.age = age;
        }

        int getAge() {
            return age;
        }
    }
}
