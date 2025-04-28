public class WhileExample {

    private boolean ready = false; // stan, który wątek sprawdza

    public synchronized void waitForReady() {
        System.out.println(Thread.currentThread().getName() + " czeka...");

        while (!ready) { // pętla while chroni
            try {
                wait(); // czekamy
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(Thread.currentThread().getName() + " wykrył, że ready == true, i rusza dalej!");
    }

    public synchronized void makeReady() {
        ready = true;
        System.out.println(Thread.currentThread().getName() + " ustawia ready = true i budzi wszystkich.");
        notifyAll(); // budzimy wszystkie wątki czekające na this
    }

    public static void main(String[] args) {
        WhileExample example = new WhileExample();

        // Tworzymy wątek, który będzie czekał
        Thread waitingThread = new Thread(example::waitForReady, "Watek-czekajacy");
        Thread waitingThread2 = new Thread(example::waitForReady, "Watek-czekajacy");
        waitingThread.start();
        waitingThread2.start();

        // Mała pauza, żeby wątek zdążył wejść w stan czekania
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Tworzymy wątek, który ustawi ready = true
        Thread notifyingThread = new Thread(example::makeReady, "Watek-ustawiajacy");
        notifyingThread.start();
    }
}
