import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminAuthFrame extends JFrame {
    private JTextField regUsernameField, regNameField;
    private JPasswordField regPasswordField;
    private JTextField logUsernameField;
    private JPasswordField logPasswordField;
    private JLabel regStatusLabel, logStatusLabel;

    public AdminAuthFrame() {
        setTitle("Admin Register / Login - ExamVerse");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Root Panel with Background
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(211, 211, 211)); // Light grey
        setContentPane(rootPanel);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 28));

        // Register Panel
        JPanel registerPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        registerPanel.setPreferredSize(new Dimension(900, 600));
        registerPanel.setBackground(new Color(230,240, 255)); // light blue
        registerPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        Font fieldFont = new Font("Times New Roman", Font.PLAIN, 24);

        regUsernameField = new JTextField(); regUsernameField.setFont(fieldFont);
        regNameField = new JTextField(); regNameField.setFont(fieldFont);
        regPasswordField = new JPasswordField(); regPasswordField.setFont(fieldFont);
        regStatusLabel = new JLabel(""); regStatusLabel.setFont(fieldFont);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Times New Roman", Font.BOLD, 28)); // Change font and size
        userLabel.setForeground(Color.BLACK);// Change text color
        registerPanel.add(userLabel);
        registerPanel.add(regUsernameField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
        nameLabel.setForeground(Color.BLACK);
        registerPanel.add(nameLabel);
        registerPanel.add(regNameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
        passLabel.setForeground(Color.BLACK);
        registerPanel.add(passLabel);
        registerPanel.add(regPasswordField);

        JButton regBtn = new JButton("Register");
        regBtn.setBackground(new Color(70, 130, 180));
        regBtn.setForeground(Color.WHITE);
        regBtn.setFont(new Font("Britannic Bold", Font.BOLD, 28));

        registerPanel.add(regBtn);
        registerPanel.add(regStatusLabel);

        regBtn.addActionListener(e -> registerAdmin());

        tabbedPane.addTab("Register", registerPanel);

        // Login Panel
        JPanel loginPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        loginPanel.setPreferredSize(new Dimension(900, 600));
        loginPanel.setBackground(new Color(230, 240, 255));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        logUsernameField = new JTextField(); logUsernameField.setFont(fieldFont);
        logPasswordField = new JPasswordField(); logPasswordField.setFont(fieldFont);
        logStatusLabel = new JLabel(""); logStatusLabel.setFont(fieldFont);

        JLabel loginUserLabel = new JLabel("Username:");
        loginUserLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
        loginUserLabel.setForeground(Color.BLACK);
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

        loginBtn.addActionListener(e -> loginAdmin());

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

    private void registerAdmin() {
        String username = regUsernameField.getText().trim();
        String name = regNameField.getText().trim();
        String password = new String(regPasswordField.getPassword());

        if (username.isEmpty() || name.isEmpty() || password.isEmpty()) {
            regStatusLabel.setText("Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM admin WHERE username = ?");
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                regStatusLabel.setText("Username already exists.");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO admin (username, name, password) VALUES (?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, name);
            ps.setString(3, password); // Use hashing in production

            int rows = ps.executeUpdate();
            if (rows > 0) {
                regStatusLabel.setText("Registration successful!");
                regStatusLabel.setForeground(Color.RED);
                // Optionally clear the input fields
                regUsernameField.setText("");
                regNameField.setText("");
                regPasswordField.setText("");
            } else {
                regStatusLabel.setText("Registration failed.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            regStatusLabel.setText("Error: " + ex.getMessage());
        }
    }


    private void loginAdmin() {
        String username = logUsernameField.getText().trim();
        String password = new String(logPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            logStatusLabel.setText("Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM admin WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                new AdminPanel(username);
            } else {
                logStatusLabel.setText("Invalid username or password.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logStatusLabel.setText("Error: " + ex.getMessage());
        }
    }
}
