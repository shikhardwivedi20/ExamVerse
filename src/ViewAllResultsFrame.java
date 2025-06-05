import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewAllResultsFrame extends JFrame {

    private final Color rootColor = new Color(0xD3D3D3);
    private final Font headerFont = new Font("Serif", Font.BOLD, 60);
    private final Font labelFont = new Font("Times New Roman", Font.BOLD, 26);
    private final Font tableFont = new Font("Times New Roman", Font.PLAIN, 20);

    public ViewAllResultsFrame() {
        setTitle("All Students' Recent Exam Results - ExamVerse");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(rootColor);
        setLayout(new BorderLayout(20, 20));

        // ===== Title Label =====
        JLabel titleLabel = new JLabel("All Students' Recent Exam Results", SwingConstants.CENTER);
        titleLabel.setFont(headerFont);
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // ===== Table Setup =====
        String[] columns = {"Roll No", "Name", "Score", "Attempt Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        JTable table = new JTable(tableModel);
        table.setFont(tableFont);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 22));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ===== Back Button =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(rootColor);
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            new AdminPanel("admin1"); // Replace "admin1" if needed
            dispose();
        });

        // ===== Fetch and Populate Data =====
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT s.roll_no, s.name, ea.score, ea.attempt_time
                FROM student s
                LEFT JOIN (
                    SELECT student_id, MAX(attempt_time) as latest_time
                    FROM exam_attempts
                    GROUP BY student_id
                ) latest ON s.id = latest.student_id
                LEFT JOIN exam_attempts ea ON ea.student_id = s.id AND ea.attempt_time = latest.latest_time;
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String roll = rs.getString("roll_no");
                String name = rs.getString("name");
                String score = rs.getString("score") != null ? rs.getString("score") : "Not Attempted";
                String time = rs.getString("attempt_time") != null ? rs.getString("attempt_time") : "-";
                tableModel.addRow(new Object[]{roll, name, score, time});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching results.");
        }

        setVisible(true);
    }
}
