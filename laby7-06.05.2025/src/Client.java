import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

public class Client {
    private Socket socket;
    private boolean printGiveAnswer = false;
    private int numberOfClientHandlers = 0;
    private Scanner scanner = new Scanner(System.in);
    private String studentName;
    private String studentID;
    //It stores how much time we have for a question, it is useful to notify student about the fact how much time they have.
    //It also helps to manage input threads
    private int timeForQuestionInMilliseconds = 0;
    PrintWriter out;


    public Client() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        this.socket = new Socket("192.168.1.200", 50001);
        this.out = new PrintWriter(socket.getOutputStream());

        String line;

        //Server asks for a name, answer and send reply
        line = waitForMessage();
        System.out.print(line);
        this.studentName = scanner.nextLine();
        sendMessage(studentName);

        //Server asks for a studentID, answer and send reply
        line = waitForMessage();
        System.out.print(line);
        this.studentID = scanner.nextLine();
        sendMessage(studentID);

        line = waitForMessage();
        this.timeForQuestionInMilliseconds = Integer.parseInt(line);
        System.out.println("Exam can be quit with q");
        System.out.println("You have " + timeForQuestionInMilliseconds / 1000 + " seconds for each question!!!!!");

        listenForQuestions();
    }

    /**
     * Blocking function that waits for a message from socket as long as it receives it. It can wait forever if no message is received
     *
     * @return it returns the received message
     **/
    private String waitForMessage() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            return line;
        }
        return "";
    }

    private void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

    /**
     * Here we listen for questions and we will create listener to listen for user's answers.
     **/
    private void listenForQuestions() throws IOException, ExecutionException, InterruptedException, TimeoutException {

        //We wil use this for listening for messages on socket
        InputStream input = socket.getInputStream();

        //It will be useful for reading from console
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        printGiveAnswer = false;

        //Listen for messages all the time
        while (true) {
            //Happens if any message was received
            try {
                //If we receive new message from server, load new question
                if (input.available() > 0) {
                    //Read received data
                    byte[] buffer = new byte[input.available()];
                    int bytesRead = input.read(buffer);
                    //Received refers to the message that it receives from server, it is probably the question and answers
                    String received = new String(buffer, 0, bytesRead);

                    //used to determine if next passed value is question
                    if (received.trim().trim().equals("q")) {
                        printGiveAnswer = true;
                    } else {
                        //Display received message
                        System.out.println(received.trim());
                        if (printGiveAnswer) {
                            System.out.print("Give your answer: ");
                        }
                        printGiveAnswer = false;
                    }

                    //For each input we create a new thread that is responsible for taking an input.
                    //We do it on different thread because of blocking nature of console input
                    if (printGiveAnswer == true) { //question will be sent so we need to read it
                        ClientInputListener clientInputListener = new ClientInputListener(numberOfClientHandlers, br);
                        numberOfClientHandlers++;
                        Thread thread = new Thread(clientInputListener);
                        thread.start();
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception");
                continue;
            }
        }
    }

    /**
     * This class is made for taking an input from user, we want to take input within given interval thus we use it on different thread.
     **/
    private class ClientInputListener implements Runnable {
        private int id = 0;
        BufferedReader br;

        public ClientInputListener(int id, BufferedReader br) {
            this.id = id;
            this.br = br;
        }

        private char answer;

        @Override
        public void run() {

            String user_input = "";

            try {
                br.mark(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //It stores info when a listening thread should be deleted, we don't want to listen after time for question has passed
            long endTime = System.currentTimeMillis() + timeForQuestionInMilliseconds;

            while (System.currentTimeMillis() < endTime) {
                try {
                    if (br.ready()) {
                        sendMessage(br.readLine());
                        break;
                    } else {
                        //System.out.println("dla handlera "+id);
                    }
                } catch (IOException e) {
                    System.out.println("Exception");
                }
            }
            //We are here if time for question has passed or we sent an answer to the question
            //System.out.println("Destroying the clientHandler "+id);
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Client c = new Client();
    }
}