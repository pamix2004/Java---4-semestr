import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TaskManager
{
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final List<Future<String>> tasks = new ArrayList<>();

    public void addTask(Callable<String> task)
    {
        Future<String> future = executor.submit(task);
        tasks.add(future);
        System.out.println("Dodano nowe zadanie. ID: " + (tasks.size() - 1));
    }

    public void showTasks()
    {
        System.out.println("\nLista zadań:");
        for (int i = 0; i < tasks.size(); i++)
        {
            Future<String> future = tasks.get(i);
            if (future.isCancelled())
            {
                System.out.println("[" + i + "] Anulowane");
            }
            else if (future.isDone())
            {
                System.out.println("[" + i + "] Zakończone");
            }
            else
            {
                System.out.println("[" + i + "] W trakcie");
            }
        }
    }

    public void cancelTask(int id)
    {
        if (id >= 0 && id < tasks.size())
        {
            boolean result = tasks.get(id).cancel(true);
            if (result)
            {
                System.out.println("Zadanie " + id + " anulowane.");
            }
            else
            {
                System.out.println("Nie udało się anulować zadania " + id + ".");
            }
        }
        else
        {
            System.out.println("Nieprawidłowe ID zadania.");
        }
    }

    public void getResult(int id)
    {
        if (id >= 0 && id < tasks.size())
        {
            try
            {
                String result = tasks.get(id).get();
                System.out.println("Wynik zadania " + id + ": " + result);
            }
            catch (CancellationException e)
            {
                System.out.println("Zadanie " + id + " zostało anulowane.");
            }
            catch (ExecutionException | InterruptedException e)
            {
                System.out.println("Błąd podczas pobierania wyniku zadania " + id);
            }
        }
        else
        {
            System.out.println("Nieprawidłowe ID zadania.");
        }
    }

    public void shutdown()
    {
        executor.shutdownNow();
        System.out.println("Zakończono działanie.");
    }
}