import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            onpInterface stub = (onpInterface) registry.lookup("ONPService");

            Scanner sc = new Scanner(System.in);
            while(true) {
                System.out.print("podaj rownanie: ");
                String rownanie = sc.nextLine();
                String przeksztalcone = stub.przeksztalc_onp(rownanie);
                System.out.println("przekształcone równanie: " + przeksztalcone);
                String obliczone = stub.oblicz_onp(przeksztalcone);
                System.out.println("obliczone równanie: " + obliczone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
