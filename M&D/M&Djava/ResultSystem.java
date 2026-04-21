import java.util.*;

// Student class
class Student {
    String name;
    int rollNo;
    int marks[];
    String subjects[] = {"Maths", "Science", "English", "Computer", "History"};
    int total;
    float percentage;
    String grade;
    String resultClass;

    Student(String name, int rollNo, int marks[]) {
        this.name = name;
        this.rollNo = rollNo;
        this.marks = marks;
    }

    void calculateResult() {
        total = 0;
        for (int m : marks) {
            total += m;
        }

        percentage = (float) total / marks.length;
        assignGrade();
        assignClass();
    }

    void assignGrade() {
        if (percentage >= 90) grade = "A+";
        else if (percentage >= 75) grade = "A";
        else if (percentage >= 60) grade = "B";
        else if (percentage >= 50) grade = "C";
        else grade = "Fail";
    }

    void assignClass() {
        if (percentage >= 75) resultClass = "Distinction";
        else if (percentage >= 60) resultClass = "First Class";
        else if (percentage >= 50) resultClass = "Second Class";
        else resultClass = "Fail";
    }

    boolean isPass(int mark) {
        return mark >= 35;
    }

    void display() {
        System.out.println("\n==============================");
        System.out.println("        REPORT CARD");
        System.out.println("==============================");
        System.out.println("Name    : " + name);
        System.out.println("Roll No : " + rollNo);

        System.out.println("\nSubjects        Marks   Status");

        for (int i = 0; i < marks.length; i++) {
        System.out.printf("%-15s %-7d %-10s\n",
            subjects[i],
            marks[i],
            (isPass(marks[i]) ? "Pass" : "Fail"));
}
        System.out.println("------------------------------");
        System.out.println("Total      : " + total);
        System.out.println("Percentage : " + percentage);
        System.out.println("Grade      : " + grade);
        System.out.println("Class      : " + resultClass);
        System.out.println("==============================");
    }
}

// Main class
public class ResultSystem {

    static Student findTopper(ArrayList<Student> list) {
        Student top = list.get(0);
        for (Student s : list) {
            if (s.percentage > top.percentage) {
                top = s;
            }
        }
        return top;
    }

    static float classAverage(ArrayList<Student> list) {
        float sum = 0;
        for (Student s : list) {
            sum += s.percentage;
        }
        return sum / list.size();
    }

    static Student searchByRoll(ArrayList<Student> list, int roll) {
        for (Student s : list) {
            if (s.rollNo == roll) return s;
        }
        return null;
    }

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Student> students = new ArrayList<>();

        int choice;

        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Add Student");
            System.out.println("2. Display All");
            System.out.println("3. Find Topper");
            System.out.println("4. Class Average");
            System.out.println("5. Search by Roll No");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Roll No: ");
                    int roll = sc.nextInt();

                    int marks[] = new int[5];
                    System.out.println("Enter marks:");
                    for (int i = 0; i < 5; i++) {
                        System.out.print("Subject " + (i + 1) + ": ");
                        marks[i] = sc.nextInt();
                    }

                    Student s = new Student(name, roll, marks);
                    s.calculateResult();
                    students.add(s);
                    break;

                case 2:
                    for (Student st : students) {
                        st.display();
                    }
                    break;

                case 3:
                    if (!students.isEmpty()) {
                        Student top = findTopper(students);
                        System.out.println("\nTopper: " + top.name +
                                " (" + top.percentage + "%)");
                    }
                    break;

                case 4:
                    if (!students.isEmpty()) {
                        System.out.println("Class Average: " +
                                classAverage(students));
                    }
                    break;

                case 5:
                    System.out.print("Enter Roll No to search: ");
                    int r = sc.nextInt();
                    Student found = searchByRoll(students, r);

                    if (found != null)
                        found.display();
                    else
                        System.out.println("Student not found!");
                    break;

                case 6:
                    System.out.println("Exiting...");
                    break;
            }

        } while (choice != 6);
    }
}