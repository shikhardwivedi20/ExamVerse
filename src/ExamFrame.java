import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.Timer;
import java.time.LocalDateTime;


public class ExamFrame extends JFrame {
    private int currentQuestionIndex = 0;
    private final java.util.List<Question> questionList = new java.util.ArrayList<>();
    private final Map<Integer, Integer> selectedAnswers = new HashMap<>();

    private JLabel questionLabel;
    private final JRadioButton[] options = new JRadioButton[4];
    private final ButtonGroup optionGroup = new ButtonGroup();
    private JButton nextBtn, prevBtn, submitBtn;
    private JLabel timerLabel;

    private Timer examTimer;
    private int remainingSeconds;
    private int examDurationInMinutes;

    private final int studentId;

    public static boolean isAttemptAllowed(int studentId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
            SELECT s.allowed_attempts,
                   (SELECT COUNT(*) FROM exam_attempts ea WHERE ea.student_id = s.id) AS attempts_made
            FROM student s
            WHERE s.id = ?
        """;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, studentId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int limit = rs.getInt("allowed_attempts");
                    int made = rs.getInt("attempts_made");
                    return made < limit;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public ExamFrame(int studentId) {
        this.studentId = studentId;

        setTitle("Attempt Exam - ExamVerse");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(0xD3D3D3));
        setLayout(new BorderLayout(20, 20));

        // ==== Title Panel ====
        JLabel titleLabel = new JLabel("Exam Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 60));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // ==== Timer Label ====
        timerLabel = new JLabel("Time Left: ", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        timerLabel.setForeground(Color.RED);
        add(timerLabel, BorderLayout.EAST);

        // ==== Main Question Panel ====
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new GridLayout(6, 1, 10, 10));
        questionPanel.setBackground(new Color(0xD3D3D3));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        questionLabel = new JLabel("", SwingConstants.LEFT);
        questionLabel.setFont(new Font("Times New Roman", Font.BOLD, 34));
        questionPanel.add(questionLabel);

        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setFont(new Font("Times New Roman", Font.PLAIN, 28));
            options[i].setBackground(new Color(0xD3D3D3));
            optionGroup.add(options[i]);
            questionPanel.add(options[i]);
        }

        add(questionPanel, BorderLayout.CENTER);

        // ==== Navigation Panel ====
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        navPanel.setBackground(new Color(0xD3D3D3));

        prevBtn = new JButton("Previous");
        nextBtn = new JButton("Next");
        submitBtn = new JButton("Submit");

        styleButton(prevBtn);
        styleButton(nextBtn);
        styleButton(submitBtn);

        navPanel.add(prevBtn);
        navPanel.add(nextBtn);
        navPanel.add(submitBtn);

        add(navPanel, BorderLayout.SOUTH);

        // ==== Button Actions ====
        prevBtn.addActionListener(e -> {
            saveSelectedAnswer();
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                loadQuestion();
            }
        });

        nextBtn.addActionListener(e -> {
            saveSelectedAnswer();
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                loadQuestion();
            }
        });

        submitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to submit?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                saveSelectedAnswer();
                submitExam();
            }
        });

        // ==== Load Questions and Start Exam ====
        loadQuestions();
        if (questionList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions available.");
            dispose();
            return;
        }

        examDurationInMinutes = fetchExamDuration();
        startTimer();
        loadQuestion();

        setVisible(true);
    }


    private boolean isAttemptAllowed() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
            SELECT s.allowed_attempts, 
                   (SELECT COUNT(*) FROM exam_attempts ea WHERE ea.student_id = s.id) AS attempts_made
            FROM student s WHERE s.id = ?
        """;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, studentId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int limit = rs.getInt("allowed_attempts");
                    int made = rs.getInt("attempts_made");
                    return made < limit;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking attempt limit.");
        }
        return false;
    }


    private int fetchExamDuration() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT duration_minutes FROM exam_config LIMIT 1")) {
            if (rs.next()) return rs.getInt("duration_minutes");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch exam time. Using default 5 minutes.");
        }
        return 5; // default fallback
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Times New Roman", Font.BOLD, 20));
        button.setFocusPainted(false);
    }

    private void loadQuestions() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM questions ORDER BY RANDOM()")) {

            while (rs.next()) {
                questionList.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4"),
                        rs.getInt("correct_option")
                ));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading questions: " + e.getMessage());
        }
    }

    private void loadQuestion() {
        Question q = questionList.get(currentQuestionIndex);
        questionLabel.setText("<html>Q" + (currentQuestionIndex + 1) + ": " + q.text + "</html>");
        options[0].setText(q.option1);
        options[1].setText(q.option2);
        options[2].setText(q.option3);
        options[3].setText(q.option4);
        optionGroup.clearSelection();

        // Restore previously selected answer
        if (selectedAnswers.containsKey(q.id)) {
            int selected = selectedAnswers.get(q.id);
            if (selected >= 1 && selected <= 4) {
                options[selected - 1].setSelected(true);
            }
        }
    }

    private void saveSelectedAnswer() {
        int selectedOption = -1;
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSelected()) {
                selectedOption = i + 1;
                break;
            }
        }
        if (selectedOption != -1) {
            int questionId = questionList.get(currentQuestionIndex).id;
            selectedAnswers.put(questionId, selectedOption);
        }
    }

    private void startTimer() {
        remainingSeconds = examDurationInMinutes * 60;
        updateTimerLabel();
        examTimer = new Timer(1000, e -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                examTimer.stop();
                JOptionPane.showMessageDialog(this, "Time is up! Auto-submitting your exam.");
                submitExam();
            }
        });
        examTimer.start();
    }

    private void updateTimerLabel() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
    }

    private void submitExam() {
        if (examTimer != null) examTimer.stop();

        int score = 0;

        for (Question q : questionList) {
            int selected = selectedAnswers.getOrDefault(q.id, -1);
            if (selected == q.correctOption) {
                score++;
            }
        }

        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO exam_attempts(student_id, score, attempt_time) VALUES (?, ?, ?)")) {
            ps.setInt(1, studentId);
            ps.setInt(2, score);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = LocalDateTime.now().format(formatter);
            ps.setString(3, formattedTime);


            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Exam submitted! Your score: " + score + "/" + questionList.size());
            new StudentPanel(studentId); // redirect to student panel
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving result: " + e.getMessage());
        }
    }

    // ===== Inner class for Question structure =====
    static class Question {
        int id;
        String text, option1, option2, option3, option4;
        int correctOption;

        public Question(int id, String text, String o1, String o2, String o3, String o4, int correct) {
            this.id = id;
            this.text = text;
            this.option1 = o1;
            this.option2 = o2;
            this.option3 = o3;
            this.option4 = o4;
            this.correctOption = correct;
        }
    }
}
