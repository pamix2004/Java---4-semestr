import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private String serverDataFile;
    private InetAddress serverIPAdress;
    private int serverPort;
    private ServerSocket serverSocket;
    private ArrayList<Thread> listOfClients;
    private int numberOfQuestions;
    private Exam exam;
    private final int timeForQuestionInMilliseconds = 10000;
    private String pathToFileWithStudentsAnswers;
    private String pathToScores = "scores.txt";



    public Server(String serverDataFile) throws FileNotFoundException {
        this.serverDataFile = serverDataFile;
        this.listOfClients = new ArrayList<Thread>(0);
        this.exam = new Exam("exam1.txt");
        this.pathToFileWithStudentsAnswers="answers.txt";

        this.numberOfQuestions = exam.getNumberOfQuestions();
    }

    public void startServer() throws IOException {
        this.serverSocket = new ServerSocket(this.serverPort, 0, this.serverIPAdress);
        int clientId = 0; //counter for numbering clients (using list.size()) would cause issues with numbering after 250th)
        boolean counterMessage = false; //so that too many clients message is shown once
        while(true){

        //if we don't have 250 clients we can add more
        if(listOfClients.size()<250) {
            counterMessage=false;
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, clientId);
            Thread thread = new Thread(clientHandler);
            thread.start();
            System.out.println("Dolaczyl klient " + clientId);
            //we look for an empty spot in our array
            listOfClients.add(thread);
            clientId++;
        }
        else{
            if(counterMessage==false) {
                System.out.println("client limit reached!");
                counterMessage=true;
            }
        }
        //when server is full, or someone new is added it checks for threads that are done and we can delete, freeing up space for new clients
        for(int i=0; i<listOfClients.size(); i++)
        {
            if(!listOfClients.get(i).isAlive())
            {
                listOfClients.remove(i);
            }
        }

        }

    }

        
     /**
      * It reads config from the file given in <code>serverDataFile</code> that was passed in constructor.
      * **/   
    public void readServerConfig(){
        try(BufferedReader br = new BufferedReader(new FileReader(this.serverDataFile))) {

            this.serverIPAdress = InetAddress.getLocalHost();
            System.out.println(this.serverIPAdress);

            String serverPortString = br.readLine();
            this.serverPort = Integer.parseInt(serverPortString);

            System.out.println(this.serverPort);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *It is responsible for handling each user on server side.
     * */
    class ClientHandler implements Runnable {
        private Socket socket;
        private int id;
        //Current question that is being asked
        private int currentQuestion = 0;
        //We want to store information about client/student we handle
        private String studentName;
        private String studentID;
        //PrintWriter is used for sending messages via sockets
        private PrintWriter out;
        //Helpful string to print studentsName and ID e.g (Jan Kowalski 253252)
        private  String clientRepresentation;
        //Self-explanatory
        private int score = 0;


        /*It stores answers that user entered e.g
        { {1:a},{2:c} } which means that user answered 'a' for 1st question and 'c' for 2nd question
        */
        HashMap<Integer,Character> givenAnswer = new HashMap<>();

        /**
         * It stores information about the corectness of answers e.g if question 3 was incorrect it is stored as key value pair like below:
         * {3:incorrect answer}
         * */
        HashMap<Integer,String> wasAnswerCorrect = new HashMap<>();

        /**
         * Constructor of server
         * @param socket socket connection between server and client
         * @param id id of clientHandler, not very useful, mainly for debugging 
         * */
        public ClientHandler(Socket socket,int id) throws IOException {
            this.socket = socket;
            this.id = id;
            this.out = new PrintWriter(socket.getOutputStream());
        }

        /***
         * Function that sends <code>msg</code> to the client.
         * @param msg the content of the message to be sent
         * */
        private void sendMessage(String msg) {
            out.println(msg);
            out.flush();
        }

        /**
         * Blocking function that waits for a message from socket as long as it receives it. It can wait forever if no message is received
         * @return it returns the received message
         * **/
        private String waitForMessage() throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            String line;
            if ((line = in.readLine()) != null) {
                return line;
            }
            return "";
        }

        /**
         * It saves answers of a client in a file in format of (question;given_answer;correct/incorrect)
         * **/
        private void saveAnswersInFile() throws IOException {
            SaveAnswers.saveAnswersInFile(pathToFileWithStudentsAnswers, studentName, studentID, numberOfQuestions, exam, givenAnswer, wasAnswerCorrect);
        }


        /**
         * It saves only scores of clients
         * **/
        private void saveScore() throws IOException {
            SaveAnswers.saveScore(pathToScores, studentName, studentID, score);
        }


        /**
         * It handles the input sent by client. It decides if given input was a correct answer or not.
         * **/
        private void handleUserInput(String userInput,Exam.Question question, AtomicBoolean wantsToQuit) throws invalidUserInput{
            char character = '-';
            if (userInput.length()>1)
                character = '-'; //worked better than exception below
                // throw new invalidUserInput("User input is too long, it's expected to be single character");


            try {
                character = userInput.charAt(0);
            } catch (StringIndexOutOfBoundsException e)
            {
                character = '-';
            }


            switch (character){
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                    givenAnswer.put(question.getId(),character);
                    if(question.checkAnswer(character)){
                        //System.out.println("Poprawna odpowiedz!");
                        wasAnswerCorrect.put(question.getId(),"Correct");
                        score++;
                    }
                    else{
                        //System.out.println("Nieoprawna odpowiedz!");
                        wasAnswerCorrect.put(question.getId(),"Incorrect");
                    }
                    break;
                    //It happens if user wants to quit
                case 'q':
                    //System.out.println("User wants to stop writing an exam");
                    wantsToQuit.set(true);
                    break;
            }

        }

        @Override
        public void run() {



            try {

                //Ask client about their name
                sendMessage("Enter your name: ");
                this.studentName = waitForMessage();
                //Ask client about their ID
                sendMessage("Enter your studentID: ");
                this.studentID = waitForMessage();

                sendMessage(String.valueOf(timeForQuestionInMilliseconds));


                clientRepresentation = this.studentName + " " + this.studentID;

            }catch (SocketException e)
            {
                System.out.println("client abruptly disconnected");
                return;
            }
             catch (IOException e) {
                throw new RuntimeException(e);
            }
            AtomicBoolean wantsToQuit = new AtomicBoolean(false);
            Exam.Question question=null;

            //if exam is ended prematurely the values are not null because of this
            for(int i = 0; i< exam.getNumberOfQuestions(); i++)
            {
                question = exam.getQuestion(i);
                givenAnswer.put(question.getId(),'-');
                wasAnswerCorrect.put(question.getId(),"User didn't answer");
            }
            //used to fix questions not appearing
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Thread control sleep was interrupted!");
            }
            for(int i = 0;i<exam.getNumberOfQuestions();i++){
                try {
                    //an information that a question will be sent so it can print the give answer thing
                    sendMessage("q");
                    //client needs time to comprehend the q, no answer message breaks without it for some godforsaken reason
                    //makes the thing overall stabler
                    Thread.sleep(50);
                    //Get the question and send it to the client so they can display the question and all of the answers
                    question = exam.getQuestion(i);
                    sendMessage(String.valueOf(question));



                    //System.out.println("I sent question to " +clientRepresentation);

                    //It throws an Exception (SocketTimeoutException) after given time in miliseconds
                    socket.setSoTimeout(timeForQuestionInMilliseconds);

                    //Wait for the answer, if socket receives an answer go the next iteration of for loop
                    String client_input = waitForMessage();

                    //System.out.println(clientRepresentation+" sent an answer: "+client_input);


                    handleUserInput(client_input, question, wantsToQuit);
                    //exam is ended if client wants to quit
                    if(wantsToQuit.get())
                    {
                        break;
                    }
                }
                //It happens when user doesn't answer the question within the time
                catch (SocketTimeoutException e){
                        sendMessage("Time for question is up!");
                }

                catch (SocketException e)
                {
                    System.out.println("client abruptly disconnected");
                    return;
                }
                //Exception for overal purposes
                catch (IOException e) {
                    throw new RuntimeException(e);
                }



                catch(InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }

            System.out.println(clientRepresentation+" has ended exam with score of: "+score);
            sendMessage("The test has ended");
            sendMessage("Your score is: " + score);


            try {
                socket.close(); //closing socket since test has finished
                saveAnswersInFile();
                saveScore();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server("server_data.txt");
        server.readServerConfig();
        server.startServer();
    }

}


