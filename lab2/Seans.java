import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Seans implements Serializable {
    private String tytul;
    private String dzien;
    private String godzina;
    private int ograniczenia_wiekowe;
    private HashMap<Character, HashMap<Integer, Boolean>> miejsca;

    public Seans(String tytul, String dzien, String godzina, int ograniczenia_wiekowe, int liczbaRzedow, int liczbaMiejsc) {
        this.tytul = tytul;
        this.dzien = dzien;
        this.godzina = godzina;
        this.ograniczenia_wiekowe = ograniczenia_wiekowe;
        this.miejsca = new HashMap<>();

        for (char rzad = 'A'; rzad < 'A' + liczbaRzedow; rzad++) {
            HashMap<Integer, Boolean> rzadMiejsc = new HashMap<>();
            for (int miejsce = 1; miejsce <= liczbaMiejsc; miejsce++) {
                rzadMiejsc.put(miejsce, false);
            }
            miejsca.put(rzad, rzadMiejsc);
        }
    }

    public boolean rezerwujMiejsca(HashMap<Character, ArrayList<Integer>> listaMiejsc) {
        for (Map.Entry<Character, ArrayList<Integer>> entry : listaMiejsc.entrySet()) {
            char rzad = entry.getKey();
            for (int numer : entry.getValue()) {
                if (!miejsca.containsKey(rzad) || !miejsca.get(rzad).containsKey(numer) || miejsca.get(rzad).get(numer)) {
                    System.out.println("Miejsce " + rzad + numer + " jest już zajęte lub nie istnieje.");
                    return false;
                }
            }
        }

        for (Map.Entry<Character, ArrayList<Integer>> entry : listaMiejsc.entrySet()) {
            char rzad = entry.getKey();
            for (int numer : entry.getValue()) {
                miejsca.get(rzad).put(numer, true);
            }
        }
        System.out.println("Rezerwacja zakończona pomyślnie!");
        return true;
    }

    public String getTytul() {
        return tytul;
    }

    public void wyswietlMiejsca() {
        for (char rzad : miejsca.keySet()) {
            System.out.print(rzad + ": ");
            for (Map.Entry<Integer, Boolean> entry : miejsca.get(rzad).entrySet()) {
                System.out.print((entry.getValue() ? "[X]" : "[ ]") + " ");
            }
            System.out.println();
        }
    }

    public static void serializujSeans(Seans seans, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(seans);
        } catch (IOException e) {
            System.err.println("BŁĄD: " + e.getMessage());
        }
    }

    public static Seans deserializujSeans(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Seans) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            {
                System.err.println("Error during deserialization: ");

            }
        }
        return null;
    }
}
