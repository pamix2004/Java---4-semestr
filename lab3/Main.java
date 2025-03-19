public class Main
{
    public static void main(String[] args)
    {
        Set<Vector> vectors = new Set<>(10);
        vectors.dodajElement(new Vector(1, 2, 3));
        vectors.dodajElement(new Vector(3, 4, 5));
        vectors.dodajElement(new Vector(0, 0, 1));
        System.out.println("Vector: " + vectors);

        vectors.usunElement(new Vector(0, 0, 1));
        System.out.println("Vector: " + vectors);

        /* vectors.szukaj(new Vector(0,0,0), false); */

        Set<Rotator> rotators = new Set<>(10);
        rotators.dodajElement(new Rotator(30, 60, 90));
        rotators.dodajElement(new Rotator(0, 0, 180));
        rotators.dodajElement(new Rotator(45, 45, 45));
        System.out.println("Rotator: " + rotators);

        Set<Vector> vectors2 = new Set<>(10);
        vectors2.dodajElement(new Vector(1, 1, 1));
        vectors2.dodajElement(new Vector(4, 4, 4));

        Set<Vector> vertorsPoDodaniu = vectors.dodajElementy(vectors2);
        System.out.println("Nowy vector: " + vertorsPoDodaniu);

        Set<Rotator> rotators2 = new Set<>(10);
        rotators2.dodajElement(new Rotator(30, 60, 90));
        rotators2.dodajElement(new Rotator(10, 20, 30));

        Set<Rotator> rotatorsPoPrzecieciu = rotators.przeciecie(rotators2);
        System.out.println("Nowy rotator: " + rotatorsPoPrzecieciu);
    }
}
