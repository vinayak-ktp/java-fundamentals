public class ClassesAndObjects {
    public static void main(String[] args) {
        Student s1 = new Student();
        s1.name = "Aditya";
        s1.age = 28;
        s1.rollNumber = 101;
        s1.college = "IIT Guwahati";

        Student s2 = new Student();
        s2.name = "Rohit";
        s2.age = 28;
        s2.rollNumber = 102;
        s2.college = "IIT Guwahati";

        s1.markAttendance();
        s2.markAttendance();

        s1.print();
        s2.print();

        // Instance fields get type defaults, unlike locals which must be initialised
        Student fresh = new Student();
        fresh.print();   // null , 0 , 0 , null
    }

    static class Student {
        // state: instance variables
        String name;
        int age;
        int rollNumber;
        String college;

        // behaviour: instance methods
        void markAttendance() {
            System.out.println("Attendance marked by " + name);
        }

        void print() {
            System.out.println(name + " , " + age + " , " + rollNumber + " , " + college);
        }
    }
}
