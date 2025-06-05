import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentPanel extends JFrame {
    private int studentId;
    private JLabel welcomeLabel;

    public StudentPanel(int studentId) {
        this.studentId = studentId;

        setTitle("Student Panel - ExamVerse");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Root panel with background color
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(211, 211, 211)); // Light grey

        // Fetch student name
        String studentName = getStudentName(studentId);

        JLabel welcomeLabel = new JLabel("Welcome, " + studentName , JLabel.CENTER);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 60));
        welcomeLabel.setForeground(new Color(25, 25, 112));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(70, 0, 30, 0));
        rootPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Center button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 50, 50));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(100, 200, 100, 200));
        buttonPanel.setOpaque(false); // Keep background color of root panel

        //Buttons
        JButton attemptExamBtn = new JButton("Attempt Exam");
        JButton viewResultBtn = new JButton("View Recent Result");
        JButton logoutBtn = new JButton("Logout");

        // Button styles
        Font btnFont = new Font("Arial", Font.BOLD, 24);
        Dimension btnSize = new Dimension(300, 80);

        JButton[] buttons = { attemptExamBtn, viewResultBtn, logoutBtn };
        Color[] colors = {
                new Color(255, 99, 71),   // Tomato red
                new Color(100, 149, 237), // Cornflower blue
                new Color(169, 169, 169)  // Dark gray
        };

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(btnFont);
            buttons[i].setPreferredSize(btnSize);
            buttons[i].setBackground(colors[i]);
            buttons[i].setForeground(Color.WHITE);
            buttonPanel.add(buttons[i]);
        }

        // Add to root panel
        rootPanel.add(buttonPanel, BorderLayout.CENTER);

        //Action Listeners
        attemptExamBtn.addActionListener(e -> {
            if (ExamFrame.isAttemptAllowed(studentId)) {
                dispose();
                new ExamFrame(studentId);
            } else {
                JOptionPane.showMessageDialog(this,
                        "You have reached your maximum number of exam attempts.",
                        "Attempt Limit Reached",
                        JOptionPane.WARNING_MESSAGE);
            }
        });


        viewResultBtn.addActionListener(e -> showRecentResult());

        logoutBtn.addActionListener(e -> {
            dispose();
            new MainWindow();
        });

        // Add root panel to frame
        setContentPane(rootPanel);
        setVisible(true);
    }

    private String getStudentName(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT name FROM student WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Student";
    }

    private void showRecentResult() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT score, attempt_time FROM exam_attempts
                WHERE student_id = ?
                ORDER BY attempt_time DESC
                LIMIT 1
            """);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int score = rs.getInt("score");
                String time = rs.getString("attempt_time");
                JOptionPane.showMessageDialog(this,
                        "Your most recent result:\nScore: " + score + "\nTime: " + time,
                        "Recent Result",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please attempt the exam first.",
                        "No Result Found",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
