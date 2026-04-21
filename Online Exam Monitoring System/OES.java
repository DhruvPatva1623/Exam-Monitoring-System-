import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

// ------------------ ADMIN GUI ------------------
class AdminGUI extends Frame {

    TextArea logArea, resultArea;
    ResultCanvas canvas;

    int[] correctAnswers = {8, 6, 12, 4};

    Map<Integer, java.util.List<Integer>> studentData =
            Collections.synchronizedMap(new HashMap<>());

    Map<Integer, Integer> scoreMap =
            Collections.synchronizedMap(new HashMap<>());

    AdminGUI() {
        setTitle("📊 Admin Dashboard");
        setSize(950, 500);
        setLayout(new BorderLayout());

        // Left panel (Logs)
        logArea = new TextArea();
        logArea.setBackground(Color.black);
        logArea.setForeground(Color.green);

        // Center panel (Live Result)
        resultArea = new TextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Right panel (Graph)
        canvas = new ResultCanvas(scoreMap);

        add(logArea, BorderLayout.WEST);
        add(resultArea, BorderLayout.CENTER);
        add(canvas, BorderLayout.EAST);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        startServer();
    }

    void startServer() {
        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(5000);

                EventQueue.invokeLater(() ->
                        logArea.append("🚀 Server Started...\n"));

                while (true) {

                    DatagramPacket packet =
                            new DatagramPacket(new byte[1024], 1024);

                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());

                    System.out.println("Received: " + msg);

                    // Safe UI log update
                    EventQueue.invokeLater(() ->
                            logArea.append("📩 " + msg + "\n"));

                    // Parse
                    String[] parts = msg.split(":");
                    int id = Integer.parseInt(parts[0].split(" ")[1]);
                    int ans = Integer.parseInt(parts[1].trim());

                    studentData.putIfAbsent(id, new ArrayList<>());
                    studentData.get(id).add(ans);

                    updateResults();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    void updateResults() {

        StringBuilder resultText = new StringBuilder();
        resultText.append("📊 LIVE RESPONSE ANALYSIS\n\n");

        Map<Integer, Integer> newScores = new HashMap<>();

        for (Map.Entry<Integer, java.util.List<Integer>> entry : studentData.entrySet()) {

            int id = entry.getKey();
            java.util.List<Integer> answers = entry.getValue();

            int score = 0;

            resultText.append("👨‍🎓 Student ").append(id).append("\n");

            for (int i = 0; i < correctAnswers.length; i++) {

                if (i < answers.size()) {

                    int studentAns = answers.get(i);
                    int correctAns = correctAnswers[i];

                    if (studentAns == correctAns) {
                        score++;
                        resultText.append("Q").append(i + 1)
                                .append(" ✔ Correct (").append(studentAns).append(")\n");
                    } else {
                        resultText.append("Q").append(i + 1)
                                .append(" ❌ Wrong (Your: ")
                                .append(studentAns)
                                .append(", Correct: ")
                                .append(correctAns)
                                .append(")\n");
                    }

                } else {
                    resultText.append("Q").append(i + 1)
                            .append(" ⏳ Not Answered\n");
                }
            }

            resultText.append("🎯 Score: ").append(score)
                    .append("/").append(correctAnswers.length).append("\n");
            resultText.append("---------------------------\n\n");

            newScores.put(id, score);
        }

        // SAFE UI UPDATE
        EventQueue.invokeLater(() -> {
            resultArea.setText(resultText.toString());
            scoreMap.clear();
            scoreMap.putAll(newScores);
            canvas.repaint();
        });
    }
}

// ------------------ GRAPH ------------------
class ResultCanvas extends Canvas {

    Map<Integer, Integer> scores;

    ResultCanvas(Map<Integer, Integer> scores) {
        this.scores = scores;
        setBackground(Color.white);
    }

    public void paint(Graphics g) {

        int x = 40;

        for (Map.Entry<Integer, Integer> entry : scores.entrySet()) {

            int height = entry.getValue() * 40;

            g.setColor(Color.blue);
            g.fillRect(x, 300 - height, 50, height);

            g.setColor(Color.black);
            g.drawString("S" + entry.getKey(), x, 320);
            g.drawString("" + entry.getValue(), x, 280 - height);

            x += 80;
        }
    }
}

// ------------------ STUDENT GUI ------------------
class StudentGUI extends Frame implements ActionListener {

    Label titleLabel, questionLabel;
    TextField answerField;
    Button nextBtn;

    int studentId;
    int qIndex = 0;

    String[] questions = {
            "5 + 3 = ?",
            "10 - 4 = ?",
            "6 × 2 = ?",
            "8 ÷ 2 = ?"
    };

    StudentGUI(int id) {
        this.studentId = id;

        setTitle("🎓 Student " + id);
        setSize(320, 250);
        setLayout(new GridLayout(5,1,10,10));

        titleLabel = new Label("Online Exam", Label.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.blue);

        questionLabel = new Label(questions[qIndex], Label.CENTER);

        answerField = new TextField();

        nextBtn = new Button("Next");
        nextBtn.setBackground(Color.green);

        add(titleLabel);
        add(questionLabel);
        add(answerField);
        add(nextBtn);

        nextBtn.addActionListener(this);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        try {

            String input = answerField.getText();

            if (input.isEmpty()) return;

            int ans = Integer.parseInt(input);

            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("127.0.0.1");

            String msg = "Student " + studentId + ": " + ans;

            System.out.println("Sending: " + msg);

            byte[] buffer = msg.getBytes();

            DatagramPacket packet =
                    new DatagramPacket(buffer, buffer.length, address, 5000);

            socket.send(packet);
            socket.close();

            answerField.setText("");

            qIndex++;

            if (qIndex < questions.length) {
                questionLabel.setText(questions[qIndex]);
            } else {
                questionLabel.setText("✅ Exam Finished!");
                nextBtn.setEnabled(false);
            }

        } catch (Exception ex) {
            System.out.println("Invalid Input");
        }
    }
}

// ------------------ MAIN ------------------
public class OES {
    public static void main(String[] args) {

        new AdminGUI();

        String input = javax.swing.JOptionPane.showInputDialog("Enter number of students:");
        int n = Integer.parseInt(input);

        for (int i = 1; i <= n; i++) {
            new StudentGUI(i);
        }
    }
}