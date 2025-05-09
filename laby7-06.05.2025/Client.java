import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private boolean userHasAnswered = false;



    public Client() throws IOException {
        this.socket = new Socket("10.10.10.124",50001);
        listenForQuestions();

    }

    private void sendMessage(String msg) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println(msg);
        out.flush();
    }

    private void listenForQuestions() throws IOException {

        InputStream input = socket.getInputStream();
        ClientInputListener clientInputListener = new ClientInputListener();
        Thread thread = new Thread(clientInputListener);
        thread.start();

        //Listen for messages all the time
        while (true) {
            //Happens if any message was received
            if (input.available() > 0) {
                thread.interrupt();
                userHasAnswered = false;
                //Read received data
                byte[] buffer = new byte[input.available()];
                int bytesRead = input.read(buffer);
                //Received refers to the message that it receives from server, it is probably the question and answers
                String received = new String(buffer, 0, bytesRead);

                //Display received message
                System.out.println(received);
                System.out.print("Podaj odpowiedz: ");
            }


        }

    }

    /**THIS CODE IS TO BE CORRECTED, it causes THREAD leakage, not all threads are deleted!*/
    private class ClientInputListener implements Runnable{
        private char answer;
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            long maxTime = 10000; // 5 seconds
                while(System.currentTimeMillis() - start < maxTime){
                        try {

                            //Create a scanner get character from user
                            Scanner scanner = new Scanner(System.in);
                            char answer = scanner.next().charAt(0);
                            System.out.println("Wpisales: "+answer+", wysylam podana odpowiedz do serwera");
                            //Send server the answers
                            sendMessage("Odpowiedz "+answer);
                            userHasAnswered = true;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }




                }


        }
    }



    public static void main(String[] args) throws IOException, InterruptedException {
        Client c = new Client();


    }

}