import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ResetAttemptsFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ResetAttemptsFrame() {
        setTitle("Reset Exam Attempts");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(211, 211, 211));
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Manage Student Exam Attempts", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 60));
        title.setForeground(new Color(25, 25, 112));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {"Roll No", "Name", "Attempts Made", "Attempt Limit"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 20));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 22));

        loadData();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ===== South Panel (Buttons + Back Button) =====
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(211, 211, 211));

        // Control Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(211, 211, 211));

        JButton resetSelectedBtn = new JButton("Reset Selected");
        JButton resetAllBtn = new JButton("Reset All");
        JButton setGlobalLimitBtn = new JButton("Set Global Attempt Limit");
        JButton setSelectedLimitBtn = new JButton("Set Attempt Limit (Selected)");

        styleButton(resetSelectedBtn);
        styleButton(resetAllBtn);
        styleButton(setGlobalLimitBtn);
        styleButton(setSelectedLimitBtn);

        btnPanel.add(resetSelectedBtn);
        btnPanel.add(resetAllBtn);
        btnPanel.add(setGlobalLimitBtn);
        btnPanel.add(setSelectedLimitBtn);

        // Back Button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(new Color(211, 211, 211));
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        backPanel.add(backButton);

        // Add to south panel
        southPanel.add(backPanel, BorderLayout.WEST);
        southPanel.add(btnPanel, BorderLayout.CENTER);

        // Add south panel to frame
        add(southPanel, BorderLayout.SOUTH);

        // Action Listeners
        resetSelectedBtn.addActionListener(e -> resetSelected());
        resetAllBtn.addActionListener(e -> resetAll());
        setGlobalLimitBtn.addActionListener(e -> setAttemptLimit());
        setSelectedLimitBtn.addActionListener(e -> setAttemptLimitForSelected());
        backButton.addActionListener(e -> {
            new AdminPanel("admin1"); // Change username if needed
            dispose();
        });

        setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(100, 149, 237));
        btn.setForeground(Color.WHITE);
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
            SELECT s.id, s.roll_no, s.name, 
                   (SELECT COUNT(*) FROM exam_attempts ea WHERE ea.student_id = s.id) as attempts,
                   s.allowed_attempts
            FROM student s
         """)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getInt("attempts"),
                        rs.getInt("allowed_attempts")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student attempts.");
        }
    }

    private void resetSelected() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }

        String roll = model.getValueAt(selected, 0).toString();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                DELETE FROM exam_attempts WHERE student_id = (SELECT id FROM student WHERE roll_no = ?)
             """)) {
            ps.setString(1, roll);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Attempts reset for Roll No: " + roll);
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error resetting attempts.");
        }
    }

    private void resetAll() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to reset attempts for ALL students?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM exam_attempts");
            JOptionPane.showMessageDialog(this, "All student attempts reset.");
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error resetting all attempts.");
        }
    }

    private void setAttemptLimit() {
        String limitStr = JOptionPane.showInputDialog(this, "Set new global attempt limit:");
        if (limitStr == null || !limitStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
            return;
        }

        int limit = Integer.parseInt(limitStr);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE student SET allowed_attempts = " + limit);
            JOptionPane.showMessageDialog(this, "Attempt limit set to " + limit + " for all students.");
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error setting attempt limit.");
        }
    }

    private void setAttemptLimitForSelected() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }

        String roll = model.getValueAt(selected, 0).toString();
        String currentLimit = model.getValueAt(selected, 3).toString();

        String limitStr = JOptionPane.showInputDialog(this,
                "Set new attempt limit for Roll No: " + roll + " (Current limit: " + currentLimit + "):");
        if (limitStr == null || !limitStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
            return;
        }

        int limit = Integer.parseInt(limitStr);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE student SET allowed_attempts = ? WHERE roll_no = ?")) {
            ps.setInt(1, limit);
            ps.setString(2, roll);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Attempt limit updated for Roll No: " + roll);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update attempt limit.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating attempt limit.");
        }
    }
}
