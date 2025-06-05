# 🎓 ExamVerse – Online Examination Portal

**Author:** Shikhar Dwivedi  
**Tech Stack:** Java (Swing GUI), SQLite (JDBC)

ExamVerse is a powerful desktop-based online examination system designed with a user-friendly GUI in Java Swing. It supports both **Admin** and **Student** roles with functionalities like registration, exam attempts, result tracking, and full admin control over questions and students.

---

## 🧩 Features

### 👨‍🏫 Admin Panel
- Register/Login system
- Add, update, delete student records
- Add, update, delete MCQ-based questions
- View students' recent exam results
- Set exam duration (time limit)
- Reset or manage exam attempts for all/individual students

### 🎓 Student Panel
- Secure login
- Attempt exam (MCQ-based with shuffling)
- View most recent result
- Timer-based submission and scoring system
- Limited attempt logic (custom per student)

---

## 🔧 Technologies Used

- **Java Swing** – For designing modern desktop GUI
- **JDBC** – To connect Java with SQLite database
- **SQLite** – Lightweight embedded database for local storage
- **OOP Concepts** – For modularity and maintainability

---

## 🛠️ Installation Instructions

1. **Clone this repository:**

   ```bash
   git clone https://github.com/shikhardwivedi20/ExamVerse.git
   cd ExamVerse
   ```

2. **Compile the code:**

   - Use any IDE like IntelliJ or VS Code with Java extension
   - Make sure `sqlite-jdbc` JAR is in the classpath

3. **Run the Main Class:**

   ```java
   public static void main(String[] args) {
       DatabaseInit.initialize();  // Creates necessary tables
       new MainWindow();           // Launches the GUI
   }
   ```

---

## 🗂️ Folder Structure

```
/src
 ├── AdminAuthFrame.java
 ├── StudentAuthFrame.java
 ├── AdminPanel.java
 ├── StudentPanel.java
 ├── ManageStudentsFrame.java
 ├── ManageQuestionsFrame.java
 ├── ExamFrame.java
 ├── ResultFrame.java
 ├── ResetAttemptsFrame.java
 ├── SetExamTimeFrame.java
 ├── DBConnection.java
 ├── DatabaseInit.java
 └── MainWindow.java
```

---

## 🧬 Database Schema (SQLite)

### Tables Created

- `admin (id, username, name, password)`
- `student (id, roll_no, name, username, password, allowed_attempts)`
- `questions (id, question_text, option1, option2, option3, option4, correct_option)`
- `exam_attempts (attempt_id, student_id, score, attempt_time)`
- `exam_config (id, duration_minutes)`

---

## 🧪 How to Use

### For Admin:
- Register/Login
- Add students and questions
- Set exam time, attempt limits
- Monitor and reset attempts or view results

### For Students:
- Login with valid credentials
- Attempt available exams
- Get instant results after submission
- View latest result anytime

---

## 🧑‍💻 Author

**SHIKHAR DWIVEDI**  

---

