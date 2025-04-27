/**
 * Klasa stworzona do obliczania ciągu Fibonacciego, sposób w który to liczy nie jest uznawany za optymalny jednak naszym celem jest pokazanie
 * działania wątków i tutaj nadaje się idealnie z uwagi na dłuższy czas obliczeń.
 * */
public class Fibonacci extends Task {

    private int numberOfFibonacciNumbers;
    private  long valueToReturn=-1;

    /**
     * Funkcja zwraca n-tą liczbę ciągu Fibonacciego. Jezeli zadanie nie zostalo dokonczone powinno zwrocic -1
     * */
    public Object returnValue() {
        return this.valueToReturn;
    }

    public Fibonacci(int n){
        this.numberOfFibonacciNumbers=n;
        this.valueToReturn = -1;
    }

    /**
     * Funkcja oblicza n-tą liczbe ciągu fibonacciego poprzez rekursję
     * @param n n-ta liczba Fibonacciego
     * @return Funkcja zwraca long
     * */
    public long fib(int n){
        //Gdy watek zostanie przerwany to wychodzmy z rekursji
        if(Thread.currentThread().isInterrupted())
        {
            return -1;
        }

        if(n==1||n==2)
            return 1;
        else
            return fib(n-1)+fib(n-2);
    }

    /**
     * Nadpisana funkcja run potrzebna do działania na osobnym wątku
     * */
    @Override
    public void run() {
        this.valueToReturn=fib(numberOfFibonacciNumbers);
    }
}
