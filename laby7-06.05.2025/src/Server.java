import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private DatabaseManager dbManager;

    public Server(String serverDataFile, DatabaseManager dbManager) throws FileNotFoundException {
        this.serverDataFile = serverDataFile;
        this.dbManager = dbManager;
        this.listOfClients = new ArrayList<>(0);
        this.exam = new Exam(this.dbManager);
        this.numberOfQuestions = exam.getNumberOfQuestions();
    }

    public void readServerConfig() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.serverDataFile))) {
            this.serverIPAdress = InetAddress.getLocalHost();
            String serverPortString = br.readLine();
            this.serverPort = Integer.parseInt(serverPortString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startServer() throws IOException {
        this.serverSocket = new ServerSocket(this.serverPort, 0, this.serverIPAdress);
        int clientId = 0;
        boolean counterMessage = false;

        while (true) {
            if (listOfClients.size() < 250) {
                counterMessage = false;
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientId, this.dbManager, this.exam);
                Thread thread = new Thread(clientHandler);
                thread.start();
                System.out.println("Client (Handler ID: " + clientId + ", IP: " + clientSocket.getInetAddress().getHostAddress() + ") connected.");
                listOfClients.add(thread);
                clientId++;
            } else {
                if (!counterMessage) {
                    counterMessage = true;
                }
            }

            for (int i = 0; i < listOfClients.size(); i++) {
                if (!listOfClients.get(i).isAlive()) {
                    listOfClients.remove(i);
                }
            }
        }
    }

    class ClientHandler implements Runnable {
        private Socket socket;
        private int handlerId;
        //We want to store information about client/student we handle
        private String studentName;
        private String studentID;
        //PrintWriter is used for sending messages via sockets
        private PrintWriter out;
        //Helpful string to print studentsName and ID e.g (Jan Kowalski 253252)
        private String clientRepresentation;
        private int score = 0;
        private Exam examInstance;

        /** It stores answers that user entered e.g
        * { {1:a},{2:c} } which means that user answered 'a' for 1st question and 'c' for 2nd question
        **/
        HashMap<Integer, Character> givenAnswer = new HashMap<>();

        /**
         * It stores information about the corectness of answers e.g if question 3 was incorrect it is stored as key value pair like below:
         * {3:incorrect answer}
         **/
        HashMap<Integer, String> wasAnswerCorrect = new HashMap<>();

        private DatabaseManager clientDbManager;

        public ClientHandler(Socket socket, int handlerId, DatabaseManager dbManager, Exam exam) throws IOException {
            this.socket = socket;
            this.handlerId = handlerId;
            this.clientDbManager = dbManager;
            this.examInstance = exam;
            this.out = new PrintWriter(socket.getOutputStream(), true);
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
         **/
        private String waitForMessage() throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            String line;
            if ((line = in.readLine()) != null) {
                return line;
            }
            return "";
        }

        private void saveAnswersToDB() {
            String sql = "INSERT INTO student_responses (student_name, student_identifier, question_id, question_text, given_answer, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = clientDbManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                boolean batchHasData = false;
                for (int i = 0; i < examInstance.getNumberOfQuestions(); i++) {
                    Exam.Question q = examInstance.getQuestion(i);
                    if (q == null) continue;

                    pstmt.setString(1, this.studentName);
                    pstmt.setString(2, this.studentID);
                    pstmt.setInt(3, q.getId());
                    pstmt.setString(4, q.getTaskInstruction());
                    pstmt.setString(5, String.valueOf(givenAnswer.getOrDefault(q.getId(), '-')));
                    pstmt.setString(6, wasAnswerCorrect.getOrDefault(q.getId(), "User didn't answer"));
                    pstmt.addBatch();
                    batchHasData = true;
                }
                if (batchHasData) {
                    pstmt.executeBatch();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void saveScoreToDB() {
            String sql = "INSERT INTO student_scores (student_name, student_identifier, score) VALUES (?, ?, ?)";
            try (Connection conn = clientDbManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, this.studentName);
                pstmt.setString(2, this.studentID);
                pstmt.setInt(3, this.score);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void handleUserInput(String userInput, Exam.Question question, AtomicBoolean wantsToQuit) throws invalidUserInput {
            char character = '-';
            if (userInput == null || userInput.trim().isEmpty()) {
                return;
            }

            String trimmedInput = userInput.trim().toLowerCase();
            if (trimmedInput.length() > 1) {
                if (Character.isLetter(trimmedInput.charAt(0))) {
                    character = trimmedInput.charAt(0);
                } else {
                    return;
                }
            } else {
                character = trimmedInput.charAt(0);
            }

            switch (character) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                    givenAnswer.put(question.getId(), character);
                    if (question.checkAnswer(character)) {
                        wasAnswerCorrect.put(question.getId(), "Correct");
                        score++;
                    } else {
                        wasAnswerCorrect.put(question.getId(), "Incorrect");
                    }
                    break;
                case 'q':
                    wantsToQuit.set(true);
                    if (!givenAnswer.containsKey(question.getId()) || givenAnswer.get(question.getId()) == '-') {
                        wasAnswerCorrect.put(question.getId(), "User didn't answer");
                        givenAnswer.put(question.getId(), '-');
                    }
                    break;
            }
        }

        @Override
        public void run() {
            try {
                sendMessage("Enter your name: ");
                this.studentName = waitForMessage();
                sendMessage("Enter your studentID: ");
                this.studentID = waitForMessage();
                sendMessage(String.valueOf(timeForQuestionInMilliseconds));
                this.clientRepresentation = this.studentName + " (" + this.studentID + ")";
                System.out.println("Client " + clientRepresentation + " (Handler ID: " + handlerId + ") initialized.");

                for (int i = 0; i < examInstance.getNumberOfQuestions(); i++) {
                    Exam.Question q = examInstance.getQuestion(i);
                    if (q != null) {
                        givenAnswer.put(q.getId(), '-');
                        wasAnswerCorrect.put(q.getId(), "User didn't answer");
                    }
                }

                try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                AtomicBoolean wantsToQuit = new AtomicBoolean(false);

                for (int i = 0; i < examInstance.getNumberOfQuestions(); i++) {
                    Exam.Question currentQuestion = examInstance.getQuestion(i);
                    if (currentQuestion == null) {
                        continue;
                    }

                    sendMessage("q");
                    try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break;}

                    sendMessage(String.valueOf(currentQuestion));

                    socket.setSoTimeout(timeForQuestionInMilliseconds);
                    String clientInput = "";
                    try {
                        clientInput = waitForMessage();
                        handleUserInput(clientInput, currentQuestion, wantsToQuit);
                    } catch (SocketTimeoutException e) {
                        sendMessage("Time for question is up!");
                    }

                    if (wantsToQuit.get()) {
                        break;
                    }
                }

                sendMessage("The test has ended");
                sendMessage("Your score is: " + score);
                saveAnswersToDB();
                saveScoreToDB();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.initializeDatabase();
            dbManager.populateQuestionsFromFileIfEmpty("bazaPytan.txt");

            Server server = new Server("server_data.txt", dbManager);
            server.readServerConfig();
            server.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}