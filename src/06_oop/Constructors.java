public class Constructors {
    public static void main(String[] args) {
        Student s1 = new Student("Rohit", 28, 102, "IIT Guwahati");
        s1.print();

        // Works only because the no-arg constructor was written explicitly:
        // declaring any constructor removes the compiler's default one.
        Student s2 = new Student();
        s2.print();
    }

    static class Student {
        String name;
        int age;
        int rollNumber;
        String college;

        Student() {
        }

        Student(String name, int age, int rollNumber, String college) {
            this.name = name;
            this.age = age;
            this.rollNumber = rollNumber;
            this.college = college;
        }

        void print() {
            System.out.println(name + " , " + age + " , " + rollNumber + " , " + college);
        }
    }
}
