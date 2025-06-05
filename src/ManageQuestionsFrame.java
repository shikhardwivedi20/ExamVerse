import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageQuestionsFrame extends JFrame {
    private JTextField questionField, option1Field, option2Field, option3Field, option4Field, correctOptionField;
    private JTable questionsTable;
    private DefaultTableModel tableModel;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn, backBtn;

    private final Color rootColor = new Color(0xD3D3D3);
    private final Font labelFont = new Font("Times New Roman", Font.BOLD, 28);
    private final Font fieldFont = new Font("Times New Roman", Font.PLAIN, 24);

    public ManageQuestionsFrame() {
        setTitle("Manage Questions - ExamVerse");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(rootColor);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(rootColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== Title Label =====
        JLabel titleLabel = new JLabel("Manage Questions", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 60));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        // Input Form Panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBackground(rootColor);

        questionField = createStyledTextField();
        option1Field = createStyledTextField();
        option2Field = createStyledTextField();
        option3Field = createStyledTextField();
        option4Field = createStyledTextField();
        correctOptionField = createStyledTextField();

        formPanel.add(createStyledLabel("Question:"));      formPanel.add(questionField);
        formPanel.add(createStyledLabel("Option 1:"));      formPanel.add(option1Field);
        formPanel.add(createStyledLabel("Option 2:"));      formPanel.add(option2Field);
        formPanel.add(createStyledLabel("Option 3:"));      formPanel.add(option3Field);
        formPanel.add(createStyledLabel("Option 4:"));      formPanel.add(option4Field);
        formPanel.add(createStyledLabel("Correct Option (1-4):")); formPanel.add(correctOptionField);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        btnPanel.setOpaque(false);
        btnPanel.setBackground(rootColor);

        addBtn = new JButton("Add Question");
        updateBtn = new JButton("Update Selected");
        deleteBtn = new JButton("Delete Selected");
        refreshBtn = new JButton("Refresh Table");

        styleButton(addBtn, new Color(0, 204, 102));
        styleButton(updateBtn, new Color(30, 144, 255));
        styleButton(deleteBtn, new Color(255, 69, 58));
        styleButton(refreshBtn, new Color(255, 165, 0));

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        // Table Section
        tableModel = new DefaultTableModel(new String[]{"ID", "Question", "Option1", "Option2", "Option3", "Option4", "Correct Option"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        questionsTable = new JTable(tableModel);
        questionsTable.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        questionsTable.setOpaque(false);
        questionsTable.setRowHeight(28);
        JScrollPane tableScroll = new JScrollPane(questionsTable);

        // Load Data + Listeners
        addBtn.addActionListener(e -> addQuestion());
        updateBtn.addActionListener(e -> updateQuestion());
        deleteBtn.addActionListener(e -> deleteQuestion());
        refreshBtn.addActionListener(e -> loadQuestions());

        questionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && questionsTable.getSelectedRow() != -1) {
                int row = questionsTable.getSelectedRow();
                questionField.setText(tableModel.getValueAt(row, 1).toString());
                option1Field.setText(tableModel.getValueAt(row, 2).toString());
                option2Field.setText(tableModel.getValueAt(row, 3).toString());
                option3Field.setText(tableModel.getValueAt(row, 4).toString());
                option4Field.setText(tableModel.getValueAt(row, 5).toString());
                correctOptionField.setText(tableModel.getValueAt(row, 6).toString());
            }
        });

        loadQuestions();

        // === Combine Form and Buttons into one Panel ===
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(btnPanel, BorderLayout.CENTER);

        // === Wrap titleLabel + topPanel ===
        JPanel northWrapperPanel = new JPanel();
        northWrapperPanel.setLayout(new BoxLayout(northWrapperPanel, BoxLayout.Y_AXIS));
        northWrapperPanel.setBackground(rootColor);
        northWrapperPanel.add(titleLabel);
        northWrapperPanel.add(topPanel);

        // === Add all to mainPanel ===
        mainPanel.add(northWrapperPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        // ==== Bottom Panel with Back Button ====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            new AdminPanel("admin1"); // Use correct username
            dispose();
        });

        // Final setup
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(fieldFont);
        return field;
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Times New Roman", Font.BOLD, 24));
        button.setFocusPainted(false);
    }

    private void loadQuestions() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM questions")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4"),
                        rs.getInt("correct_option")
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading questions: " + ex.getMessage());
        }
    }

    private void addQuestion() {
        String qText = questionField.getText().trim();
        String o1 = option1Field.getText().trim();
        String o2 = option2Field.getText().trim();
        String o3 = option3Field.getText().trim();
        String o4 = option4Field.getText().trim();
        String correctStr = correctOptionField.getText().trim();

        if (qText.isEmpty() || o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty() || correctStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try {
            int correct = Integer.parseInt(correctStr);
            if (correct < 1 || correct > 4) throw new NumberFormatException();

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, qText);
                ps.setString(2, o1);
                ps.setString(3, o2);
                ps.setString(4, o3);
                ps.setString(5, o4);
                ps.setInt(6, correct);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Question added successfully.");
                clearForm();
                loadQuestions();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Correct option must be between 1 and 4.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding question: " + ex.getMessage());
        }
    }

    private void updateQuestion() {
        int row = questionsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a question to update.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String qText = questionField.getText().trim();
        String o1 = option1Field.getText().trim();
        String o2 = option2Field.getText().trim();
        String o3 = option3Field.getText().trim();
        String o4 = option4Field.getText().trim();
        String correctStr = correctOptionField.getText().trim();

        if (qText.isEmpty() || o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty() || correctStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try {
            int correct = Integer.parseInt(correctStr);
            if (correct < 1 || correct > 4) throw new NumberFormatException();

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE questions SET question_text=?, option1=?, option2=?, option3=?, option4=?, correct_option=? WHERE id=?")) {
                ps.setString(1, qText);
                ps.setString(2, o1);
                ps.setString(3, o2);
                ps.setString(4, o3);
                ps.setString(5, o4);
                ps.setInt(6, correct);
                ps.setInt(7, id);

                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Question updated successfully.");
                    clearForm();
                    loadQuestions();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Correct option must be between 1 and 4.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating question: " + ex.getMessage());
        }
    }

    private void deleteQuestion() {
        int row = questionsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a question to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this question?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM questions WHERE id = ?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Question deleted successfully.");
                clearForm();
                loadQuestions();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting question: " + ex.getMessage());
        }
    }

    private void clearForm() {
        questionField.setText("");
        option1Field.setText("");
        option2Field.setText("");
        option3Field.setText("");
        option4Field.setText("");
        correctOptionField.setText("");
        questionsTable.clearSelection();
    }
}
