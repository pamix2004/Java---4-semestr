import java.util.concurrent.Callable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.locks.Lock;

public class fileRead implements Callable<String> {
    private String equation;
    private final Lock lock;
    String filePath;
    int lineIndex;

    public fileRead(Lock lock, String filePath, int lineIndex) {
        this.equation = "";
        this.lock = lock;
        this.filePath = filePath;
        this.lineIndex = lineIndex;
    }

    @Override
    public String call() {
        lock.lock();
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                // Przesuwamy się do linii przypisanej temu wątkowi
                for (int i = 0; i < lineIndex; i++) {
                    reader.readLine(); // Odczytujemy i ignorujemy linie do tej, która nas interesuje
                }

                // Odczytujemy jedną linię
                equation = reader.readLine();
                if (equation != null) {
                    // Wątek zapisuje linię
                    System.out.println("odczytano linię: " + equation);
                    return equation;
                }

            } catch (IOException e) {
                System.out.println("Błąd odczytu pliku: " + e.getMessage());
            }
            return null;
        } finally {
            lock.unlock();
        }
    }
}
