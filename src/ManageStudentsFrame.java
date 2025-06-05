import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageStudentsFrame extends JFrame {

    private JTextField rollNoField, nameField, usernameField, passwordField;
    private JTextField updateIdField, deleteIdField;
    private JLabel statusLabel;

    public ManageStudentsFrame() {
        setTitle("Manage Students - ExamVerse");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== Root Panel Setup =====
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(211, 211, 211)); // Light grey

        // ===== Title Label =====
        JLabel titleLabel = new JLabel("Manage Students", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 60));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));
        rootPanel.add(titleLabel, BorderLayout.NORTH);

        // ===== Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 20, 80));

        Font labelFont = new Font("Times New Roman", Font.PLAIN, 28);
        Font fieldFont = new Font("Times New Roman", Font.PLAIN, 24);

        // ==== Inputs ====
        rollNoField = new JTextField(); rollNoField.setFont(fieldFont);
        nameField = new JTextField(); nameField.setFont(fieldFont);
        usernameField = new JTextField(); usernameField.setFont(fieldFont);
        passwordField = new JTextField(); passwordField.setFont(fieldFont);
        updateIdField = new JTextField(); updateIdField.setFont(fieldFont);
        deleteIdField = new JTextField(); deleteIdField.setFont(fieldFont);
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        formPanel.add(new JLabel("Roll No:", SwingConstants.LEFT)).setFont(labelFont);
        formPanel.add(rollNoField);
        formPanel.add(new JLabel("Name:", SwingConstants.LEFT)).setFont(labelFont);
        formPanel.add(nameField);
        formPanel.add(new JLabel("Username:", SwingConstants.LEFT)).setFont(labelFont);
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:", SwingConstants.LEFT)).setFont(labelFont);
        formPanel.add(passwordField);

        JButton addBtn = new JButton("Add Student");
        styleButton(addBtn, new Color(60, 179, 113));
        formPanel.add(addBtn);
        formPanel.add(new JLabel(""));

        formPanel.add(new JLabel("Enter Student ID to Update:", SwingConstants.LEFT)).setFont(labelFont);
        formPanel.add(updateIdField);

        JButton updateBtn = new JButton("Update Student");
        styleButton(updateBtn, new Color(30, 144, 255));
        formPanel.add(updateBtn);
        formPanel.add(new JLabel(""));

        JButton viewBtn = new JButton("View All Students");
        styleButton(viewBtn, new Color(255, 165, 0));
        formPanel.add(viewBtn);

        formPanel.add(new JLabel("Enter Student ID to Delete:", SwingConstants.LEFT)).setFont(labelFont);
        formPanel.add(deleteIdField);

        JButton deleteBtn = new JButton("Delete Student");
        styleButton(deleteBtn, new Color(220, 20, 60));
        formPanel.add(deleteBtn);

        formPanel.add(new JLabel("Status:", SwingConstants.LEFT)).setFont(labelFont);
        formPanel.add(statusLabel);

        rootPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        // === Action Listeners ===
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        viewBtn.addActionListener(e -> viewStudents());

        // ==== Bottom Panel with Back Button ====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        bottomPanel.add(backButton);
        rootPanel.add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            new AdminPanel("admin1"); // Pass correct admin username if needed
            dispose(); // Close this window
        });

        setVisible(true);
    }

    private void styleButton(JButton button, Color bg) {
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
    }

    private void addStudent() {
        String rollNo = rollNoField.getText().trim();
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        Font labelFont = new Font("Times New Roman", Font.PLAIN, 28);

        if (rollNo.isEmpty() || name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO student (roll_no, name, username, password) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, rollNo);
            ps.setString(2, name);
            ps.setString(3, username);
            ps.setString(4, password);

            int rows = ps.executeUpdate();
            statusLabel.setText(rows > 0 ? "Student added!" : "Failed to add student.");
            statusLabel.setFont(labelFont);
            statusLabel.setForeground(Color.RED);
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void updateStudent() {
        String idText = updateIdField.getText().trim();
        String rollNo = rollNoField.getText().trim();
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        Font labelFont = new Font("Times New Roman", Font.PLAIN, 28);

        if (idText.isEmpty()) {
            statusLabel.setText("Enter Student ID to update.");
            return;
        }

        if (rollNo.isEmpty() || name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Fill all fields for update.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE student SET roll_no = ?, name = ?, username = ?, password = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, rollNo);
                ps.setString(2, name);
                ps.setString(3, username);
                ps.setString(4, password);
                ps.setInt(5, id);

                int rows = ps.executeUpdate();
                statusLabel.setText(rows > 0 ? "Student updated!" : "Student ID not found.");
                statusLabel.setFont(labelFont);
                statusLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid Student ID.");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void deleteStudent() {
        String idText = deleteIdField.getText().trim();

        Font labelFont = new Font("Times New Roman", Font.PLAIN, 28);

        if (idText.isEmpty()) {
            statusLabel.setText("Enter Student ID to delete.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM student WHERE id = ?";  // Fixed table name
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, id);

                int rows = ps.executeUpdate();
                statusLabel.setText(rows > 0 ? "Student deleted." : "Student ID not found.");
                statusLabel.setFont(labelFont);
                statusLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid Student ID.");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void viewStudents() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM student");

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                        .append(", Roll No: ").append(rs.getString("roll_no"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Username: ").append(rs.getString("username"))
                        .append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "All Students", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }
}
