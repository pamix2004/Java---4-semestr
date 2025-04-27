import java.util.concurrent.Callable;

public class EquationProcessorCallable implements Callable<String> {
    private final String equation;
    private final ONP onp;

    public EquationProcessorCallable(String equation) {
        this.equation = equation;
        this.onp = new ONP(); //każde przekształcenie ma swój własny obiekt by móc przekształcać współbieżnie
    }

    @Override
    public String call() {
        System.out.println("Przetwarzam równanie: " + equation);
        String rownanieOnp = onp.przeksztalcNaOnp(equation);
        String wynik = onp.obliczOnp(rownanieOnp);
        return wynik;
    }
}