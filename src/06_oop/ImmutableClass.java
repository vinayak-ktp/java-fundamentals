/*
 * Recipe for immutability:
 *   final class          so behaviour cannot be overridden
 *   private final fields set only in the constructor
 *   no setters
 *   defensive copies of every mutable field, on the way in *and* out
 *
 * Skipping the last step is the classic mistake: final only freezes the
 * reference, not the object it points at.
 */
public class ImmutableClass {
    public static void main(String[] args) {
        College college = new College("IIT Guwahati", "Assam");

        LeakyStudent leaky = new LeakyStudent(28, "Aditya", college);
        leaky.getCollege().name = "IIT Bombay";
        System.out.println(leaky.getCollege().name);   // IIT Bombay - state escaped

        SafeStudent safe = new SafeStudent(28, "Aditya", college);
        safe.getCollege().name = "IIT Bombay";
        System.out.println(safe.getCollege().name);    // IIT Guwahati - copy was mutated
    }

    static final class LeakyStudent {
        private final int age;
        private final String name;
        private final College college;

        LeakyStudent(int age, String name, College college) {
            this.age = age;
            this.name = name;
            this.college = college;   // stores the caller's object
        }

        int getAge() {
            return age;
        }

        String getName() {
            return name;
        }

        College getCollege() {
            return college;   // hands the internal object out
        }
    }

    static final class SafeStudent {
        private final int age;
        private final String name;
        private final College college;

        SafeStudent(int age, String name, College college) {
            this.age = age;
            this.name = name;
            this.college = new College(college.name, college.address);
        }

        int getAge() {
            return age;
        }

        String getName() {
            return name;
        }

        College getCollege() {
            return new College(college.name, college.address);
        }
    }

    // Mutable on purpose, to show why the copies matter
    static class College {
        String name;
        String address;

        College(String name, String address) {
            this.name = name;
            this.address = address;
        }
    }
}
