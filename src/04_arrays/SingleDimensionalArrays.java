public class SingleDimensionalArrays {
    public static void main(String[] args) {
        // Both declaration styles are legal; int[] name is preferred
        int[] rollNumbers = new int[3];

        int next = 101;
        for (int i = 0; i < rollNumbers.length; i++) {
            rollNumbers[i] = next++;
        }

        for (int rollNumber : rollNumbers) {
            System.out.println(rollNumber);
        }

        // Array literal, size is inferred
        int[] marks = {12, 14, 56};
        System.out.println("length = " + marks.length);

        // Elements are initialised to the type's default (0 here), unlike locals
        int[] empty = new int[2];
        System.out.println(empty[0]);
    }
}
