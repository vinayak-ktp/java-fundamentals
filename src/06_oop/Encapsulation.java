public class Encapsulation {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();
        account.deposit(500);
        account.withdraw(300);
        account.withdraw(1000);   // rejected by the guard inside the class

        System.out.println(account.getBalance());

        Student student = new Student("Aditya", 101, 28, "IIT Guwahati");
        student.setCollege("IIT Bombay");
        System.out.println(student.getName() + " , " + student.getCollege());
    }

    // Fields stay private, so every change goes through a method that can validate
    static class BankAccount {
        private double balance;

        void deposit(int amount) {
            if (amount <= 0) {
                return;
            }
            balance += amount;
        }

        void withdraw(int amount) {
            if (amount > balance) {
                System.out.println("Insufficient balance");
                return;
            }
            balance -= amount;
        }

        double getBalance() {
            return balance;
        }
    }

    static class Student {
        private String name;
        private int rollNumber;
        private int age;
        private String college;

        Student(String name, int rollNumber, int age, String college) {
            this.name = name;
            this.rollNumber = rollNumber;
            this.age = age;
            this.college = college;
        }

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        int getRollNumber() {
            return rollNumber;
        }

        String getCollege() {
            return college;
        }

        void setCollege(String college) {
            // a setter is the place for validation the caller cannot skip
            if (college == null || college.isBlank()) {
                return;
            }
            this.college = college;
        }
    }
}
