import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "rownania.txt"; // Ścieżka do pliku z równaniami
        ReentrantLock lock = new ReentrantLock();

        try {
            List<String> equations = readEquationsFromFile(filePath);
            int lines = equations.size(); //liczba linii w pliku - 1 linia 2 thready
            List<Thread> threads = new ArrayList<>();
            ExecutorService taskExecutor = Executors.newCachedThreadPool(); //nielimitowana liczba wątków dla obliczenia i zapisu równań

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

    //funkcja zczytująca plik - drastycznie upraszcza zapisywanie zmian jesli po prostu nadpiszemy cały plik
    private static List<String> readEquationsFromFile(String filePath) throws IOException {
        List<String> equations = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                equations.add(line.trim());
            }
        }
        reader.close();
        return equations;
    }
}