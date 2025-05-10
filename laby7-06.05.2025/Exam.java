import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Exam {
    //HashMap of all questions, key is an ID of question while the value is the question itself.
    HashMap<Integer,Question> allQuestions = new HashMap<>();
    //Number of questions,useful so each question will be assigned with unique ID
    private int numberOfQuestions = 0;


    /**
     * Constructor of Exam
     * @param path path to the exam file
     * */
    public Exam(String path) throws FileNotFoundException {
        Scanner in = new Scanner(new FileReader(path));

        //Let's read lines in a text file and make questions (each line is a single questions with 4 answers and correct answer)
        while (in.hasNextLine()) {
            String line = in.nextLine();
            addQuestion(line,numberOfQuestions);
            numberOfQuestions++;
        }




    }

    public int getNumberOfQuestions(){
        return this.numberOfQuestions;
    }

    public Question getQuestion(int number){
        return allQuestions.get(number);
    }


    /**
     *
     *  Helper function to add a question, read question from line and adds it into allQuestions hashmap.
     *  @param line line to be parsed
     *  @id desired id of questions
     * */
    private void addQuestion(String line,int id){
        Question q = new Question(line,id);
        allQuestions.put(q.id,q);
    }


    public class Question{
        //It stores the 4 possible answers
        private String answerList[] = new String[4];
        //It stores the given task (po polsku po prostu polecenie zadania)
        private String taskInstruction = "";
        //id of question
        private int id;
        //stores the correct answer to the questions
        private char correctAnswer=' ';


        //Question is in format
        /**
         *
         *TASK;ANSWER_A;ANSWER_B;ANSWER_C,ANSWER_D;CORRECT_ANSWER(as a character e.g 'a')
         *
         * */
        public Question(String line,int id)
        {
            this.id = id;
            String[] parts = getValuesFromLine(line);
            taskInstruction = parts[0];
            answerList[0] = parts[1];
            answerList[1] = parts[2];
            answerList[2] = parts[3];
            answerList[3] = parts[4];
            correctAnswer  = parts[5].charAt(0);
        }

        /**
         * Function that returns if the given answer is correct answer to the question
         * @param answer answer given by user
         * **/
        public boolean checkAnswer(char answer){
            return answer==correctAnswer;
        }

        /**
         * It display the questions as the task and possible answers. Useful for sending to client and not having to do any serialization.
         * **/
        @Override
        public String toString() {
            return taskInstruction + "\n" +
                    "a) " + answerList[0] + "\n" +
                    "b) " + answerList[1] + "\n" +
                    "c) " + answerList[2] + "\n" +
                    "d) " + answerList[3] + "\n";
        }


        /**
        * Helper function that helps to return the array of values separated by semicolon(;)
        * */
        private static String[] getValuesFromLine(String line)throws InvalidLineInTextFile{
            String[] parts = line.split(";",-1);
            if(parts.length!=6){
                throw new InvalidLineInTextFile("Problem z linijka:"+line);
            }
            return parts;
        }

        public int getId() {
            return id;
        }

        public String getTaskInstruction() {
            return taskInstruction;
        }
    }
}
