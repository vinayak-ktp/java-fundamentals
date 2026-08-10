import java.util.Objects;

/*
 * Every class extends Object implicitly, which is where toString, equals,
 * hashCode, getClass and clone come from.
 *
 * Contract: equal objects must have equal hash codes, otherwise hash based
 * collections (HashSet, HashMap) will not find them.
 */
public class ObjectClassMethods {
    public static void main(String[] args) throws CloneNotSupportedException {
        Student s1 = new Student("Aditya", 28);
        Student s2 = new Student("Aditya", 28);

        System.out.println(s1);                                // uses toString
        System.out.println(s1.equals(s2));                     // true
        System.out.println(s1.hashCode() == s2.hashCode());    // true
        System.out.println(s1.getClass().getName());

        // A shallow copy: primitives and references are copied field by field
        Student s3 = (Student) s1.clone();
        System.out.println(s3);

        instanceOfCheck();
    }

    // instanceof is true for the exact class and any subclass
    static void instanceOfCheck() {
        Animal animal = new Animal();
        Animal dog = new Dog();

        System.out.println(animal instanceof Dog);      // false
        System.out.println(dog instanceof Animal);      // true
        System.out.println(dog instanceof Object);      // true
    }

    static class Student implements Cloneable {
        String name;
        int age;

        Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name + " , " + age;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            Student other = (Student) obj;
            // Compare content with equals, never with == on references
            return this.age == other.age && Objects.equals(this.name, other.name);
        }

        @Override
        public int hashCode() {
            // Hand written version of the same idea:
            //   int result = 17;
            //   result = result * 31 + age;
            //   result = result * 31 + (name == null ? 0 : name.hashCode());
            return Objects.hash(name, age);
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    static class Animal {
    }

    static class Dog extends Animal {
    }
}
