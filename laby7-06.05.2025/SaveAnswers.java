import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

//separate class necessary for locks to work
public class SaveAnswers{
    private static final ReentrantLock answersLock = new ReentrantLock();
    private static final ReentrantLock scoresLock = new ReentrantLock();

    public static void saveAnswersInFile(String pathToFileWithStudentsAnswers,
                                         String studentName,
                                         String studentID,
                                         int numberOfQuestions,
                                         Exam exam,
                                         HashMap<Integer,Character> givenAnswers,
                                         HashMap<Integer,String> wasAnswerCorrect) {
        answersLock.lock();
        try (PrintWriter pw = new PrintWriter(new FileWriter(pathToFileWithStudentsAnswers, true))) {
            pw.println(studentName + "," + studentID + ": ");
            for (int i = 0; i < numberOfQuestions; i++) {
                String task = exam.getQuestion(i).getTaskInstruction();
                pw.println(task + ";" + givenAnswers.get(i) + ";" + wasAnswerCorrect.get(i));
            }
            pw.println("-------");
        } catch (Exception e) {
            System.out.println("Exception when saving answers to file");
        } finally {
            answersLock.unlock();
        }
    }

    public static void saveScore(String pathToScores,
                                 String studentName,
                                 String studentID,
                                 int score) {
        scoresLock.lock();
        try (PrintWriter pw = new PrintWriter(new FileWriter(pathToScores, true))) {
            pw.println(studentName + "," + studentID + ", score: " + score);
        } catch (IOException e) {
            System.out.println("Exception when saving score to file");
        } finally {
            scoresLock.unlock();
        }
    }
}
