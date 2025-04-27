import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //Tworzymy scanner do pobierania danych
        Scanner scanner = new Scanner(System.in);
        /**
         *Flaga słuzaca do obslugi menu
         */
        boolean running = true;

        /**Obiekt ktory zarzadza zasobami*/
        TaskManager taskManager = new TaskManager();




        //Petla do menu glownego
        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Dodaj zadanie");
            System.out.println("2. Wyświetl status wszystkich zadań");
            System.out.println("3. Anuluj zadanie");
            System.out.println("4. Pokaż wynik zadania");
            System.out.println("5. Wyjście");
            System.out.print("Wybierz opcję: ");

            //Pobierzmy od uzytkownika co wybieramy w menu
            int choice = scanner.nextInt();

            try {
                switch (choice) {
                    case 1:
                        //Wyswietl liste dostepnych zadan i pobierz od uzytkownika co wybiera
                        System.out.println("Dodaj zadanie,wybierz jakie zadanie chcesz dodac.");
                        System.out.println("1.Fibonacci");
                        System.out.println("2.Timer");
                        System.out.println("3. Find prime numbers");
                        int userInputInteger = scanner.nextInt();
                        System.out.println();

                        Task newTask;
                        Thread thread;
                        switch (userInputInteger) {
                            case 1:
                                System.out.println("Podaj ile liczb fibonacciego nalezy obliczyc: ");
                                userInputInteger = scanner.nextInt();
                                
                                
                                newTask = new Fibonacci(userInputInteger);
                                thread = new Thread(newTask);
                                thread.start();
                                taskManager.addTaskAndThread(availableTasks.fibonacci, thread, newTask);

                                break;

                            case 2:
                                System.out.println("Podaj po ilu sekundach timer ma zakonczyc dzialanie: ");
                                userInputInteger = scanner.nextInt();
                                newTask = new MyTimer(userInputInteger);
                                thread = new Thread(newTask);
                                thread.start();
                                taskManager.addTaskAndThread(availableTasks.timer, thread, newTask);

                                break;

                            case 3:
                                System.out.println("Podaj ile liczby pierwszych ma znalezc funkcja");
                                userInputInteger = scanner.nextInt();
                                scanner.nextLine();

                                System.out.println("Podaj nazwe pliku do ktorego nalezy zapisac liczby");

                                String userInputString = scanner.nextLine();


                                newTask = new PrimeNumber(userInputInteger,userInputString);
                                thread = new Thread(newTask);
                                thread.start();

                                taskManager.addTaskAndThread(availableTasks.prime, thread,newTask);

                                break;

                            default:
                                System.out.println("Nie ma takiej opcji!");
                                break;
                        }

                        break;
                        //Wyswietl wszystkie zadania wraz ze statusami
                    case 2:
                        System.out.println("Status wszystkich zadań");
                        taskManager.displayAllStates();

                        break;
                    //Wyswietl liste zadan i pozwol uzytkownikowi wybrac ktore nalezy anulowac
                        case 3:
                        System.out.println("Lista zadań");
                        taskManager.displayAllStates();
                        userInputInteger = scanner.nextInt();
                        taskManager.cancelTask(userInputInteger-1);

                        break;
                        //wyswietl liste zadan i pozwol uzytkownikowi pokazac wyniki
                    case 4:
                        System.out.println("Lista zadań:");
                        taskManager.displayAllStates();
                        userInputInteger = scanner.nextInt();

                        taskManager.displayResults(userInputInteger-1);


                        break;
                        //Wyjdz z programu
                    case 5:
                        System.out.println("Wyjście z programu.");
                        running = false;
                        break;
                    default:
                        System.out.println("Niepoprawna opcja. Spróbuj ponownie.");
                }
            } catch (IndexOutOfBoundsException e){
                System.out.println("--------");
                System.out.println("Niepoprawna wartosc!");
                System.out.println("--------");

            }
            catch(Exception e){
                System.out.println("Wystapil niespodziewany blad,"+e.getMessage());
            }
        }



        scanner.close();
    }
}
