import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class Exam {
    // HashMap of all questions, key is an ID of question while the value is the question itself.
    HashMap<Integer, Question> allQuestions = new HashMap<>();

    // Number of questions,useful so each question will be assigned with unique ID
    private int numberOfQuestions = 0;

    private ArrayList<Integer> questionOrder = new ArrayList<>();
    private DatabaseManager dbManager;

    /**
     * Constructor of Exam
     * @param dbManager The DatabaseManager instance to fetch questions.
     */
    public Exam(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        loadQuestionsFromDB();
    }

    private void loadQuestionsFromDB() {
        String sql = "SELECT id, task_instruction, answer_a, answer_b, answer_c, answer_d, correct_answer FROM questions ORDER BY id"; // Or any other desired order
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            allQuestions.clear();
            questionOrder.clear();
            numberOfQuestions = 0;

            while (rs.next()) {
                int dbId = rs.getInt("id");
                String task = rs.getString("task_instruction");
                String ansA = rs.getString("answer_a");
                String ansB = rs.getString("answer_b");
                String ansC = rs.getString("answer_c");
                String ansD = rs.getString("answer_d");
                char correctAns = rs.getString("correct_answer").charAt(0);

                Question q = new Question(dbId, task, new String[]{ansA, ansB, ansC, ansD}, correctAns);
                allQuestions.put(dbId, q);
                questionOrder.add(dbId);
                numberOfQuestions++;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Critical error loading questions from database: " + e.getMessage(), e);
        }
    }

    public int getNumberOfQuestions() {
        return this.numberOfQuestions;
    }

    public Question getQuestion(int sequenceNumber) {
        if (sequenceNumber < 0 || sequenceNumber >= questionOrder.size()) {
            return null;
        }
        int dbId = questionOrder.get(sequenceNumber);
        return allQuestions.get(dbId);
    }

    public class Question {
        private String[] answerList = new String[4];
        private String taskInstruction = "";
        private int id;
        private char correctAnswer = ' ';

        public Question(int dbId, String task, String[] answers, char correctAns) {
            this.id = dbId;
            this.taskInstruction = task;
            if (answers != null && answers.length == 4) {
                this.answerList[0] = answers[0];
                this.answerList[1] = answers[1];
                this.answerList[2] = answers[2];
                this.answerList[3] = answers[3];
            }
            this.correctAnswer = correctAns;
        }

        public boolean checkAnswer(char answer) {
            return Character.toLowerCase(answer) == Character.toLowerCase(correctAnswer);
        }

        @Override
        public String toString() {
            return taskInstruction + "\n" +
                    "a) " + answerList[0] + "\n" +
                    "b) " + answerList[1] + "\n" +
                    "c) " + answerList[2] + "\n" +
                    "d) " + answerList[3] + "\n";
        }

        public int getId() {
            return id;
        }

        public String getTaskInstruction() {
            return taskInstruction;
        }
    }
}