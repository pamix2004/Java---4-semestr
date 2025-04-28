import java.util.concurrent.CountDownLatch;

class Worker implements Runnable{
    private static int global_id=1;
    public int workerId;
    private int workTime;
    private static CountDownLatch latchField;
    public Worker(int workTime,CountDownLatch latch){
        this.workerId = global_id;
        global_id++;
        this.workTime = workTime;
        latchField = latch;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(workTime);
            System.out.println("Pracownik o ID:"+workerId+" zakończył swoja prace.");
            latchField.countDown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

public class Latch {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        Worker worker1 = new Worker(3000,latch);
        Thread thread1 = new Thread(worker1);
        thread1.start();

        Worker worker2 = new Worker(5000,latch);
        Thread thread2 = new Thread(worker2);
        thread2.start();

        Worker worker3 = new Worker(2000,latch);
        Thread thread3 = new Thread(worker3);
        thread3.start();

        latch.await();

        System.out.println("Wszyscy pracownicy zakonczyli swoją pracę!");


    }
}
