import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private String serverDataFile;
    private String serverIPAdress;
    private int serverPort;
    private ServerSocket serverSocket;
    private Map<Integer,ClientHandler>listOfClients;
    private int numberOfQuestions;
    private Exam exam;
    private int timeForQuestionInMilliseconds = 10000;



    public Server(String serverDataFile) throws FileNotFoundException {
        this.serverDataFile = serverDataFile;
        this.listOfClients = new HashMap<Integer,ClientHandler>();
        this.exam = new Exam("exam1.txt");
    }

    public void startServer() throws IOException {
        this.serverSocket = new ServerSocket(this.serverPort, 0, InetAddress.ofLiteral(this.serverIPAdress));

        while(true){


        Socket clientSocket = serverSocket.accept();
        ClientHandler clientHandler = new ClientHandler(clientSocket,listOfClients.size());
        Thread thread = new Thread(clientHandler);
        thread.start();
        System.out.println("Dolaczyl klient "+listOfClients.size());
        listOfClients.put(listOfClients.size(),clientHandler);
        }

        }


    public void readFromFile(){
        try(BufferedReader br = new BufferedReader(new FileReader(this.serverDataFile))) {

            this.serverIPAdress = br.readLine();
            System.out.println(this.serverIPAdress);

            String serverPortString = br.readLine();
            this.serverPort = Integer.parseInt(serverPortString);

            System.out.println(this.serverPort);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Klasa której obiekt powinien być uruchomiony na osobnym wątku. Jest to klasa reprezentująca wątek klienta obsługiwany przez serwer.
     * Serwer posiada instancję każdego klienta właśnie jako obiekt tej klasy. W skrócie to wątki obsługiwanych klientów.
     * */
    class ClientHandler implements Runnable {
        private Socket socket;
        private int id;
        private int currentQuestion = 0;


        public ClientHandler(Socket socket,int id){
            this.socket = socket;
            this.id = id;
        }

        /***
         * Function that sends <code>msg</code> to the client.
         * @param msg the content of the message to be sent
         * */
        private void sendMessage(String msg) throws IOException {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(msg);
            out.flush();
        }

        @Override
        public void run() {
            String klientRepr = "client " + id;

            for(int i = 0;i<exam.getNumberOfQuestions();i++){
                try {
                    //Get the question and send it to the client so they can display the question and all of the answers
                    Exam.Question question = exam.getQuestion(i);
                    sendMessage(String.valueOf(question));

                    System.out.println("I sent question " +question.getId()+" "+klientRepr);

                    //It throws an Exception (SocketTimeoutException) after given time in miliseconds
                    socket.setSoTimeout(timeForQuestionInMilliseconds);

                    //Wait for the answer, if socket receives an answer go the next iteration of for loop
                    BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("Client "+this.id+": "+line);
                        break;
                    }

                }
                //It happens when user doesn't answer the question within the time
                catch (SocketTimeoutException e){
                    System.out.println(klientRepr+" run out of time");
                }

                //Exception for overal purposes
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server("server_data.txt");
        server.readFromFile();
        server.startServer();
    }

}


