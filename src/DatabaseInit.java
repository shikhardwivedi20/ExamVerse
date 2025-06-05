import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInit {
    public static void initialize() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Admin Table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS admin (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    name TEXT NOT NULL,
                    password TEXT NOT NULL
                );
            """);

            // Student Table (Added 'allowed_attempts' with default = 3)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS student (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    roll_no TEXT UNIQUE NOT NULL,
                    name TEXT NOT NULL,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    allowed_attempts INTEGER DEFAULT 3
                );
            """);

            // Questions Table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    question_text TEXT NOT NULL,
                    option1 TEXT NOT NULL,
                    option2 TEXT NOT NULL,
                    option3 TEXT NOT NULL,
                    option4 TEXT NOT NULL,
                    correct_option INTEGER NOT NULL
                );
            """);

            // Exam Attempts Table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS exam_attempts (
                    attempt_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    score INTEGER NOT NULL,
                    attempt_time DATETIME NOT NULL,
                    FOREIGN KEY(student_id) REFERENCES student(id)
                );
            """);

            // Exam Configuration Table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS exam_config (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    duration_minutes INTEGER NOT NULL
                );
            """);

            // Insert default value into exam_config if table is empty
            stmt.executeUpdate("""
                INSERT INTO exam_config (duration_minutes)
                SELECT 5
                WHERE NOT EXISTS (SELECT 1 FROM exam_config);
            """);

            System.out.println("Database tables created/verified successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
