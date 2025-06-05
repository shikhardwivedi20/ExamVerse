import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JFrame {
    private String adminUsername;
    private String adminName;

    public AdminPanel(String adminUsername) {
        this.adminUsername = adminUsername;
        this.adminName = fetchAdminName(adminUsername);  // Get name from DB

        setTitle("Admin Panel - ExamVerse");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Root panel with background color
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(211, 211, 211)); // Light grey

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome, " + adminName, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 60));
        welcomeLabel.setForeground(new Color(25, 25, 112));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(70, 0, 30, 0));
        rootPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Center button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 50, 50));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(100, 200, 100, 200));
        buttonPanel.setOpaque(false); // Keep background color of root panel

        // Buttons
        JButton manageStudentsBtn = new JButton("Manage Students");
        JButton manageQuestionsBtn = new JButton("Manage Questions");
        JButton viewAllResultsBtn = new JButton("View All Results");

        JButton manageAttemptsBtn = new JButton("Manage/Reset Exam Attempts");
        manageAttemptsBtn.addActionListener(e -> {
            new ResetAttemptsFrame();
            dispose();
        });

        JButton setTimeBtn = new JButton("Set Exam Time");
        setTimeBtn.addActionListener(e -> {
            new SetExamTimeFrame();
            dispose();
        });

        JButton logoutBtn = new JButton("Logout");


        // Button styles
        Font btnFont = new Font("Arial", Font.BOLD, 24);
        Dimension btnSize = new Dimension(300, 80);

        JButton[] buttons = { manageStudentsBtn, manageQuestionsBtn, viewAllResultsBtn, manageAttemptsBtn, setTimeBtn, logoutBtn };
        Color[] colors = {
                new Color(255, 99, 71),   // Red
                new Color(60, 179, 113),  // Sea green
                new Color(100, 149, 237), // Light Blue
                new Color(0,0,255),       // Blue
                new Color(128,0,128),     // Purple
                new Color(139, 0, 0)      // Dark red
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

        // Action listeners
        manageStudentsBtn.addActionListener(e -> {
            new ManageStudentsFrame();
            dispose();
        });

        manageQuestionsBtn.addActionListener(e -> {
            new ManageQuestionsFrame();
            dispose();
        });

        viewAllResultsBtn.addActionListener(e -> {
            new ViewAllResultsFrame();
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            new MainWindow();
            dispose();
        });

        // Add root panel to frame
        setContentPane(rootPanel);
        setVisible(true);
    }

    // Fetch name from DB using username
    private String fetchAdminName(String username) {
        String name = username;  // Fallback to username if name not found
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT name FROM admin WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
}
