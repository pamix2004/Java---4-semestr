import java.rmi.Remote;
import java.rmi.RemoteException;

public interface onpInterface extends Remote {
    String przeksztalc_onp(String rownanie) throws RemoteException;
    String oblicz_onp(String rownanie) throws RemoteException;
}
