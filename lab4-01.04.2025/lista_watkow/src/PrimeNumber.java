import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PrimeNumber extends Task{
    private int numberOfPrimeNumbers;
    private String filepath;
    private int statusCode;


    /**
     * Funkcja zwraca czy udalo sie obliczyc podaną ilosc liczb pierwszych.
     * @return 1 jezeli sie udalo,-1 jezeli sie nie udało
     * */
    @Override
    public Object returnValue() {
        return this.statusCode;
    }



    public PrimeNumber(int numberOfPrimeNumbers,String filepath){
        this.numberOfPrimeNumbers = numberOfPrimeNumbers;
        this.filepath = filepath;
        this.statusCode = -1;
    }


    /**
     * Fukcja sprawdzajaca czy liczba jest pierwsza
     * @param n - sprawdzana liczba
     *
     * */
    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    /**
     * Funkcja zapisuca liczby pierwsze, parametry sa podane poprzez konstruktor.
     * */
    public void savePrimeNumbers() throws IOException {
        int i = 1;
        int foundPrimeNumbers = 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            while (true) {
                if(Thread.currentThread().isInterrupted()){
                    return;
                }

                if (numberOfPrimeNumbers == foundPrimeNumbers)
                    break;
                if (isPrime(i)) {
                    writer.write(Integer.toString(i));
                    writer.newLine(); // moves to the next line
                    foundPrimeNumbers++;
                }
                i++;
            }
            statusCode=1;
        } // <--- automatically calls writer.close() here!
    }

    @Override
    public void run() {
        try {
            savePrimeNumbers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
