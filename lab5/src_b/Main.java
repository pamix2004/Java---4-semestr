import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main
{
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 5; i++) {
            String taskName = "Zadanie-" + i;
            ListenerFutureTask futureTask = new ListenerFutureTask(new ExampleTask(taskName), taskName);
            executor.execute(futureTask);
        }

        executor.shutdown();
    }
}
