import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Seans implements Serializable {
    private String tytul;
    private String dzien;
    private String godzina;
    private int ograniczenia_wiekowe;
    private HashMap<Character, HashMap<Integer, Boolean>> liczba_miejsc;

    public Seans(String tytul, String dzien, String godzina, int ograniczenia_wiekowe, int liczbaRzedow, int liczbaMiejsc) {
        this.tytul = tytul;
        this.dzien = dzien;
        this.godzina = godzina;
        this.ograniczenia_wiekowe = ograniczenia_wiekowe;
        this.liczba_miejsc = new HashMap<>();

        for (char rzad = 'A'; rzad < 'A' + liczbaRzedow; rzad++) {
            HashMap<Integer, Boolean> rzadMiejsc = new HashMap<>();
            for (int miejsce = 1; miejsce <= liczbaMiejsc; miejsce++) {
                rzadMiejsc.put(miejsce, false);
            }
            liczba_miejsc.put(rzad, rzadMiejsc);
        }
    }

    public String getTytul() {return tytul;}
    public String getDzien() {return dzien;}
    public String getGodzina(){return godzina;}
    public int getOgraniczenia_wiekowe(){return ograniczenia_wiekowe;}
    public HashMap<Character, HashMap<Integer, Boolean>> getLiczba_miejsc(){return liczba_miejsc;}




    public boolean rezerwujMiejsca(HashMap<Character, ArrayList<Integer>> listaMiejsc) {
        for (Map.Entry<Character, ArrayList<Integer>> entry : listaMiejsc.entrySet()) {
            char rzad = entry.getKey();
            for (int numer : entry.getValue()) {
                if (!liczba_miejsc.containsKey(rzad) || !liczba_miejsc.get(rzad).containsKey(numer) || liczba_miejsc.get(rzad).get(numer)) {
                    System.out.println("Miejsce " + rzad + numer + " jest już zajęte lub nie istnieje.");
                    return false;
                }
            }
        }

        for (Map.Entry<Character, ArrayList<Integer>> entry : listaMiejsc.entrySet()) {
            char rzad = entry.getKey();
            for (int numer : entry.getValue()) {
                liczba_miejsc.get(rzad).put(numer, true);
            }
        }
        System.out.println("Rezerwacja zakończona pomyślnie!");
        return true;
    }


    public void wyswietlMiejsca() {
        for (char rzad : liczba_miejsc.keySet()) {
            System.out.print(rzad + ": ");
            for (Map.Entry<Integer, Boolean> entry : liczba_miejsc.get(rzad).entrySet()) {
                System.out.print((entry.getValue() ? "[X]" : "[ ]") + " ");
            }
            System.out.println();
        }
    }

    @Override
    public String toString()
    {
        return "Tytul: " + tytul + " Data i Godzina: " + dzien + " " + godzina + " Ograniczenia wiekowe: " + ograniczenia_wiekowe;
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
