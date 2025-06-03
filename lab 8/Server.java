import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            Impl obj = new Impl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ONPService", obj);
            System.out.println("Serwer gotowy.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
