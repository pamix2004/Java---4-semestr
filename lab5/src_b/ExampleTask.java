import java.util.concurrent.*;

class ExampleTask implements Callable<String>
{
    private final String name;

    public ExampleTask(String name) {
        this.name = name;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep((long) (Math.random() * 3000));
        return "Wynik zadania: " + name;
    }
}