import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/*
 * Comparable defines a type's natural order, used by Collections.sort,
 * TreeSet and TreeMap.
 *
 *   compareTo < 0   this comes first
 *   compareTo == 0  the two are equal *for ordering purposes*
 *   compareTo > 0   the other comes first
 */
public class ComparableConcept {
    public static void main(String[] args) {
        sorting();
        equalityInSortedSets();
    }

    static void sorting() {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Aditya", 95));
        students.add(new Student("Rohit", 85));
        students.add(new Student("Rohan", 56));
        students.add(new Student("Monu", 85));

        Collections.sort(students);

        for (Student s : students) {
            System.out.println(s.name + " , " + s.marks);
        }
    }

    /*
     * A TreeSet decides duplicates with compareTo, not equals. Two students
     * with the same marks and name collapse into one entry even though equals
     * was never overridden.
     */
    static void equalityInSortedSets() {
        TreeSet<Student> set = new TreeSet<>();
        set.add(new Student("Aditya", 95));
        set.add(new Student("Aditya", 95));

        System.out.println(set.size());   // 1
    }

    static class Student implements Comparable<Student> {
        String name;
        int marks;

        Student(String name, int marks) {
            this.name = name;
            this.marks = marks;
        }

        @Override
        public int compareTo(Student other) {
            if (this.marks != other.marks) {
                return Integer.compare(this.marks, other.marks);
            }
            // Tie breaker keeps the order total and predictable
            return this.name.compareTo(other.name);
        }
    }
}
