import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class ListenerFutureTask extends FutureTask<String>
{
    private final String taskName;

    public ListenerFutureTask(Callable<String> callable, String taskName) {
        super(callable);
        this.taskName = taskName;
    }

    @Override
    protected void done() {
        try {
            String result = get();  // pobranie wyniku
            System.out.println("Zakończono [" + taskName + "]: " + result);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Błąd w [" + taskName + "]: " + e.getMessage());
        }
    }
}