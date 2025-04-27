import java.util.ArrayList;

/**
 * ennum pokazujący jakies są dozwoleone taski na wątkach
 * */
enum availableTasks{
    fibonacci,
    prime,
    timer
}

public class TaskManager {
    private ArrayList<Thread>thread_list;
    private ArrayList<availableTasks> taskNameList;
    private ArrayList<Task> taskList;


    public TaskManager() {
        this.thread_list = new ArrayList<>();
        this.taskNameList = new ArrayList<>();
        this.taskList = new ArrayList<>();
    }


    /**
     * Funkcja wyswietla obslugiwane watki wraz z ich stanami
     * */
    public void displayAllStates(){
        for(int i =0;i<thread_list.size();i++){
            System.out.println(i+1+". "+taskNameList.get(i)+": "+thread_list.get(i).getState());
        }
    }


    /**
     * Funkcja wyswietla wynik dla obslugiwanego watku, jezeli watek nie zostal zakonczony to wyswietla stosowną wiadomosc, tak samo w przypadku gdy watek został przerwany, wtedy
     * wypisujemy wiadomosc informujaca uzytkownika o tym ze watek zostal anulowany.
     * @param i index wątku z listy
     * */
    public void displayResults(int i){
        if(thread_list.get(i).getState()== Thread.State.RUNNABLE){
             System.out.println("Nie mozna wyswietlic wyniku gdyz watek wciaz dziala");
        }
        else if(thread_list.get(i).getState()== Thread.State.TERMINATED){
            if(!thread_list.get(i).isInterrupted())
                System.out.println("Zwrocony wynik: "+taskList.get(i).returnValue());
            else{
                System.out.println("Watek zostal anulowany zatem nie zwracam wyniku");
            }
        }



    }


    /**
     * Funkcja dodaje watek do odpowiednich list.
     * @param taskToRun typ enum informujacy jakie zadanie chcemy przechowywac
     * @param thread - obiekt typu <code>thread</code>
     * @param task - obiekt typu <code>task</code>
     * */
    public void addTaskAndThread(availableTasks taskToRun, Thread thread,Task task) {
        switch(taskToRun){
            case fibonacci:
            case timer:
            case prime:
                this.taskNameList.add(taskToRun);
                this.thread_list.add(thread);
                this.taskList.add(task);
                break;
            default:
                System.out.println("Nieznane zadanie!");
                break;
        }
    }


    /**
     * Funkcja anuluje watek o podanym indeksie
     * */
    public void cancelTask(int i) {
        if(thread_list.get(i).getState()== Thread.State.RUNNABLE || thread_list.get(i).getState()==Thread.State.TIMED_WAITING){
            thread_list.get(i).interrupt();
        }
        else{
            throw new RuntimeException("Zadanie bylo juz przerwane!");
        }

    }
}
