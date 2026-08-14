import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * Comparable gives a type one order. A Comparator is an order supplied from
 * outside, so the same objects can be sorted many different ways - and it can
 * be written as a class, an anonymous class or a lambda.
 */
public class ComparatorConcept {
    public static void main(String[] args) {
        List<Student> students = students();

        students.sort(new SortByMarks());
        print("by marks (named class)", students);

        students.sort(new Comparator<Student>() {
            @Override
            public int compare(Student a, Student b) {
                return a.name.compareTo(b.name);
            }
        });
        print("by name (anonymous class)", students);

        students.sort((a, b) -> Integer.compare(a.rollNo, b.rollNo));
        print("by roll number (lambda)", students);

        // Built in factories compose and read better than hand written compares
        students.sort(Comparator.comparingInt((Student s) -> s.marks)
                .reversed()
                .thenComparing(s -> s.name));
        print("by marks desc, then name", students);
    }

    static List<Student> students() {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Aditya", 101, 85));
        students.add(new Student("Rohit", 102, 89));
        students.add(new Student("Rohan", 103, 93));
        students.add(new Student("Sonu", 104, 89));
        return students;
    }

    static void print(String label, List<Student> students) {
        System.out.println("-- " + label);
        for (Student s : students) {
            System.out.println(s.name + " , " + s.rollNo + " , " + s.marks);
        }
    }

    static class SortByMarks implements Comparator<Student> {
        @Override
        public int compare(Student a, Student b) {
            return Integer.compare(a.marks, b.marks);
        }
    }

    static class Student {
        String name;
        int rollNo;
        int marks;

        Student(String name, int rollNo, int marks) {
            this.name = name;
            this.rollNo = rollNo;
            this.marks = marks;
        }
    }
}
