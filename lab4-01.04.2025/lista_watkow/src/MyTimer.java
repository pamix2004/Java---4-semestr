public class MyTimer extends Task{
    private int valueToReturn;
    private int numberOfSeconds;

    /**
     * Funkcja zwraca ile czasu odliczal timer, jezeli zostal przerwany to zwraca wartosc zero.
     * */
    @Override
    public Object returnValue() {

        return this.valueToReturn;
    }

    public MyTimer(int numberOfSeconds) {
        this.numberOfSeconds = numberOfSeconds;
        this.valueToReturn = -1;
    }

    /**
     * Funkcja odliczajaca czas i wypisujaca oraz konczaca swoje działanie po określonym w konstruktorze czasie.
     * */
    public void count() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        for (int i = 0; i < this.numberOfSeconds; i++) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            // Wait until 1 second has passed
            while (elapsedTime < (i + 1) * 1000) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                elapsedTime = System.currentTimeMillis() - startTime;
                Thread.yield();
            }

        }


        this.valueToReturn = numberOfSeconds;
        System.out.println("Skonczylem odliczac " + this.numberOfSeconds + " sekund/sekundy");
    }


    @Override
    public void run() {
        try {
            count();
        } catch (InterruptedException e) {
            //Ignorujemy wyjatek wywolany przy wywołaniu przerwania, gdyż pozwalamy na to użytkownikowi
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

}
