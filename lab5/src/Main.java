import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        TaskManager manager = new TaskManager();
        Scanner scanner = new Scanner(System.in);

        /* Lambdas */
        manager.addTask(() ->
        {
            Thread.sleep(6000);
            return "Zadanie 1 zakończone!";
        });

        manager.addTask(() ->
        {
            Thread.sleep(10000);
            return "Zadanie 2 zakończone!";
        });

        manager.addTask(() ->
        {
            Thread.sleep(4000);
            return "Zadanie 3 zakończone!";
        });

        while (true)
        {
            System.out.println("1. Pokaż zadania");
            System.out.println("2. Anuluj zadanie");
            System.out.println("3. Pokaż wynik zadania");
            System.out.println("4. Wyjście");

            System.out.print("Wybierz opcję: ");
            int option = scanner.nextInt();

            switch (option)
            {
                case 1:
                    manager.showTasks();
                    break;
                case 2:
                    System.out.print("Podaj ID zadania do anulowania: ");
                    int idToCancel = scanner.nextInt();
                    manager.cancelTask(idToCancel);
                    break;
                case 3:
                    System.out.print("Podaj ID zadania do pokazania wyniku: ");
                    int idToGetResult = scanner.nextInt();
                    manager.getResult(idToGetResult);
                    break;
                case 4:
                    manager.shutdown();
                    return;
                default:
                    System.out.println("Nieprawidłowa opcja.");
            }
        }
    }
}