import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DatabaseManager {
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_NAME = "exam";

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL_SERVER = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_URL_DATABASE = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public DatabaseManager() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Please add it to your classpath.", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL_DATABASE, DB_USER, DB_PASSWORD);
    }

    public void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL_SERVER, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        } catch (SQLException e) {
            System.err.println("Error creating database '" + DB_NAME + "': " + e.getMessage());
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createQuestionsTableSQL = "CREATE TABLE IF NOT EXISTS questions ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "task_instruction TEXT NOT NULL, "
                    + "answer_a VARCHAR(255) NOT NULL, "
                    + "answer_b VARCHAR(255) NOT NULL, "
                    + "answer_c VARCHAR(255) NOT NULL, "
                    + "answer_d VARCHAR(255) NOT NULL, "
                    + "correct_answer CHAR(1) NOT NULL"
                    + ")";
            stmt.executeUpdate(createQuestionsTableSQL);

            String createStudentResponsesTableSQL = "CREATE TABLE IF NOT EXISTS student_responses ("
                    + "response_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "student_name VARCHAR(255) NOT NULL, "
                    + "student_identifier VARCHAR(255) NOT NULL, "
                    + "question_id INT, "
                    + "question_text TEXT NOT NULL, "
                    + "given_answer CHAR(1), "
                    + "status VARCHAR(20) NOT NULL, "
                    + "FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE SET NULL"
                    + ")";
            stmt.executeUpdate(createStudentResponsesTableSQL);

            String createStudentScoresTableSQL = "CREATE TABLE IF NOT EXISTS student_scores ("
                    + "score_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "student_name VARCHAR(255) NOT NULL, "
                    + "student_identifier VARCHAR(255) NOT NULL, "
                    + "score INT NOT NULL, "
                    + "exam_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            stmt.executeUpdate(createStudentScoresTableSQL);

        } catch (SQLException e) {
            throw new RuntimeException("Error initializing tables in database '" + DB_NAME + "': " + e.getMessage(), e);
        }
    }

    public void populateQuestionsFromFileIfEmpty(String filePath) {
        if (isTableEmpty("questions")) {
            String sql = "INSERT INTO questions (task_instruction, answer_a, answer_b, answer_c, answer_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("---") || line.contains(": ")) {
                        continue;
                    }
                    String[] parts = line.split(";", -1);
                    if (parts.length == 6) {
                        pstmt.setString(1, parts[0]);
                        pstmt.setString(2, parts[1]);
                        pstmt.setString(3, parts[2]);
                        pstmt.setString(4, parts[3]);
                        pstmt.setString(5, parts[4]);
                        pstmt.setString(6, parts[5]);
                        pstmt.addBatch();
                        count++;
                    }
                }
                if (count > 0) {
                    pstmt.executeBatch();
                }
            } catch (IOException e) {
                System.err.println("Error reading questions file '" + filePath + "': " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Error inserting questions into database: " + e.getMessage());
            }
        }
    }

    private boolean isTableEmpty(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}