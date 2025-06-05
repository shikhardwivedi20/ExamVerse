import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentAuthFrame extends JFrame {
    private JTextField logUsernameField;
    private JPasswordField logPasswordField;
    private JLabel logStatusLabel;

    public StudentAuthFrame() {
        setTitle("Student Login - ExamVerse");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Root Panel with Background
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(211, 211, 211)); // Light blue background
        setContentPane(rootPanel);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 28));

        // Login Panel
        JPanel loginPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        loginPanel.setPreferredSize(new Dimension(900, 600));
        loginPanel.setBackground(new Color(230, 240, 255));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        Font fieldFont = new Font("Times New Roman", Font.PLAIN, 24);

        logUsernameField = new JTextField(); logUsernameField.setFont(fieldFont);
        logPasswordField = new JPasswordField(); logPasswordField.setFont(fieldFont);
        logStatusLabel = new JLabel(""); logStatusLabel.setFont(fieldFont);

        JLabel loginUserLabel = new JLabel("Username:");
        loginUserLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
        loginUserLabel.setForeground(Color.BLACK); // Dark green
        loginPanel.add(loginUserLabel);
        loginPanel.add(logUsernameField);

        JLabel loginPassLabel = new JLabel("Password:");
        loginPassLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
        loginPassLabel.setForeground(Color.BLACK);
        loginPanel.add(loginPassLabel);
        loginPanel.add(logPasswordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(46, 139, 87));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Britannic Bold", Font.BOLD, 28));
        loginPanel.add(loginBtn);
        loginPanel.add(logStatusLabel);

        loginBtn.addActionListener(e -> loginStudent());

        tabbedPane.addTab("Login", loginPanel);

        // Center Panel to hold tabbedPane
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(tabbedPane);

        rootPanel.add(centerPanel, BorderLayout.CENTER);

        // Back Button at Bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        bottomPanel.add(backButton);
        rootPanel.add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            dispose();
            new MainWindow();
        });

        setVisible(true);
    }

    private void loginStudent() {
        String username = logUsernameField.getText().trim();
        String password = new String(logPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            logStatusLabel.setText("Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM student WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int studentId = rs.getInt("id");
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                new StudentPanel(studentId); // Redirect to Student Panel
            } else {
                logStatusLabel.setText("Invalid username or password.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logStatusLabel.setText("Error: " + ex.getMessage());
        }
    }
}
