/*
 * static members belong to the class, not to any object, so all instances
 * share one copy. That is also why main is static: the JVM calls it without
 * creating an object first.
 */
public class StaticKeyword {
    public static void main(String[] args) {
        Student s1 = new Student("Aditya", 28, 101);
        Student s2 = new Student("Rohit", 28, 102);

        // Changing it through the class changes it for every instance
        Student.college = "IIT Bombay";

        s1.print();
        s2.print();

        System.out.println("Grade set by the static block: " + Student.grade);
        System.out.println("Objects created: " + Student.count);
    }

    static class Student {
        String name;
        int age;
        int rollNumber;

        static String college = "IIT Guwahati";
        static int grade;
        static int count;

        // Runs once when the class is loaded, before any object exists
        static {
            grade = 8;
        }

        Student(String name, int age, int rollNumber) {
            this.name = name;
            this.age = age;
            this.rollNumber = rollNumber;
            count++;
        }

        void print() {
            System.out.println(name + " , " + age + " , " + rollNumber + " , " + college);
        }
    }
}
