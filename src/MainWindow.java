import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public MainWindow() {
        setTitle("Welcome to ExamVerse");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a root panel and set background color
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(211, 211, 211)); // Light grey background

        // Heading Panel
        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BorderLayout());
        headingPanel.setBorder(BorderFactory.createEmptyBorder(200, 0, 30, 0));
        headingPanel.setOpaque(false); // Allow root background to show through

        JLabel welcomeLabel = new JLabel("Welcome to ExamVerse", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 90));
        welcomeLabel.setForeground(new Color(25, 25, 112));
        headingPanel.add(welcomeLabel, BorderLayout.CENTER);

        // === Buttons Panel ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 100));
        buttonPanel.setOpaque(false); // Transparent so rootPanel's color is visible

        JButton adminBtn = new JButton("Admin");
        JButton studentBtn = new JButton("Student");

        adminBtn.setPreferredSize(new Dimension(360, 150));
        studentBtn.setPreferredSize(new Dimension(360, 150));

        adminBtn.setFont(new Font("Arial", Font.BOLD, 30));
        studentBtn.setFont(new Font("Arial", Font.BOLD, 30));

        adminBtn.setBackground(new Color(255, 77, 77));
        adminBtn.setForeground(Color.WHITE);

        studentBtn.setBackground(new Color(60, 179, 113));
        studentBtn.setForeground(Color.WHITE);

        buttonPanel.add(adminBtn);
        buttonPanel.add(studentBtn);

        // Add panels to rootPanel
        rootPanel.add(headingPanel, BorderLayout.NORTH);
        rootPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add root panel to frame
        setContentPane(rootPanel);

        // Button actions
        adminBtn.addActionListener(e -> {
            dispose();
            new AdminAuthFrame();
        });

        studentBtn.addActionListener(e -> {
            dispose();
            new StudentAuthFrame();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        DatabaseInit.initialize();
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
