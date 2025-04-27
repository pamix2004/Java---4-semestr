import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;

public class EquationProcessorTask extends FutureTask<String> {
    private final String equation;
    private final String filePath;
    private final Lock lock;

    public EquationProcessorTask(Callable<String> callable, String equation, String filePath, Lock lock) {
        super(callable); //opakowanie callable metody
        this.equation = equation;
        this.filePath = filePath;
        this.lock = lock;
    }
    //uruchamia się gdy callable task kończy pracę
    @Override
    protected void done() {
        lock.lock();
        try {
            try {
                // Pobierz wynik obliczenia z Callable
                String wynik = get();


                // Wczytaj wszystkie linie do pamięci
                List<String> lines = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();

                // Aktualizuj odpowiednią linię
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).trim().equals(equation.trim())) {
                        lines.set(i, equation + " " + wynik);
                        break;
                    }
                }

                // Nadpisz cały plik
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false));
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
                writer.close();

                System.out.println("Dopisano wynik: " + wynik + " do równania: " + equation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
    }
}
