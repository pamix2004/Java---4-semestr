import java.util.*;

public class Kino {
    public static void main(String[] args) {
        Seans seans = new Seans("Despacito 8", "2025-03-20", "18:00", 12, 5, 10);
        System.out.println(seans);
        seans.wyswietlMiejsca();

        HashMap<Character, ArrayList<Integer>> miejsca = new HashMap<>();
        miejsca.put('A', new ArrayList<>(Arrays.asList(1, 2)));
        miejsca.put('B', new ArrayList<>(Arrays.asList(3)));

        Klient klient = new Klient("Kowalski", "Jan", "jan.kowalski@gmail.com", 123456789, seans, miejsca);

        if (klient.zarezerwujMiejsca()) {
            System.out.println(klient);
        }
        seans.wyswietlMiejsca();
        System.out.println();
        System.out.println("Wypisanie obiektów po serializaji i deserializacji:");
        Seans.serializujSeans(seans, "seans.ser");
        Seans seans2 = Seans.deserializujSeans("seans.ser");
        System.out.println(seans2);
        seans2.wyswietlMiejsca();

        Klient.serializujKlient(klient, "klient.ser");
        Klient klient2 = Klient.deserializujKlient("klient.ser");
        System.out.println(klient2);



    }
}