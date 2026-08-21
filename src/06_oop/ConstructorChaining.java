public class ConstructorChaining {
    public static void main(String[] args) {
        // Prints the fifth constructor first: this(...) runs before the body
        Student s1 = new Student();
        s1.print();
    }

    static class Student {
        String name;
        int age;
        int rollNumber;
        String college;

        // this(...) must be the FIRST statement — a line above it gives:
        //   error: call to this must be first statement in constructor
        Student() {
            this("Unknown");
            System.out.println("first constructor");
        }

        Student(String name) {
            this(name, 0);
            System.out.println("second constructor");
        }

        Student(String name, int age) {
            this(name, age, 0);
            System.out.println("third constructor");
        }

        Student(String name, int age, int rollNumber) {
            this(name, age, rollNumber, "Unknown");
            System.out.println("fourth constructor");
        }

        // this(...) must be the first statement, so only one chain is possible
        Student(String name, int age, int rollNumber, String college) {
            this.name = name;
            this.age = age;
            this.rollNumber = rollNumber;
            this.college = college;
            System.out.println("fifth constructor");
        }

        void print() {
            System.out.println(name + " , " + age + " , " + rollNumber + " , " + college);
        }
    }
}
