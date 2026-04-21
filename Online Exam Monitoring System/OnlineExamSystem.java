import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;

// ------------------ BASE CLASS ------------------
class User {
    String name;

    User(String name) {
        this.name = name;
    }
}

// ------------------ STUDENT CLASS ------------------
class Student extends User implements Runnable {
    int id;
    List<Integer> answers = new ArrayList<>();

    Student(int id, String name) {
        super(name);
        this.id = id;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");

            // Simulating answers
            for (int i = 0; i < 5; i++) {
                int ans = new Random().nextInt(4) + 1;
                answers.add(ans);

                String message = "Student " + id + " Answer " + i + ": " + ans;
                byte[] buffer = message.getBytes();

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 9876);
                socket.send(packet);

                Thread.sleep(1000); // simulate time
            }

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// ------------------ ADMIN CLASS ------------------
class Admin extends User {
    List<String> logs = Collections.synchronizedList(new ArrayList<>());

    Admin(String name) {
        super(name);
    }

    // Monitor (UDP Server)
    public void startMonitoring() {
        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(9876);
                byte[] buffer = new byte[1024];

                System.out.println("Admin Monitoring Started...");

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String received = new String(packet.getData(), 0, packet.getLength());
                    logs.add(received);

                    System.out.println("MONITOR: " + received);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Evaluate using Streams
    public void evaluate(List<Student> students) {
        System.out.println("\n--- RESULTS ---");

        students.stream().forEach(s -> {
            long score = s.answers.stream()
                    .filter(a -> a == 1) // assume correct answer = 1
                    .count();

            System.out.println("Student " + s.id + " Score: " + score);
        });
    }
}

// ------------------ MAIN CLASS ------------------
public class OnlineExamSystem {
    public static void main(String[] args) throws Exception {

        Admin admin = new Admin("Admin");
        admin.startMonitoring();

        // Create students
        List<Student> students = Arrays.asList(
                new Student(1, "A"),
                new Student(2, "B"),
                new Student(3, "C")
        );

        // Multithreading (parallel exam)
        List<Thread> threads = new ArrayList<>();

        for (Student s : students) {
            Thread t = new Thread(s);
            threads.add(t);
            t.start();
        }

        // Wait for all students
        for (Thread t : threads) {
            t.join();
        }

        Thread.sleep(2000); // wait for monitoring

        admin.evaluate(students);
    }
}