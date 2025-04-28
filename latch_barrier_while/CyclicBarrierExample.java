import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExample {

    public static void main(String[] args) {

        CyclicBarrierExample example = new CyclicBarrierExample();

        // Bariera, która wywoła zwykłą metodę
        CyclicBarrier barrier = new CyclicBarrier(3, example::barrierAction);

        // Tworzenie wątków normalnie przez obiekty klasy MyWorker
        for (int i = 1; i <= 3; i++) {
            Thread worker = new Thread(new MyWorker(i, barrier));
            worker.start();
        }
    }

    // Normalna metoda, którą wywoła bariera
    private void barrierAction() {
        System.out.println("Wszystkie wątki dotarły do bariery. Kontynuujemy!\n");
    }
}

// Osobna klasa dla "pracowników" (Runnable)
class MyWorker implements Runnable {

    private final int id;
    private final CyclicBarrier barrier;

    public MyWorker(int id, CyclicBarrier barrier) {
        this.id = id;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            System.out.println("Wątek " + id + " wykonuje pracę...");
            Thread.sleep(1000 * id);
            System.out.println("Wątek " + id + " czeka na barierze...");
            barrier.await();
            System.out.println("Wątek " + id + " przeszedł barierę!");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
