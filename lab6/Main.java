import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        String filePath = "rownania.txt"; // Ścieżka do pliku z równaniami
        ReentrantLock lock = new ReentrantLock();

        try {

            int lines = countLines(filePath);
            ExecutorService taskExecutor = Executors.newFixedThreadPool(lines); //liczba wątków dla obliczenia i zapisu równań

            for (int i=0; i<lines; i++) {
                //tworzenie nowego wątku do zczytania linii tekstu
                ExecutorService readerExecutor = Executors.newSingleThreadExecutor();
                //dzięki future możemy czekać na koniec zadania poprzez .get()
                Future<String> futureEquation = readerExecutor.submit(new fileRead(lock, filePath, i));
                // Czekamy z dalszym wykonaniem na wynik odczytania by wątek obliczający miał kompletne równanie
                readerExecutor.shutdown(); //kończymy pracę executora od zczytywania - kończy wykonywane zadania
                String equation = futureEquation.get();

                // Tworzymy callable task do przetworzenia równania skoro już je mamy
                EquationProcessorCallable calculatorThread = new EquationProcessorCallable(equation);
                // Tworzymy FutureTask odpowiedzialny za zapis wyniku, który opakowuje callable task
                EquationProcessorTask task = new EquationProcessorTask(calculatorThread, equation, filePath, lock);

                //dodajemy do executora
                taskExecutor.submit(task);


            }

            //czeka aż taski się skończą wykonywać i nie przyjmuje nowych
            taskExecutor.shutdown();

            System.out.println("Wszystkie równania zostały przetworzone.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funkcja zliczająca liczby linii w pliku
    private static int countLines(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        int lineCount = 0;

        // Liczymy liczbę linii w pliku
        while ((reader.readLine()) != null) {
            lineCount++;
        }

        reader.close();
        return lineCount;
    }
}