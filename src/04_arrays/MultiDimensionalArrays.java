public class MultiDimensionalArrays {
    public static void main(String[] args) {
        rectangular();
        jagged();
        literal();
    }

    static void rectangular() {
        int[][] marks = new int[3][3];
        int value = 10;

        for (int row = 0; row < marks.length; row++) {
            for (int col = 0; col < marks[row].length; col++) {
                marks[row][col] = value++;
            }
        }

        print(marks);
    }

    // A 2-D array is really an array of arrays, so the rows may differ in length
    static void jagged() {
        int[][] marks = new int[3][];
        marks[0] = new int[]{23};
        marks[1] = new int[]{24, 90};
        marks[2] = new int[]{12, 78, 45};

        print(marks);
    }

    static void literal() {
        int[][] marks = {
                {12, 14, 56},
                {34, 45, 67},
                {45, 67, 78}
        };

        print(marks);
    }

    static void print(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
