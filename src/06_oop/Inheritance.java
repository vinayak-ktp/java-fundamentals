/*
 * Java supports single, multi-level and hierarchical inheritance between
 * classes. Multiple inheritance of classes is not allowed - only interfaces
 * can give a type more than one supertype.
 *
 *   multi-level                hierarchical
 *   Student                        Student
 *      |                          /       \
 *   EngineeringStudent    Engineering   Medical
 *      |
 *   CseStudent
 */
public class Inheritance {
    public static void main(String[] args) {
        CseStudent cse = new CseStudent();
        cse.markAttendance();   // inherited from Student
        cse.attendLab();        // inherited from EngineeringStudent
        cse.writeCode();

        MedicalStudent medical = new MedicalStudent();
        medical.attendLab();

        Student student = new Student();
        student.markAttendance();
    }

    static class Student {
        String name;
        int age;

        void markAttendance() {
            System.out.println("Attendance marked");
        }
    }

    static class EngineeringStudent extends Student {
        void attendLab() {
            System.out.println("Engineering lab attended");
        }
    }

    static class MedicalStudent extends Student {
        void attendLab() {
            System.out.println("Medical lab attended");
        }
    }

    static class CseStudent extends EngineeringStudent {
        void writeCode() {
            System.out.println("Writing code");
        }
    }
}
