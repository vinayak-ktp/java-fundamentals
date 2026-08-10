/*
 * Run from this directory:
 *   javac PackagesDemo.java college/*.java school/*.java
 *   java PackagesDemo
 *
 * Two classes share the simple name Student, so neither can be imported
 * without the other becoming ambiguous. Fully qualified names are the way out.
 */
public class PackagesDemo {
    public static void main(String[] args) {
        college.Student collegeStudent = new college.Student();
        collegeStudent.print();

        school.Student schoolStudent = new school.Student();
        schoolStudent.print();

        new college.Teacher().teach();
    }
}
