import java.util.function.Predicate;

/*
 * Predicates compose with the boolean operators they mirror:
 *   and()    &&
 *   or()     ||
 *   negate() !
 *
 * Naming each condition once and combining them keeps business rules readable.
 */
public class PredicateComposition {
    public static void main(String[] args) {
        numbers();
        businessRule();
    }

    static void numbers() {
        Predicate<Integer> isLarge = x -> x > 100;
        Predicate<Integer> isEven = x -> x % 2 == 0;
        Predicate<Integer> isOdd = isEven.negate();

        System.out.println(isLarge.and(isEven).test(200));   // true
        System.out.println(isLarge.or(isEven).test(56));     // true
        System.out.println(isOdd.test(55));                  // true
    }

    static void businessRule() {
        Predicate<Student> passed = s -> s.marks >= 40;
        Predicate<Student> isAdult = s -> s.age >= 18;

        Predicate<Student> isEligible = passed.and(isAdult);

        System.out.println(isEligible.test(new Student(50, 17)));   // false
        System.out.println(isEligible.test(new Student(50, 19)));   // true
    }

    static class Student {
        int marks;
        int age;

        Student(int marks, int age) {
            this.marks = marks;
            this.age = age;
        }
    }
}
