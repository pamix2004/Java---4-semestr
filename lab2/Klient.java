import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Klient implements Serializable {
    private String nazwisko;
    private String imie;
    private String mail;
    private int telefon;
    private Seans seans;
    private HashMap<Character, ArrayList<Integer>> miejsca;

    public Klient(String nazwisko, String imie, String mail, int telefon, Seans seans, HashMap<Character, ArrayList<Integer>> miejsca) {
        this.nazwisko = nazwisko;
        this.imie = imie;
        this.mail = mail;
        this.telefon = telefon;
        this.seans = seans;
        this.miejsca = miejsca;
    }

    public void updateMiejsca(HashMap<Character, ArrayList<Integer>> nowe_miejsca) {
        miejsca = nowe_miejsca;
    }

    public boolean zarezerwujMiejsca() {
        return seans.rezerwujMiejsca(miejsca);
    }

    @Override
    public String toString() {
        return "Klient: " + imie + " " + nazwisko + " (" + mail + ", " + telefon + ")\nSeans: " + seans.getTytul() + "\nMiejsca: " + miejsca;
    }

    public static void serializujKlient(Klient klient, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(klient);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Klient deserializujKlient(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Klient) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            {
                System.err.println("Error during deserialization: ");

            }
        }
        return null;
    }
}
