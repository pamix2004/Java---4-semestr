import java.util.*;

public class Kino {
    public static void main(String[] args) {
        Seans seans = new Seans("Despacito 8", "2025-03-20", "18:00", 12, 5, 10);
        seans.wyswietlMiejsca();

        HashMap<Character, ArrayList<Integer>> miejsca = new HashMap<>();
        miejsca.put('A', new ArrayList<>(Arrays.asList(1, 2)));
        miejsca.put('B', new ArrayList<>(Arrays.asList(3)));

        Klient klient = new Klient("Kowalski", "Jan", "jan.kowalski@gmail.com", 123456789, seans, miejsca);

        if (klient.zarezerwujMiejsca()) {
            System.out.println(klient);
        }

        Seans.serializujSeans(seans, "seans.ser");
        Seans seans2 = Seans.deserializujSeans("seans.ser");
        seans2.wyswietlMiejsca();

        Klient.serializujKlient(klient, "klient.ser");
        Klient klient2 = Klient.deserializujKlient("klient.ser");
        System.out.println(klient2);



    }
}