import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SetExamTimeFrame extends JFrame {

    private JTextField durationField;

    public SetExamTimeFrame() {
        setTitle("Set Exam Time");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(211, 211, 211));
        setLayout(new BorderLayout(20, 20));

        // ==== Title Panel ====
        JLabel titleLabel = new JLabel("Set Exam Duration (in minutes)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 60));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(80, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // ===== Center Panel with Title, Field, and Button =====
        JPanel rootPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        rootPanel.setBackground(new Color(211, 211, 211));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(80, 600, 100, 600));

        durationField = new JTextField();
        durationField.setFont(new Font("Arial", Font.PLAIN, 28));

        JButton setBtn = new JButton("Update Time");
        setBtn.setBackground(new Color(60, 179, 113));
        setBtn.setForeground(Color.WHITE);
        setBtn.setFont(new Font("Arial", Font.BOLD, 18));
        setBtn.addActionListener(e -> updateDuration());

        rootPanel.add(durationField);
        rootPanel.add(setBtn);
        add(rootPanel, BorderLayout.CENTER);

        // ===== Back Button at the Bottom =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(211, 211, 211));
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            new AdminPanel("admin1"); // Replace with appropriate admin name if needed
            dispose();
        });

        fetchCurrentDuration();
        setVisible(true);
    }

    private void fetchCurrentDuration() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT duration_minutes FROM exam_config LIMIT 1")) {
            if (rs.next()) {
                durationField.setText(String.valueOf(rs.getInt("duration_minutes")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching duration: " + e.getMessage());
        }
    }

    private void updateDuration() {
        try {
            int minutes = Integer.parseInt(durationField.getText().trim());
            if (minutes <= 0) throw new NumberFormatException();

            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE exam_config SET duration_minutes = " + minutes);
                JOptionPane.showMessageDialog(this, "Exam duration updated to " + minutes + " minutes.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating duration: " + e.getMessage());
        }
    }
}
