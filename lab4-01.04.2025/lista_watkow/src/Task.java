/**
 * Klasa nadrzedna po ktorej dziedzicza inne klasy ktore dzialaja na osobnych watkach
 * */
public abstract class Task  implements Runnable{

    public abstract Object returnValue();
    @Override
    public abstract void run();
}
