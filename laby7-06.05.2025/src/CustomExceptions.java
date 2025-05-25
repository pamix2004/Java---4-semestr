/**
 * This exception should be throw when a line inside a file is invalid e.g file is supposed to be in format
 * TASK;ANSWER_A;ANSWER_B;ANSWER_C,ANSWER_D;CORRECT_ANSWER(as a character e.g 'a'). If its not in that format throw an exception
 * */
class InvalidLineInTextFile extends RuntimeException {
    public InvalidLineInTextFile(String message) {
        super(message);
    }
}

class invalidUserInput extends RuntimeException{
   public invalidUserInput(String message){
       super(message);
   }
}