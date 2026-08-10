/*
 * Before enums, constants were public static final fields. That approach has
 * no type safety (any int or String passes), reads poorly, and does not group
 * related values - the compiler cannot stop you mixing Role.ADMIN with a
 * PaymentStatus. An enum is a fixed, type safe set of instances.
 */
public class EnumConcept {
    public static void main(String[] args) {
        System.out.println(LegacyStatus.SUCCESS);   // just a String, no type safety

        PaymentStatus status = PaymentStatus.FAILED;
        System.out.println(status.name());

        System.out.println(Direction.SOUTH.getDegree());
        Direction.NORTH.move();

        builtInMethods();
    }

    static void builtInMethods() {
        for (Direction d : Direction.values()) {
            System.out.print(d.name() + " ");
        }
        System.out.println();

        Direction east = Direction.valueOf("EAST");   // throws if the name is unknown
        System.out.println(east + " at index " + east.ordinal());
    }

    static class LegacyStatus {
        public static final String SUCCESS = "Success";
        public static final String FAILED = "Failed";
        public static final String PENDING = "Pending";
    }

    enum PaymentStatus {
        SUCCESS,
        FAILED,
        PENDING
    }

    // Enums can carry state and behaviour, and even override a method per constant
    enum Direction {
        NORTH(0) {
            @Override
            public void move() {
                System.out.println("Move up (Y + 1)");
            }
        },
        SOUTH(180) {
            @Override
            public void move() {
                System.out.println("Move down (Y - 1)");
            }
        },
        EAST(90) {
            @Override
            public void move() {
                System.out.println("Move right (X + 1)");
            }
        },
        WEST(270) {
            @Override
            public void move() {
                System.out.println("Move left (X - 1)");
            }
        };

        private final int degree;

        // The constructor is implicitly private
        Direction(int degree) {
            this.degree = degree;
        }

        public int getDegree() {
            return degree;
        }

        public abstract void move();
    }
}
