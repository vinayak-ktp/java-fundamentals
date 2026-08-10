public class ThisAndSuper {
    public static void main(String[] args) {
        EngineeringStudent es = new EngineeringStudent("Aditya", 28, 101, "IIT Guwahati");
        es.print();
    }

    static class Student {
        String name;
        int age;
        int rollNo;

        Student(String name, int age, int rollNo) {
            // this disambiguates the field from the parameter of the same name
            this.name = name;
            this.age = age;
            this.rollNo = rollNo;
        }

        void print() {
            System.out.println(name + " , " + age + " , " + rollNo);
        }
    }

    static class EngineeringStudent extends Student {
        String college;

        EngineeringStudent(String name, int age, int rollNo, String college) {
            // super(...) must come first; without it the compiler inserts super()
            super(name, age, rollNo);
            this.college = college;
        }

        @Override
        void print() {
            super.print();   // reach the overridden version
            System.out.println(college);
        }
    }
}
