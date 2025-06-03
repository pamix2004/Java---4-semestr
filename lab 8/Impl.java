import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class Impl extends UnicastRemoteObject implements onpInterface {

    private ONP onp; // <- pole klasy

    protected Impl() throws RemoteException {
        super();
        onp = new ONP(); // inicjalizacja pola
    }
    @Override
    public String przeksztalc_onp(String rownanie) {
        return onp.przeksztalcNaOnp(rownanie);
    }
    @Override
    public String oblicz_onp(String rownanie){
        return onp.obliczOnp(rownanie);
    }
}
