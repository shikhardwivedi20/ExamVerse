import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewResultFrame extends JFrame {
    private int studentId;
    private JLabel resultLabel;

    public ViewResultFrame(int studentId) {
        this.studentId = studentId;

        setTitle("View Result - ExamVerse");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(resultLabel, BorderLayout.CENTER);

        fetchLatestResult();

        setVisible(true);
    }

    private void fetchLatestResult() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT score, attempt_time
                FROM exam_attempts
                WHERE student_id = ?
                ORDER BY attempt_time DESC
                LIMIT 1
            """);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int score = rs.getInt("score");
                String time = rs.getString("attempt_time");

                resultLabel.setText("<html><center>Recent Score: <b>" + score + "</b><br/>" +
                        "Attempted On: " + time + "</center></html>");
            } else {
                resultLabel.setText("❗ No attempt found. Please attempt the exam first.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultLabel.setText("Error fetching result.");
        }
    }
}
